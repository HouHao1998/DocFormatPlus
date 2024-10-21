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
 * @date 2024/9/11 14:53
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.sourceforge.pinyin4j.PinyinHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class ExcelToExcel {


    public static void main(String[] args) {
        String excelFilePath = "/Users/houhao/Downloads/报表清单/绕行访问涉敏后台资源审计.xlsx";  // Excel文件路径
        String folderPath = "/Users/houhao/Downloads/报表清单/";  // Excel文件路径
        String jsonFilePath = "/Users/houhao/Downloads/报表清单/report_templates.json";  // JSON文件路径
        String sqlFilePath = "/Users/houhao/Downloads/报表清单/settingsDataSource.txt";  // SQL脚本文件路径
        String groupSn = "9902360002";
        String groupName = "审计报表数据源";
        long snCounter = 9902240903370004L;


        settingsDataSourceForFolder(folderPath, jsonFilePath, sqlFilePath, snCounter, groupSn, groupName);

    }

    // 获取文件名并去掉后缀的方法
    private static String getFileNameWithoutExtension(String filePath) {
        File file = new File(filePath);
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(0, lastDotIndex);
        }
        return fileName;
    }


    public static void settingsDataSourceForFolder(String folderPath, String jsonFilePath, String sqlFilePath, long snCounter, String groupSn, String groupName) {
        List<ReportTemplate> reportTemplates = new ArrayList<>();
        File folder = new File(folderPath);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".xlsx") || name.endsWith(".xls"));

        if (files != null && files.length > 0) {
            try (BufferedWriter sqlWriter = new BufferedWriter(new FileWriter(sqlFilePath))) {

                for (File file : files) {
                    String excelFilePath = file.getAbsolutePath();

                    // 获取文件名称并去掉后缀
                    String fileNameWithoutExtension = getFileNameWithoutExtension(excelFilePath);

                    FileInputStream fileInputStream = new FileInputStream(excelFilePath);
                    Workbook workbook = new XSSFWorkbook(fileInputStream);


                    Sheet detailSheet = workbook.getSheet("明细表");

                    LinkedList<Field> detailFields = new LinkedList<>();
                    LinkedList<Field> genFields = new LinkedList<>();


                    // 解析明细表的字段信息，同时区分是否为数据生成字段
                    parseDetailAndGenFields(detailSheet, detailFields, genFields);
                    // 创建ReportTemplate并设置属性
                    ReportTemplate reportTemplate = new ReportTemplate();

                    reportTemplate.setName(fileNameWithoutExtension);
                    reportTemplate.setCode(chineseToPinyin(fileNameWithoutExtension));
                    // 生成SQL并创建ReportTemplate
                    ReportDataSource reportDataSource = new ReportDataSource();
                    reportDataSource.setDataSourceSn(snCounter);

                    reportDataSource.setFields(detailFields);


                    reportDataSource.setDataSourceSn(snCounter);


                    reportDataSource.setFields(detailFields);
                    reportTemplate.setDetailDataSource(reportDataSource);
                    LinkedList<Field> exportableFields = detailFields.stream()
                            .filter(field -> Boolean.TRUE.equals(field.getExportable()))  // 使用 Boolean.TRUE 来直接判断是否为 true
                            .collect(Collectors.toCollection(LinkedList::new));
                    reportTemplate.setFields(exportableFields);

                    // 生成 SQL 语句
                    String sql = "";
                    System.out.println(sql);  // 打印生成的 SQL
                    snCounter++;
                    reportTemplates.add(reportTemplate);


                    workbook.close();
                    fileInputStream.close();
                }

                // 保存报表模版到JSON文件
                saveReportTemplatesToJson(reportTemplates, jsonFilePath);

                System.out.println("SQL脚本已保存到: " + sqlFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件夹为空或没有找到Excel文件！");
        }
    }

    // 解析 数据源SQL sheet
    private static List<DataSourceSQL> parseDataSourceSQLSheet(Sheet sheet) {
        List<DataSourceSQL> dataSourceSQLList = new ArrayList<>();
        int rowIndex = 0;
        for (Row row : sheet) {
            rowIndex++;
            if (rowIndex < 2) {
                // 跳过标题行
                continue;
            }

            // 读取数据源SQL表格的列信息
            Cell dataSourceNameCell = row.getCell(0);   // 数据源名称
            Cell sqlExpressionCell = row.getCell(1);    // SQL表达式
            Cell optionalFieldCell = row.getCell(2);    // 选填字段

            if (dataSourceNameCell != null && sqlExpressionCell != null) {
                String dataSourceName = dataSourceNameCell.getStringCellValue();
                String sqlExpression = sqlExpressionCell.getStringCellValue();
                String optionalField = optionalFieldCell != null ? optionalFieldCell.getStringCellValue() : null;

                // 将读取到的数据封装到DataSourceSQL对象中
                dataSourceSQLList.add(new DataSourceSQL(dataSourceName, sqlExpression, optionalField));
            }
        }
        return dataSourceSQLList;
    }

    // 解析统计表字段
    private static void parseStatFields(Sheet statSheet, List<Field> statFields) {
        int rowIndex = 0;
        for (Row row : statSheet) {
            rowIndex++;
            if (rowIndex < 2) {
                // 跳过标题行
                continue;
            }
            Cell fieldNameCell = row.getCell(1);  // 表字段名
            // 创建新的 Field 对象
            if (fieldNameCell != null && StringUtils.isNotBlank(fieldNameCell.getStringCellValue())) {
                // 获取字段信息并逐个设置
                Field field = new Field();
                if (fieldNameCell != null) {
                    field.setFieldName(fieldNameCell.getStringCellValue());

                }

                Cell tableNameCell = row.getCell(0);       // 名称（中文表头）
                if (tableNameCell != null) {
                    field.setTableName(tableNameCell.getStringCellValue());

                }

                Cell attributeCell = row.getCell(2);       // 属性名
                if (attributeCell != null) {
                    field.setAttributeName(attributeCell.getStringCellValue());
                }

                Cell fieldTypeCell = row.getCell(3);       // 字段类型
                if (fieldTypeCell != null) {
                    field.setFieldType(fieldTypeCell.getStringCellValue());
                }

                Cell supportDrillCell = row.getCell(4);    // 支持钻取
                if (supportDrillCell != null && "是".equals(supportDrillCell.getStringCellValue())) {
                    field.setSupportDrill(true);
                }

                Cell drillTypeCell = row.getCell(5);       // 钻取类型
                if (drillTypeCell != null) {
                    field.setDrillType(drillTypeCell.getStringCellValue());
                }

                Cell displayModeCell = row.getCell(6);     // 显示方式
                if (displayModeCell != null) {
                    field.setDisplayMode(displayModeCell.getStringCellValue());
                }


                Cell drillOptionalParamCell = row.getCell(8); // 钻取选填参数配置
                if (drillOptionalParamCell != null) {
                    field.setDrillOptionalParam(drillOptionalParamCell.getStringCellValue());
                }
                // 将解析后的 Field 对象添加到列表
                statFields.add(field);
            }

        }
    }


    // 解析明细表字段，同时区分生成字段和普通字段
    private static void parseDetailAndGenFields(Sheet detailSheet, List<Field> detailFields, List<Field> genFields) {
        // 创建一个映射来保存表头名称对应的列号
        Map<String, Integer> headerMap = new HashMap<>();

        // 读取第一行表头，找到每个表头对应的列号
        Row headerRow = detailSheet.getRow(0);
        for (Cell cell : headerRow) {
            String header = cell.getStringCellValue().trim();
            switch (header) {
                case "名称":
                case "表字段名":
                case "属性名":
                case "任务派发":
                case "审计信息字段":
                case "可导出":
                case "数据生成字段":
                    headerMap.put(header, cell.getColumnIndex());
                    break;
                default:
                    break;
            }
        }

        // 从第二行开始遍历数据行
        for (int rowIndex = 1; rowIndex <= detailSheet.getLastRowNum(); rowIndex++) {
            Row row = detailSheet.getRow(rowIndex);
            if (row == null) {
                continue; // 如果某行为空，跳过
            }

            // 读取字段信息，创建新的 Field 对象
            Field field = new Field();

            // 根据表头映射获取相应的列号，再读取单元格的值
            if (headerMap.containsKey("表字段名")) {
                Cell fieldNameCell = row.getCell(headerMap.get("表字段名"));
                if (fieldNameCell != null && StringUtils.isNotBlank(fieldNameCell.getStringCellValue())) {
                    field.setFieldName(fieldNameCell.getStringCellValue());
                }
            }

            if (headerMap.containsKey("名称")) {
                Cell tableNameCell = row.getCell(headerMap.get("名称"));
                if (tableNameCell != null) {
                    field.setTableName(tableNameCell.getStringCellValue());
                }
            }

            if (headerMap.containsKey("属性名")) {
                Cell attributeCell = row.getCell(headerMap.get("属性名"));
                if (attributeCell != null) {
                    field.setAttributeName(attributeCell.getStringCellValue());
                }
            }

            if (headerMap.containsKey("任务派发")) {
                Cell taskDispatchCell = row.getCell(headerMap.get("任务派发"));
                if (taskDispatchCell != null && "是".equals(taskDispatchCell.getStringCellValue())) {
                    field.setTaskDispatch(true);
                }
            }

            if (headerMap.containsKey("审计信息字段")) {
                Cell auditFieldCell = row.getCell(headerMap.get("审计信息字段"));
                if (auditFieldCell != null && "是".equals(auditFieldCell.getStringCellValue())) {
                    field.setAuditField(true);
                }
            }

            if (headerMap.containsKey("可导出")) {
                Cell exportableCell = row.getCell(headerMap.get("可导出"));
                if (exportableCell != null && "是".equals(exportableCell.getStringCellValue())) {
                    field.setExportable(true);
                }
            }

            if (headerMap.containsKey("数据生成字段")) {
                Cell dataGenerationFieldCell = row.getCell(headerMap.get("数据生成字段"));
                if (dataGenerationFieldCell != null && "是".equals(dataGenerationFieldCell.getStringCellValue())) {
                    field.setDataGenerationField(true);
                }
            }

            // 判断是否为数据生成字段
            if (field.getDataGenerationField() != null && field.getDataGenerationField()) {
                genFields.add(field);  // 如果是数据生成字段，添加到 genFields 列表
            }

            // 添加到 detailFields 列表
            detailFields.add(field);
        }
    }


    // 生成最终的插入SQL语句
    private static String generateInsertSQL(String dataSourceName, String sqlExpression, String optionalField, List<Field> fields, long sn, String groupSn, String groupName) {
        StringBuilder fieldJson = new StringBuilder("[");

        for (Field field : fields) {
            fieldJson.append(String.format(
                    "{\"comment\":\"%s\",\"dataType\":\"%s\",\"dataTypeName\":\"%s\",\"name\":\"%s\"},",
                    field.getTableName(),    // 字段描述
                    field.getFieldType(),    // 字段类型
                    "字符串",         // 数据类型名默认设置为字符串
                    field.getFieldName()));  // 字段名称
        }

        if (fieldJson.length() > 1) {
            fieldJson.setLength(fieldJson.length() - 1); // 去掉最后一个逗号
        }

        fieldJson.append("]");

        return String.format(
                "INSERT INTO `settings_data_source` "
                        + "( `sn`, `name`, `code`, `protocol`, `data_source_group_sn`, `data_source_group_name`, "
                        + "`data_source_type`, `description`, `request_method`, `request_body`, `db_type`, `count_field`, "
                        + "`result_type`, `filter_field`, `api_url`, `field`, `sql`, `enabled`, `adder`, `add_time`, "
                        + "`updater`, `update_time`, `deleted`, `fixed_field`, `purview_fileld`) VALUES "
                        + "( %d, '%s', '%s', 'JDBC', %s, '%s', 'WORKBENCH,REPORT,REPORT_FORM', NULL, "
                        + "'POST', NULL, 'ck', 'add_time', 'data', '%s', NULL, '%s', '%s', 0, 'sysadmin', "
                        + "'2024-09-02 16:06:40', 'sysadmin', '2024-09-02 16:06:40', 0, '[]', NULL);",
                sn, dataSourceName, chineseToPinyin(dataSourceName), groupSn, groupName, fieldJson, optionalField, sqlExpression.replace("'", "\\'"));
    }


    private static String chineseToPinyin(String chinese) {
        StringBuilder pinyin = new StringBuilder();

        for (char ch : chinese.toCharArray()) {
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(ch);
            if (pinyinArray != null) {
                pinyin.append(pinyinArray[0]).append(" ");
            } else {
                pinyin.append(ch); // 非汉字保持原样
            }
        }

        return pinyin.toString().trim().replaceAll(" ", "_");
    }

    // 保存报表模板列表到JSON文件
    private static void saveReportTemplatesToJson(List<ReportTemplate> reportTemplates, String jsonFilePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            gson.toJson(reportTemplates, writer);
        }
        System.out.println("JSON文件保存到: " + jsonFilePath);
    }

    // DataSourceSQL 类用于存储数据源SQL的信息
    static class DataSourceSQL {
        private String dataSourceName;
        private String sqlExpression;
        private String optionalField;

        public DataSourceSQL(String dataSourceName, String sqlExpression, String optionalField) {
            this.dataSourceName = dataSourceName;
            this.sqlExpression = sqlExpression;
            this.optionalField = optionalField;
        }

        public String getDataSourceName() {
            return dataSourceName;
        }

        public String getSqlExpression() {
            return sqlExpression;
        }

        public String getOptionalField() {
            return optionalField;
        }
    }
}

// 定义用于保存到JSON文件的类


