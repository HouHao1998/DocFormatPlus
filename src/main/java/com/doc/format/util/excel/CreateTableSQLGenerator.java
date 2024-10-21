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
 * @date 2024/9/13 15:16
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class CreateTableSQLGenerator {

    private static final String TABLE_PREFIX = "report_storage_";  // 表名前缀

    public static void main(String[] args) throws IOException {
        String jsonFilePath = "/Users/houhao/Downloads/report_templates.json";  // JSON 文件路径
        String outputFilePath = "/Users/houhao/Downloads/建表语句.txt";  // SQL 文件输出路径

        // 从 JSON 读取模板并生成 SQL
        try {
            List<ReportTemplate> reportTemplates = readJsonToReportTemplates(jsonFilePath);
            for (ReportTemplate reportTemplate : reportTemplates) {
                String createTableSQL = generateCreateTableSQL(reportTemplate);
                saveSQLToFile(createTableSQL, outputFilePath);
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
            }.getType();  // 处理 JSON 数组
            return gson.fromJson(reader, listType);
        }
    }

    // 生成 CREATE TABLE SQL 语句
    private static String generateCreateTableSQL(ReportTemplate reportTemplate) {
        StringBuilder sqlBuilder = new StringBuilder();
        String tableName = TABLE_PREFIX + reportTemplate.getTemplateSn();

        // 表头部分
        sqlBuilder.append("CREATE TABLE `").append(tableName).append("` (\n")
                .append("`datafrom_id` int(11) NOT NULL AUTO_INCREMENT,\n")
                .append("`record_sn` bigint(20) DEFAULT NULL,\n")
                .append("`created_time` datetime DEFAULT NULL,\n");

        // 遍历 fields，生成每个字段的定义
        for (Field field : reportTemplate.getFields()) {
            sqlBuilder.append("`").append(field.getFieldName()).append("` text,\n");  // 假设所有字段类型为 text
        }

        // 添加主键
        sqlBuilder.append("PRIMARY KEY (`datafrom_id`)\n")
                .append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;");

        return sqlBuilder.toString();
    }

    // 将 SQL 保存到 txt 文件中
    private static void saveSQLToFile(String sql, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {  // 添加 'true' 以便追加而不是覆盖文件
            writer.write(sql);
            writer.newLine();  // 在每个 SQL 后添加换行
            System.out.println("SQL 已保存到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}