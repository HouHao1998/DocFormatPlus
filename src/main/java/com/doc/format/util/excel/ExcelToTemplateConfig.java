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

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelToTemplateConfig {


    public static void main(String[] args) {
        String SQL_FILE_PATH = "/Users/houhao/Downloads/report_template_config脚本.txt";  // SQL文件路径
        String JSON_FILE_PATH = "/Users/houhao/Downloads/report_templates.json";  // JSON文件路径
        long snCounter = 80012409111190001L;  // 初始SN值
        excelToTemplateConfig(SQL_FILE_PATH, JSON_FILE_PATH);
    }

    public static void excelToTemplateConfig(String SQL_FILE_PATH, String JSON_FILE_PATH) {
        try (BufferedWriter sqlWriter = new BufferedWriter(new FileWriter(SQL_FILE_PATH))) {
            // 读取并加载JSON文件
            List<ReportTemplate> reportTemplates = loadReportTemplates(JSON_FILE_PATH);

            for (ReportTemplate template : reportTemplates) {
                // 生成SQL语句
                String sql = generateSQL(template, Long.parseLong("3333" + template.getSn()));
                sqlWriter.write("DELETE from report_template_config where sn = " + Long.parseLong("3333" + template.getSn()) + ";");
                // 写入SQL到文件
                sqlWriter.write(sql);
                sqlWriter.newLine();

                // 输出到控制台
                System.out.println(sql);

            }

            System.out.println("SQL脚本已保存到: " + SQL_FILE_PATH);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 生成INSERT语句
    private static String generateSQL(ReportTemplate template, long snCounter) {
        String configJson = generateConfigJson(template);
        String tableHead = generateTableHead(template);
        String tableBody = generateTableBody(template);
        String tableField = generateTableField(template);

        return String.format("INSERT INTO `report_template_config` "
                        + "(`template_sn`, `dataset_codes`, `dataset_param`, `config_json`, `deleted`, `sn`, `table_head`, `table_body`, `template_file_sn`, `add_time`, `table_field`) "
                        + "VALUES (%d, NULL, NULL, '%s', 0, %d, '%s', '%s', %d, '2024-09-11 10:35:32', '%s');",
                template.getTemplateSn(), configJson, snCounter, tableHead.replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\"", "\\\""), tableBody.replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\"", "\\\""), template.getFileSn(), tableField);
    }

    // 生成config_json字段
    public static String generateConfigJson(ReportTemplate template) {
        // 1. 生成根对象 ReportDatasetConf
        ReportDatasetConf reportDatasetConf = new ReportDatasetConf();

        // 2. 填充 singleVars 列表
        reportDatasetConf.setSingleVars(new ArrayList<>());  // 没有内容，空列表

        // 3. 创建 detail 表
        ReportDatasetConf.Table detailTable = new ReportDatasetConf.Table();
        detailTable.setDataset(template.getDetailDataSource().getCode());
        detailTable.setDatasetLabel("审计策略数据源/" + template.getDetailDataSource().getName());
        detailTable.setStartCell("A2");
        detailTable.setLastCell("T2");
        detailTable.setItems("items");
        List<String> detailTableDatasetKeys = new ArrayList<>();
        detailTableDatasetKeys.add(String.valueOf(template.getDetailDataSource().getGroupSn()));
        detailTableDatasetKeys.add(template.getDetailDataSource().getCode());
        detailTable.setDatasetKey(detailTableDatasetKeys);
        List<ReportDatasetConf.Fieldoptions> detailFieldoptions = new ArrayList<>();
        template.getDetailDataSource().getFields().forEach(field -> {
            detailFieldoptions.add(createFieldoptions(field.getFieldName(), field.getTableName()));
        });
        detailTable.setFieldoptions(detailFieldoptions);
        // 填充 detail 部分的 tableField
        List<ReportDatasetConf.TabelField> detailTableFields = new ArrayList<>();
        template.getDetailDataSource().getFields().forEach(field -> {
            detailTableFields.add(createTableField(field.getFieldName(), field.getTableName()));
        });
        detailTable.setTableField(detailTableFields);

        // 填充 detail 部分的 fieldsConfigs
        List<ReportDatasetConf.FieldsConfig> detailFieldsConfigs = new ArrayList<>();
        template.getDetailDataSource().getFields().forEach(field -> {
            // 创建 fieldsConfig 对象并配置
            ReportDatasetConf.FieldsConfig fieldsConfig = createFieldsConfig(
                    field.getFieldName(), field.getTableName(), 0, new ReportDatasetConf.Drilling());

            // 处理补全功能
            if (field.getSupportComplete() != null && field.getSupportComplete()) {
                ReportDatasetConf.Completion completion = new ReportDatasetConf.Completion();
                completion.setEnable("yes");
                completion.setType(field.getCompleteType());

                // 解析 completeConfig，生成 fieldMapping 列表
                List<ReportDatasetConf.FieldMapping> fieldMappings = parseCompleteConfig(field.getCompleteConfig());
                completion.setFieldMapping(fieldMappings);
                fieldsConfig.setCompletion(completion);
            } else {
                fieldsConfig.setCompletion(new ReportDatasetConf.Completion());
            }


            detailFieldsConfigs.add(fieldsConfig);
        });
        detailTable.setFieldsConfigs(detailFieldsConfigs);

        // 4. 创建 stat 表
        ReportDatasetConf.Table statTable = new ReportDatasetConf.Table();
        statTable.setDataset(template.getStatDataSource().getCode());
        statTable.setDatasetLabel("报表数据源组/" + template.getStatDataSource().getName());
        statTable.setStartCell("A2");
        statTable.setLastCell("T2");
        statTable.setItems("items");

        // 填充 stat 部分的 tableField
        List<ReportDatasetConf.TabelField> statTableFields = new ArrayList<>();
        template.getStatDataSource().getFields().forEach(field -> {
            statTableFields.add(createTableField(field.getFieldName(), field.getTableName()));
        });
        statTable.setTableField(statTableFields);
        List<ReportDatasetConf.Fieldoptions> fieldoptions = new ArrayList<>();
        template.getStatDataSource().getFields().forEach(field -> {
            fieldoptions.add(createFieldoptions(field.getFieldName(), field.getTableName()));
        });
        statTable.setFieldoptions(fieldoptions);
        // 填充 stat 部分的 fieldsConfigs
        List<ReportDatasetConf.FieldsConfig> statFieldsConfigs = new ArrayList<>();
        template.getStatDataSource().getFields().forEach(field -> {
            // 判断是否支持钻取
            Integer drilling = (field.getSupportDrill() != null && field.getSupportDrill()) ? 1 : 0;

            // 创建 drillingConfig 并确保 filterField 不存在循环引用
            ReportDatasetConf.Drilling drillingConfig = null;  // 初始化为null
            if (drilling == 1) {
                drillingConfig = new ReportDatasetConf.Drilling();
                // 设置 drillingConfig 的详细信息
                drillingConfig.setDataset("下级".equals(field.getDrillType()) ? template.getStatDataSource().getCode() : template.getDetailDataSource().getCode());
                drillingConfig.setType("下级".equals(field.getDrillType()) ? "subordinate" : "detailed");
                Map<String, String> map = new HashMap<>();
                if (field.getDrillOptionalParam() != null && !field.getDrillOptionalParam().isEmpty()) {
                    for (String pair : field.getDrillOptionalParam().split(", ")) {
                        String[] keyValue = pair.split("=");
                        if (keyValue.length == 2 && keyValue[0] != null && !keyValue[0].trim().isEmpty()
                                && keyValue[1] != null && !keyValue[1].trim().isEmpty()) {
                            map.put(keyValue[0].trim(), keyValue[1].trim());
                        }
                    }
                }
                drillingConfig.setFilterField("下级".equals(field.getDrillType()) ? deepCopyFilterFields(template.getStatDataSource().getFilterField(), map) : deepCopyFilterFields(template.getDetailDataSource().getFilterField(), map));
                List<String> datasetKeys = new ArrayList<>();
                datasetKeys.add(String.valueOf(template.getStatDataSource().getGroupSn()));
                datasetKeys.add("下级".equals(field.getDrillType()) ? template.getStatDataSource().getCode() : template.getDetailDataSource().getCode());
                drillingConfig.setDatasetKey(datasetKeys);
            }

            // 创建 fieldsConfig 对象并配置
            ReportDatasetConf.FieldsConfig fieldsConfig = createFieldsConfig(
                    field.getFieldName(), field.getTableName(), drilling, drillingConfig);

            // 设置 drilling 和 drillingConfig
            if (drilling == 1) {
                fieldsConfig.setDrilling(drilling);
                fieldsConfig.setDrillingConfig(drillingConfig);
            } else {
                fieldsConfig.setDrillingConfig(new ReportDatasetConf.Drilling());
            }

            // 处理补全功能
            if (field.getSupportComplete() != null && field.getSupportComplete()) {
                ReportDatasetConf.Completion completion = new ReportDatasetConf.Completion();
                completion.setEnable("yes");
                completion.setType(field.getCompleteType());

                // 解析 completeConfig，生成 fieldMapping 列表
                List<ReportDatasetConf.FieldMapping> fieldMappings = parseCompleteConfig(field.getCompleteConfig());
                completion.setFieldMapping(fieldMappings);
                fieldsConfig.setCompletion(completion);
            } else {  // 不支持补全，则清空补全配置
                fieldsConfig.setCompletion(new ReportDatasetConf.Completion());
            }

            statFieldsConfigs.add(fieldsConfig);
        });
        statTable.setFieldsConfigs(statFieldsConfigs);
        List<String> statTableDatasetKeys = new ArrayList<>();
        statTableDatasetKeys.add(String.valueOf(template.getStatDataSource().getGroupSn()));
        statTableDatasetKeys.add(template.getStatDataSource().getCode());
        statTable.setDatasetKey(statTableDatasetKeys);
        // 5. 将 detail 和 stat 表格添加到 tables
        List<Map<String, ReportDatasetConf.Table>> tables = new ArrayList<>();
        Map<String, ReportDatasetConf.Table> detailMap = new HashMap<>();
        detailMap.put("detail", detailTable);
        detailTable.setFilterField(template.getDetailDataSource().getFilterField());
        tables.add(detailMap);

        detailMap.put("stat", statTable);
        tables.add(detailMap);

        reportDatasetConf.setTables(tables);

        // 6. 填充 charts 部分（这里是空的）
        reportDatasetConf.setCharts(new ArrayList<>());

        return JSON.toJSONString(reportDatasetConf);
    }

    private static ReportDatasetConf.Fieldoptions createFieldoptions(String fieldName, String tableName) {
        ReportDatasetConf.Fieldoptions fieldoptions = new
                ReportDatasetConf.Fieldoptions();
        fieldoptions.setComment(tableName);
        fieldoptions.setName(fieldName);
        fieldoptions.setDataTypeName("字符串");
        fieldoptions.setDataType("String");

        return fieldoptions;
    }

    // 方法解析 completeConfig，生成 FieldMapping 列表
    private static List<ReportDatasetConf.FieldMapping> parseCompleteConfig(String completeConfig) {
        List<ReportDatasetConf.FieldMapping> fieldMappings = new ArrayList<>();
        if (completeConfig != null && !completeConfig.trim().isEmpty()) {
            // 去除可能存在的多余空格
            completeConfig = completeConfig.trim();

            // 处理不同的分隔符组合，如 ';' 或 ','
            String[] mappings = completeConfig.split("[;,]");
            for (String mapping : mappings) {
                String[] fields = mapping.split("=");
                if (fields.length == 2) {
                    // 去除来源字段和填充字段中的多余空格
                    String sourceField = fields[0].trim();
                    String fillField = fields[1].trim();

                    // 确保字段不为空
                    if (!sourceField.isEmpty() && !fillField.isEmpty()) {
                        ReportDatasetConf.FieldMapping fieldMapping = new ReportDatasetConf.FieldMapping();
                        fieldMapping.setSf(sourceField);  // 来源字段
                        fieldMapping.setFf(fillField);    // 填充字段
                        fieldMappings.add(fieldMapping);
                    }
                }
            }
        }
        return fieldMappings;
    }

    // 深拷贝 filterField 防止循环引用问题
    private static List<FilterField> deepCopyFilterFields(List<FilterField> originalFilterFields, Map<String, String> map) {
        List<FilterField> copiedFilterFields = new ArrayList<>();
        for (FilterField field : originalFilterFields) {
            FilterField copiedField = new FilterField();
            copiedField.setFieldName(field.getFieldName());
            copiedField.setFieldComment(field.getFieldComment());
            copiedField.setFieldType(field.getFieldType());
            copiedField.setInputType(field.getInputType());
            copiedField.setValue(map.get(field.getFieldName()));
            copiedFilterFields.add(copiedField);
        }
        return copiedFilterFields;
    }


    // 帮助方法：创建 tableField
    private static ReportDatasetConf.TabelField createTableField(String fieldName, String tableName) {
        ReportDatasetConf.TabelField tabelField = new ReportDatasetConf.TabelField();
        tabelField.setFieldName(fieldName);
        tabelField.setTableName(tableName);
        return tabelField;
    }

    // 帮助方法：创建 fieldsConfigs（不带钻取）
    private static ReportDatasetConf.FieldsConfig createFieldsConfig(String field, String fieldLabel, int drilling, ReportDatasetConf.Drilling drillingConfig) {
        ReportDatasetConf.FieldsConfig fieldsConfig = new ReportDatasetConf.FieldsConfig();
        fieldsConfig.setField(field);
        fieldsConfig.setTemplateVarName(field);
        fieldsConfig.setFieldLabel(fieldLabel);
        fieldsConfig.setDrilling(drilling);
        fieldsConfig.setDrillingConfig(drillingConfig);
        return fieldsConfig;
    }

    // 帮助方法：创建 fieldsConfigs（带钻取）
    private static ReportDatasetConf.FieldsConfig createFieldsConfigWithDrilling(String field, String fieldLabel, int drilling, String type, String dataset) {
        ReportDatasetConf.FieldsConfig fieldsConfig = new ReportDatasetConf.FieldsConfig();
        fieldsConfig.setField(field);
        fieldsConfig.setTemplateVarName(field);
        fieldsConfig.setFieldLabel(fieldLabel);
        fieldsConfig.setDrilling(drilling);

        ReportDatasetConf.Drilling drillingConfig = new ReportDatasetConf.Drilling();
        drillingConfig.setDataset(dataset);
        drillingConfig.setType(type);
        fieldsConfig.setDrillingConfig(drillingConfig);

        return fieldsConfig;
    }

    // 生成table_head字段
    private static String generateTableHead(ReportTemplate template) {
        StringBuilder tableHead = new StringBuilder("{");

        // **生成 detail 部分的表头**
        tableHead.append("\"detail\":\"[[");
        for (Field field : template.getDetailDataSource().getFields()) {
            // 对表头字段进行转义处理
            String tableNameEscaped = escapeSqlValue(field.getTableName());

            tableHead.append(String.format("{\\\"t_h_title\\\":\\\"%s\\\"},", tableNameEscaped));
        }
        if (tableHead.length() > 1) {
            tableHead.setLength(tableHead.length() - 1);  // 去掉最后的逗号
        }
        tableHead.append("]]\",");

        // **生成 stat 部分的表头**
        tableHead.append("\"stat\":\"[[");
        for (Field field : template.getStatDataSource().getFields()) {
            // 对表头字段进行转义处理
            String tableNameEscaped = escapeSqlValue(field.getTableName());

            tableHead.append(String.format("{\\\"t_h_title\\\":\\\"%s\\\"},", tableNameEscaped));
        }
        if (tableHead.length() > 1) {
            tableHead.setLength(tableHead.length() - 1);  // 去掉最后的逗号
        }
        tableHead.append("]]\"");

        tableHead.append("}");

        return tableHead.toString();
    }

    // 生成table_body字段
    private static String generateTableBody(ReportTemplate template) {
        StringBuilder tableBody = new StringBuilder("{");

        // **处理 detail 部分的表体**
        tableBody.append("\"detail\":\"[[");
        for (Field field : template.getDetailDataSource().getFields()) {
            // 对字段名和变量名进行转义处理
            String fieldNameEscaped = escapeSqlValue(field.getFieldName());
            String templateVarNameEscaped = escapeSqlValue(field.getFieldName());

            tableBody.append(String.format("{\\\"drilling\\\":\\\"0\\\",\\\"field\\\":\\\"%s\\\",\\\"templateVarName\\\":\\\"%s\\\"},",
                    fieldNameEscaped, templateVarNameEscaped));
        }
        if (tableBody.length() > 1) {
            tableBody.setLength(tableBody.length() - 1);  // 去掉最后的逗号
        }
        tableBody.append("]]\",");

        // **处理 stat 部分的表体**
        tableBody.append("\"stat\":\"[[");
        for (Field field : template.getStatDataSource().getFields()) {
            // 对字段名和变量名进行转义处理
            String fieldNameEscaped = escapeSqlValue(field.getFieldName());
            String templateVarNameEscaped = escapeSqlValue(field.getFieldName());

            // 处理 supportDrill 为空的情况
            int drillingValue = (field.getSupportDrill() != null && field.getSupportDrill()) ? 1 : 0;

            // 处理 drillType 为空的情况
            String typeValue = "明细".equalsIgnoreCase(field.getDrillType() != null ? field.getDrillType() : "") ? "detailed" : "subordinate";

            tableBody.append(String.format("{\\\"drilling\\\":\\\"%d\\\",\\\"field\\\":\\\"%s\\\",\\\"templateVarName\\\":\\\"%s\\\",\\\"type\\\":\\\"%s\\\"},",
                    drillingValue, fieldNameEscaped, templateVarNameEscaped, typeValue));
        }
        if (tableBody.length() > 1) {
            tableBody.setLength(tableBody.length() - 1);  // 去掉最后的逗号
        }
        tableBody.append("]]\"");

        tableBody.append("}");

        return tableBody.toString();
    }


    // 对 SQL 中的值进行转义
    private static String escapeSqlValue(String value) {
        if (value == null) {
            return null;
        }
        // 转义反斜杠和引号
        return value.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"");
    }


    // 生成table_field字段
    private static String generateTableField(ReportTemplate template) {
        StringBuilder tableField = new StringBuilder("[");

        for (Field field : template.getFields()) {
            int audit = field.getAuditField() != null && field.getAuditField() ? 1 : 0;  // 根据 auditField 判断是否为 1
            int auditDimension = field.getTaskDispatch() != null && field.getTaskDispatch() ? 1 : 0;  // 根据 taskDispatch 判断是否为 1

            tableField.append("{");
            tableField.append(String.format("\"audit\":%d,\"auditDimension\":%d,", audit, auditDimension));

            if (auditDimension == 1) {
                tableField.append("\"dimensionType\":\"ORG\",");  // 如果 auditDimension 为 1，增加 dimensionType
            }

            tableField.append(String.format("\"fieldName\":\"%s\",\"tableName\":\"%s\"},",
                    field.getFieldName(), field.getTableName()));
        }

        if (tableField.length() > 1) {
            tableField.setLength(tableField.length() - 1);  // 去掉最后的逗号
        }

        tableField.append("]");
        return tableField.toString();
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
}
