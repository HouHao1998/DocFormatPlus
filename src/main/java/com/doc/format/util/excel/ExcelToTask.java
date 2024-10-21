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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;

public class ExcelToTask {


    public static void main(String[] args) {
        long taskSn = 80042409119820001L;
        String jsonFilePath = "/Users/houhao/Downloads/report_templates.json";  // JSON 文件路径
        String outputFilePath = "/Users/houhao/Downloads/report_task_脚本.txt";  // SQL 文件输出路径

        jsonToTask(jsonFilePath, outputFilePath, taskSn);
    }

    public static void jsonToTask(String jsonFilePath, String outputFilePath, long taskSn) {
        // 从 JSON 读取模板并生成 SQL
        try {
            List<ReportTemplate> reportTemplates = readJsonToReportTemplates(jsonFilePath);
            for (ReportTemplate reportTemplate : reportTemplates) {
                String insertSQL = generateInsertSQL(reportTemplate, taskSn);
                saveSQLToFile(insertSQL, outputFilePath);

                // 自增 sn
                taskSn++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 从 JSON 文件读取 ReportTemplate 对象列表
    private static List<ReportTemplate> readJsonToReportTemplates(String filePath) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(filePath)) {
            Type listType = new TypeToken<List<ReportTemplate>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        }
    }

    // 生成 INSERT INTO SQL 语句
    private static String generateInsertSQL(ReportTemplate reportTemplate, long snCounter) {
        long templateSn = reportTemplate.getTemplateSn();
        String templateName = reportTemplate.getName();
        String taskName = templateName;  // 生成任务名称，可以根据需要修改

        // 生成 INSERT INTO 语句，使用自增的 sn 值
        return String.format("INSERT INTO `report_task` " +
                        "( `sn`, `name`, `template_sn`, `template_name`, `corn`, `task_type`, `data_set_param`, `template_type`, `start_range_time`, `end_range_time`, " +
                        "`time_unit`, `time_value`, `day_of_month`, `day_of_week`, `hour_of_day`, `min_of_hour`, `description`, `enabled`, `add_time`, `deleted`, " +
                        "`is_push`, `region`, `write_time`, `write_day`) " +
                        "VALUES ( %d, '%s', %d, '%s', '0 0 15 14 * ? ', 'month', NULL, 'graphic_excel', NULL, NULL, NULL, NULL, '14', 0, 15, 0, NULL, 1, '2024-09-11 07:36:19', 0, NULL, NULL, NULL, NULL);",
                snCounter, taskName, templateSn, templateName);
    }

    // 将 SQL 保存到 txt 文件中
    private static void saveSQLToFile(String sql, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(sql);
            writer.newLine();  // 在每个 SQL 后添加换行
            System.out.println("SQL 已保存到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
