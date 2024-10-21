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
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class ExcelToTemplate {


    public static void main(String[] args) {
        String SQL_FILE_PATH = "/Users/houhao/Downloads/report_template脚本.txt";  // SQL文件路径
        String JSON_FILE_PATH = "/Users/houhao/Downloads/report_templates.json";  // JSON文件路径
        String OUTPUT_JSON_FILE_PATH = "/Users/houhao/Downloads/report_templates.json";  // 更新后的JSON文件路径
        long snCounter = 80012409111101001L;  // 初始SN值
        long groupSn = 8009240809140002L;  // 初始SN值
        excelToTemplate(SQL_FILE_PATH, JSON_FILE_PATH, OUTPUT_JSON_FILE_PATH, snCounter, groupSn);
    }

    public static void excelToTemplate(String SQL_FILE_PATH, String JSON_FILE_PATH, String OUTPUT_JSON_FILE_PATH, long snCounter, long groupSn) {
        try (BufferedWriter sqlWriter = new BufferedWriter(new FileWriter(SQL_FILE_PATH))) {
            // 读取并加载JSON文件
            List<ReportTemplate> reportTemplates = loadReportTemplates(JSON_FILE_PATH);

            for (ReportTemplate template : reportTemplates) {
                // 使用json中的name 和 template_file_sn生成SQL
                String sql = generateSQL(template.getName(), template.getFileSn(), snCounter, groupSn, template.getSourceDataset(), template.getDetailTable());
                // 写入SQL到文件
                sqlWriter.write("DELETE from report_template where sn = " + snCounter + ";");
                sqlWriter.write(sql);
                sqlWriter.newLine();

                // 更新sn
                template.setTemplateSn(snCounter);

                // 输出到控制台
                System.out.println(sql);

                // 自增sn
                snCounter++;
            }

            // 保存更新后的JSON文件，包含templateSn
            saveReportTemplatesToJson(reportTemplates, OUTPUT_JSON_FILE_PATH);

            System.out.println("SQL脚本已保存到: " + SQL_FILE_PATH);
            System.out.println("更新后的JSON文件已保存到: " + OUTPUT_JSON_FILE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 生成INSERT语句
    private static String generateSQL(String name, long templateFileSn, long snCounter, Long groupSn, String sourceDataset, String detailTable) {
        return String.format("INSERT INTO `report_template` ( `sn`, `name`, `template_type`, `image_url`, "
                        + "`description`, `conf_status`, `enabled`, `deleted`, `adder`, `add_time`, `updater`, "
                        + "`update_time`, `group_sn`, `flow_sn`, `writeable`, `generate_mode`, `template_file_sn`, "
                        + "`action_scope`, `template_from`, `audit`, `order_basis`, `detail_table`, `source_dataset`) "
                        + "VALUES (%d, '%s', 'graphic_excel', NULL, '', NULL, 1, 0, NULL, '2024-09-11 10:35:32', "
                        + "NULL, NULL, %s, NULL, 0, 0, %d, 'M,B', NULL, NULL, '工单依据', '%s', '%s');",
                snCounter, name, groupSn, templateFileSn, detailTable, sourceDataset);
    }

    // 读取JSON文件并转换为ReportTemplate列表
    private static List<ReportTemplate> loadReportTemplates(String jsonFilePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(jsonFilePath)) {
            Type listType = new TypeToken<List<ReportTemplate>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        }
    }

    // 保存更新后的报表模板到JSON文件
    private static void saveReportTemplatesToJson(List<ReportTemplate> reportTemplates, String jsonFilePath) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            gson.toJson(reportTemplates, writer);
        }
        System.out.println("JSON文件保存到: " + jsonFilePath);
    }
}

