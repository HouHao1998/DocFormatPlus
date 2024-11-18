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

public class GenerateSQLForFiles {

    private static final String CHECKSUM = "aQB/3mx3jW2dK7GNKnAiKQ==";  // checksum值

    private static final String FILE_PATH_PREFIX = "display/waijie/";  // file_path前缀


    public static void main(String[] args) {
        String BASE_FOLDER = "/Users/houhao/Downloads/上海报表测试文件夹/";
        String jsonFilePath = "/Users/houhao/Downloads/report_templates.json";  // JSON文件路径
        String outputJsonFilePath = "/Users/houhao/Downloads/report_templates.json";  // 更新后的JSON文件路径
        String sqlFilePath = "/Users/houhao/Downloads/minIo脚本.txt";  // SQL文件路径
        String ORIGIN_URL_PREFIX = "http://10.196.25.93:9000/saas/display/waijie/";  // origin_url前缀
        long fileSn = 5097652704810301L;
        excelToFileSql(ORIGIN_URL_PREFIX, sqlFilePath, jsonFilePath, outputJsonFilePath, BASE_FOLDER);
    }

    public static void excelToFileSql(String ORIGIN_URL_PREFIX, String sqlFilePath, String jsonFilePath, String outputJsonFilePath, String BASE_FOLDER) {
        try (BufferedWriter sqlWriter = new BufferedWriter(new FileWriter(sqlFilePath))) {
            // 读取并加载JSON文件
            List<ReportTemplate> reportTemplates = loadReportTemplates(jsonFilePath);

            File folder = new File(BASE_FOLDER);
            File[] files = folder.listFiles();

            if (files == null || files.length == 0) {
                System.out.println("没有找到文件！");
                return;
            }

            for (File file : files) {
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (!fileName.endsWith(".xlsx")) {
                      continue;
                    }
                    // 根据文件名找到对应的ReportTemplate对象
                    ReportTemplate matchingTemplate = findTemplateByName(file.getName(), reportTemplates);
                    if (matchingTemplate != null) {
                        // 更新fileSn111111
                        matchingTemplate.setFileSn(Long.parseLong("2222"+matchingTemplate.getSn()));
                    } else {
                        System.out.println("没有找到与文件名匹配的模板: " + file.getName());
                    }
                    sqlWriter.write("DELETE from common_attachment where file_sn = " + "2222"+matchingTemplate.getSn() + ";");
                    // 生成SQL
                    String sql = generateSQL(ORIGIN_URL_PREFIX, file, Long.parseLong("2222"+matchingTemplate.getSn()));
                    // 写入SQL到文件
                    sqlWriter.write(sql);
                    sqlWriter.newLine();
                    // 输出到控制台
                    System.out.println(sql);

                }
            }

            // 保存更新后的JSON文件
            saveReportTemplatesToJson(reportTemplates, outputJsonFilePath);

            System.out.println("SQL文件已保存到: " + sqlFilePath);
            System.out.println("更新后的JSON文件已保存到: " + outputJsonFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 查找与文件名匹配的ReportTemplate对象
    private static ReportTemplate findTemplateByName(String fileName, List<ReportTemplate> reportTemplates) {
        // 提取文件名的基本名称（忽略大小写和后缀）
        String baseFileName = fileName.substring(0, fileName.lastIndexOf(".")).toLowerCase();
        for (ReportTemplate template : reportTemplates) {
            String templateName = template.getName().toLowerCase();
            // 模板名是否包含文件的基本名称
            if (templateName.contains(baseFileName)) {
                return template;
            }
        }
        return null;
    }

    // 生成INSERT语句
    private static String generateSQL(String ORIGIN_URL_PREFIX, File file, long snCounter) {
        String fileName = file.getName();
        long fileSize = file.length();
        String fileSuffix = getFileSuffix(fileName);
        String filePath = FILE_PATH_PREFIX + fileName;
        String originUrl = ORIGIN_URL_PREFIX + fileName;

        // 当前时间
        String currentTime = "2024-09-10 20:22:57";

        return String.format("INSERT INTO `common_attachment` "
                        + "(`file_sn`, `file_name`, `file_size`, `file_suffix`, `file_path`, `checksum`, `origin_url`, "
                        + "`thumbnail_url`, `deleted`, `create_time`, `update_time`) "
                        + "VALUES (%d, '%s', %d, '%s', '%s', '%s', '%s', '', 0, '%s', '%s');",
                snCounter, fileName, fileSize, fileSuffix, filePath, CHECKSUM, originUrl, currentTime, currentTime);
    }

    // 获取文件后缀名
    private static String getFileSuffix(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        return (lastIndexOfDot == -1) ? "" : fileName.substring(lastIndexOfDot);
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
