package com.doc.format.util.excel;

import com.alibaba.fastjson2.JSONArray;
import lombok.Data;

import javax.annotation.processing.Completion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <b>审计报表数据集配置</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 袁海洋(0862)
 * @date 2024/5/27 16:27
 */
@Data
public class ReportDatasetConf {

    public final static String TABLE_TYPE_DETAIL = "detail";

    public final static String TABLE_TYPE_STAT = "stat";

    private List<SingleVar> singleVars;

    private List<Map<String, Table>> tables;

    private List<Chart> charts;

    @Data
    public static class Table {
        private String dataset;
        private String items;
        private List<FieldsConfig> fieldsConfigs;
        private List<FilterField> filterField = new ArrayList<>();

        //下面两个字段，编辑模板时前端回显需要
        private List<String> datasetKey;
        //单元格标注
        private String startCell;
        private String lastCell;
        private String datasetLabel;
        private List<Fieldoptions> fieldoptions;
        //detail-明细表 ; stat-统计表 ;
        private String type;
        private List<TabelField> tableField;
    }

    @Data
    public static class Fieldoptions {
        private String comment;
        private String dataType;

        //下面字段，编辑模板时前端回显需要
        private String dataTypeName;
        //是否钻取 默认不开启
        private String name;

    }

    @Data
    public static class FieldsConfig {
        private String templateVarName;
        private String field;

        //下面字段，编辑模板时前端回显需要
        private String fieldLabel;
        //是否钻取 默认不开启
        private Integer drilling = 0;
        private Drilling drillingConfig;
        //补全
        private Completion completion;
    }

    @Data
    public static class TabelField {
        private String fieldName;
        private String tableName;
    }

    @Data
    public static class Drilling {
        private String templateSn;
        private String dataset;
        /**
         * 钻取类型：下级 subordinate 明细 detailed
         */
        private String type;
        private String startTime;
        private String endTime;
        private List<String> datasetKey;
        private List<FilterField> filterField = new ArrayList<>();
    }

    @Data
    public static class Completion {
        //yes/no
        private String enable;//是否开启补全
        //mainAcct/org/resource
        private String type;//补全类型
        //来源字段与填充字段对应关系
        private List<FieldMapping> fieldMapping = new ArrayList<>();
    }

    @Data
    public static class FieldMapping {
        private String sf;//来源字段
        private String ff;//填充字段
    }

    @Data
    public class Chart {
        private String dataset;
        private String templateVarName;
        private String style;
        private String chartCode;
        private String title;
        private List<FilterField> filterField = new ArrayList<>();
        //下面字段，编辑模板时前端回显需要
        private String datasetKey;
        private String datasetLabel;
        private JSONArray dataStructures;
        private String charName;
        private String chartCodeName;
        private String chartStyle;
        //是否钻取 默认不开启
        private Integer drilling = 0;
        private Drilling drillingConfig;
    }

    @Data
    public class SingleVar {
        private String templateVarName;
        private String dataset;
        private String field;
        private List<FilterField> filterField = new ArrayList<>();

        //下面四个字段，前端编辑回显需要
        private String datasetKey;
        private String fieldKey;
        private String datasetLabel;
        private String fieldLabel;
    }

}
