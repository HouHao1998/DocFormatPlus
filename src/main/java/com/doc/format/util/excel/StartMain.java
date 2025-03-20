package com.doc.format.util.excel;


import java.io.*;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/10/11 09:33
 */
public class StartMain {
    public static void main(String[] args) {
        String excelFilePath = "/Users/houhao/Downloads/1119报表/绕行访问涉敏后台资源审计.xlsx";  // Excel文件路径
        String jsonFilePath = "/Users/houhao/Downloads/1119报表/report_templates.json";  // JSON文件路径
        String sqlFilePath = "/Users/houhao/Downloads/1119报表/settingsDataSource.txt";  // SQL脚本文件路径
        String folderPath = "/Users/houhao/Downloads/1119报表/";
        String groupSn = "9902360002";
        String groupName = "审计报表数据源";
        long snCounter = 9912340908370004L;
        //数据源的SN默认是1110开始然后叠加
        ExcelToSettingsDataSource.settingsDataSourceForFolder(folderPath, jsonFilePath, sqlFilePath, groupSn, groupName);
        String outputFolder = "/Users/houhao/Downloads/1119报表/上海报表测试文件夹/"; // 输出 Excel 文件夹路径
        //
        ExcelReportGenerator.makeExcel(jsonFilePath, outputFolder);
        long fileSn = 5012342106810301L;
        String minIo = "/Users/houhao/Downloads/1119报表/minIo.txt";
        String originUrlPrefix = "http://192.168.2.121:9000/saas/display/waijie/";
        // String originUrlPrefix = "http://10.196.25.93:9000/saas/display/waijie/";
        GenerateSQLForFiles.excelToFileSql(originUrlPrefix, minIo, jsonFilePath, jsonFilePath, outputFolder);
        long templateSn = 80123419117101001L;  // 初始SN值111111
        String templateFilePath = "/Users/houhao/Downloads/1119报表/report_template.txt";  // SQL文件路径
        long templateGroupSn = 8012340809140002L;
        ExcelToTemplate.excelToTemplate(templateFilePath, jsonFilePath, jsonFilePath, templateGroupSn);
        long templateConfigSn = 80123419181190001L;  // 初始SN值1
        String reportTemplateConfigFilePath = "/Users/houhao/Downloads/1119报表/report_template_config.txt";
        ExcelToTemplateConfig.excelToTemplateConfig(reportTemplateConfigFilePath, jsonFilePath);
        // long taskSn = 80042409112820011L;11
        // String outputFilePath = "/Users/houhao/Downloads/1119报表/report_task.txt";  // SQL 文件输出路径£
        //1
        // ExcelToTask.jsonToTask(jsonFilePath, outputFilePath, taskSn);
        String[] txtFiles = {
                "/Users/houhao/Downloads/1119报表/settingsDataSource.txt",
                "/Users/houhao/Downloads/1119报表/minIo.txt",
                "/Users/houhao/Downloads/1119报表/report_template.txt",
                "/Users/houhao/Downloads/1119报表/report_template_config.txt"
        };

        // 合并后的输出文件
        String outputFilePath = "/Users/houhao/Downloads/1119报表/统一执行sql文件.txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for (String filePath : txtFiles) {
                File file = new File(filePath);
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                    writer.newLine(); // 文件之间加入空行
                } catch (IOException e) {
                    System.out.println("Error reading file: " + filePath);
                }
            }
            System.out.println("All files merged successfully into: " + outputFilePath);
        } catch (IOException e) {
            System.out.println("Error writing to output file: " + outputFilePath);
        }
    }


}
