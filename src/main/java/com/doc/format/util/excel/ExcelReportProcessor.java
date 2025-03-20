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
 * @date 2024/11/21 14:31
 */

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelReportProcessor {

    public static void main(String[] args) throws Exception {
        String reportDirectoryPath = "/Users/houhao/Downloads/生成模版/报表目录.xlsx";
        String templateFilePath = "/Users/houhao/Downloads/生成模版/模板文件.xlsx";
        String outputDir = "/Users/houhao/Downloads/生成模版/生成模版文件";

        processReports(reportDirectoryPath, templateFilePath, outputDir);
    }

    public static void processReports(String reportDirectoryPath, String templateFilePath, String outputDir) throws Exception {
        // Step 1: Read report_sn and report_name from the report directory
        Map<String, String> reportMap = readReportDirectory(reportDirectoryPath);

        // Step 2: Process each report and generate a new Excel file
        for (Map.Entry<String, String> entry : reportMap.entrySet()) {
            String reportSn = entry.getKey();
            String reportName = entry.getValue();
            generateExcel(templateFilePath, outputDir, reportSn, reportName);
        }
    }

    private static Map<String, String> readReportDirectory(String reportDirectoryPath) throws Exception {
        Map<String, String> reportMap = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(reportDirectoryPath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assume the first sheet contains the report directory
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Start from the second row
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell reportSnCell = row.getCell(0);
                    Cell reportNameCell = row.getCell(1);
                    if (reportSnCell != null && reportNameCell != null) {
                        String reportSn = reportSnCell.toString().trim();
                        String reportName = reportNameCell.toString().trim();
                        reportMap.put(reportSn, reportName);
                    }
                }
            }
        }
        return reportMap;
    }

    private static void generateExcel(String templateFilePath, String outputDir, String reportSn, String reportName) throws Exception {
        try (FileInputStream fis = new FileInputStream(templateFilePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            // Modify the sheet named "配置说明"
            Sheet configSheet = workbook.getSheet("配置说明");
            if (configSheet != null) {
                Row row = configSheet.getRow(2); // Third row (0-indexed)
                if (row != null) {
                    Cell cell = row.getCell(1); // Second column (0-indexed)
                    if (cell == null) {
                        cell = row.createCell(1);
                    }
                    cell.setCellValue(reportSn);
                }
            }

            // Modify the sheet named "数据源SQL"
            Sheet sqlSheet = workbook.getSheet("数据源SQL");
            if (sqlSheet != null) {
                for (Row row : sqlSheet) {
                    Cell cell = row.getCell(0); // First column (0-indexed)
                    if (cell != null && cell.toString().contains("XXXX")) {
                        cell.setCellValue(cell.toString().replace("XXXX", reportName));
                    }
                }
            }

            // Save the modified workbook
            File outputDirectory = new File(outputDir);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }

            String outputFilePath = outputDir + File.separator + reportName + ".xlsx";
            try (FileOutputStream fos = new FileOutputStream(outputFilePath)) {
                workbook.write(fos);
            }
        }
    }
}