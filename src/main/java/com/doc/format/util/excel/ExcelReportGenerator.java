package com.doc.format.util.excel;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/11 16:28
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class ExcelReportGenerator {

    public static void main(String[] args) {
        String jsonFilePath = "/Users/houhao/Downloads/报表清单/report_templates.json";  // JSON 文件路径
        String outputFolder = "/Users/houhao/Downloads/报表清单/上海报表测试文件夹/"; // 输出 Excel 文件夹路径

        makeExcel(jsonFilePath, outputFolder);
    }

    public static void makeExcel(String jsonFilePath, String outputFolder) {
        try {
            // 读取并解析 JSON 文件
            List<ReportTemplate> reportTemplates = parseJsonFile(jsonFilePath);

            // 根据解析出的数据生成多个文件
            for (ReportTemplate reportTemplate : reportTemplates) {
                String reportName = reportTemplate.getName();  // 从模板中获取报表名称
                if (reportTemplate.getFields() == null) {
                    continue;
                }
                if (reportTemplate.getFields() == null) {
                    continue;
                }
                // 将字段信息转换为 Map 列表，符合原有方法的格式
                List<Map<String, String>> fields = convertFieldsToMap(reportTemplate.getFields());

                String fileName = outputFolder + reportName + ".xlsx";

                // 生成每个 Excel 文件
                createExcelFile(fileName, reportName, fields);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Map<String, String>> convertFieldsToMap(List<Field> fields) {
        List<Map<String, String>> list = new ArrayList<>();
        for (Field field : fields) {
            if (field.getFieldName() != null) {
                Map<String, String> name = Map.of(
                        "name", field.getFieldName() != null ? field.getFieldName() : "",  // 如果为null，替换为空字符串
                        "comment", field.getTableName() != null ? field.getTableName() : "",  // 同样处理tableName为null的情况
                        "fieldType", field.getFieldType() != null ? field.getFieldType() : ""  // 同样处理fieldType为null的情况
                );
                list.add(name);
            }
        }
        return list;
    }

    // 解析 JSON 文件的方法
    private static List<ReportTemplate> parseJsonFile(String jsonFilePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(jsonFilePath)) {
            // 定义 JSON 文件的类型
            Type reportTemplateListType = new TypeToken<List<ReportTemplate>>() {
            }.getType();
            return gson.fromJson(reader, reportTemplateListType);
        }
    }


    // 生成Excel文件并动态添加注释
    private static void createExcelFile(String outputFilePath, String reportName, List<Map<String, String>> fields) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(reportName); // 每个文件都有一个以报表名称命名的Sheet

        // 动态计算 lastCell 的列名 (根据字段数动态设置最后一列)
        String lastColumn = getExcelColumnName(fields.size());

        // 在A1单元格添加备注
        addCommentToCell("A1", "jx:area(lastCell=\"Z80\")", sheet);

        // 在A2单元格添加备注，lastCell 动态变化
        addCommentToCell("A2", "jx:each(items=\"items\" var=\"table\" lastCell=\"" + lastColumn + "2\")", sheet);

        // 创建表头行 (第一行)
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < fields.size(); i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(fields.get(i).get("comment"));

            // 设置每列的宽度为20个字符宽度
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 创建内容行（带占位符）(第二行)
        Row contentRow = sheet.createRow(1);
        for (int i = 0; i < fields.size(); i++) {
            Cell contentCell = contentRow.createCell(i);
            contentCell.setCellValue("${table." + fields.get(i).get("name") + "}");
        }

        // 保存文件
        FileOutputStream fileOut = new FileOutputStream(outputFilePath);
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();

        System.out.println("Generated Excel file: " + outputFilePath);
    }

    // 为单元格添加注释
    public static void addCommentToCell(String cellReference, String commentText, Sheet sheet) throws IOException {
        CellReference cellRef = new CellReference(cellReference);
        int rowIndex = cellRef.getRow();
        int colIndex = cellRef.getCol();

        Row row = sheet.getRow(rowIndex);
        if (row == null) {
            row = sheet.createRow(rowIndex);
        }

        Cell cell = row.getCell(colIndex);
        if (cell == null) {
            cell = row.createCell(colIndex);
        }

        // 创建绘图对象
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        // 创建锚点
        ClientAnchor anchor = new XSSFClientAnchor();
        anchor.setCol1(cell.getColumnIndex());
        // 设置备注覆盖的单元格范围
        anchor.setCol2(cell.getColumnIndex() + 2);
        anchor.setRow1(row.getRowNum());
        anchor.setRow2(row.getRowNum() + 3);

        // 创建备注
        Comment comment = drawing.createCellComment(anchor);
        comment.setString(new XSSFRichTextString(commentText));

        // 设置备注作者
        comment.setAuthor("Author");

        // 将备注添加到单元格
        cell.setCellComment(comment);
    }

    // 解析汇总sheet，返回报表名称与字段结构信息的映射
    private static Map<String, List<Map<String, String>>> parseSummarySheet(Sheet sheet) {
        Map<String, List<Map<String, String>>> reportToFieldsMap = new HashMap<>();
        int rowIndex = 0;
        for (Row row : sheet) {
            rowIndex++;
            if (rowIndex < 2) {
                // 跳过第一行
                continue;
            }

            Cell reportNameCell = row.getCell(5); // 假设报表名称是第6列
            if (reportNameCell != null) {
                String reportName = reportNameCell.getStringCellValue();
                Sheet structureSheet = sheet.getWorkbook().getSheet(reportName); // 假设每个报表有一个对应的表结构sheet

                // 解析表结构
                List<Map<String, String>> fields = parseStructureSheet(structureSheet);
                reportToFieldsMap.put(reportName, fields);
            }
        }
        return reportToFieldsMap;
    }

    // 解析表结构sheet，返回字段信息列表
    private static List<Map<String, String>> parseStructureSheet(Sheet sheet) {
        List<Map<String, String>> fields = new ArrayList<>();
        int rowIndex = 0;
        for (Row row : sheet) {
            rowIndex++;
            if (rowIndex < 5) {
                // 跳过前4行
                continue;
            }

            // 假设字段名称是第1列
            Cell fieldNameCell = row.getCell(0);
            // 假设字段类型是第2列
            Cell fieldTypeCell = row.getCell(1);
            // 假设字段描述是第3列
            Cell fieldDescCell = row.getCell(2);

            if (fieldNameCell != null && fieldTypeCell != null && fieldDescCell != null) {
                Map<String, String> field = new HashMap<>();
                field.put("name", fieldNameCell.getStringCellValue());
                field.put("dataType", fieldTypeCell.getStringCellValue());
                field.put("comment", fieldDescCell.getStringCellValue());
                field.put("dataTypeName", fieldTypeCell.getStringCellValue().equals("String") ? "字符串" : "整型");
                fields.add(field);
            }
        }
        return fields;
    }

    // 根据字段数量计算最后一列的列名（A, B, ..., Z, AA, AB, ...）
    private static String getExcelColumnName(int columnNumber) {
        StringBuilder columnName = new StringBuilder();
        while (columnNumber >= 0) {
            columnName.insert(0, (char) ('A' + (columnNumber % 26)));
            columnNumber = (columnNumber / 26) - 1;
        }
        return columnName.toString();
    }
}