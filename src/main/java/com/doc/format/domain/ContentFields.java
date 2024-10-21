package com.doc.format.domain;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/7/12 15:12
 */
public enum ContentFields {
    CHAPTER_TITLES("章节编号和标题", new ArrayList<>()),
    SECTION_TITLES("节编号和标题", new ArrayList<>()),
    FULL_CONTENT("正文内容", new ArrayList<>()),
    TABLES("表格", new ArrayList<>()),
    REFERENCES("参考文献", new ArrayList<>()),
    CHINESE_TITLE("中文题名", ""),
    AUTHOR_NAME("作者姓名", ""),
    WORK_UNIT("工作单位及通信方式", ""),
    CHINESE_ABSTRACT("中文摘要", new HashMap<String, String>() {{
        put("引题", "摘要：");
        put("内容", "");
    }}),
    CHINESE_KEYWORDS("中文关键词", new HashMap<String, String>() {{
        put("引题", "关键词：");
        put("内容", "");
    }}),
    ENGLISH_TITLE("英文题名", ""),
    ENGLISH_AUTHOR_NAME("英文作者姓名", ""),
    ENGLISH_WORK_UNIT("英文工作单位及通信方式", ""),
    ENGLISH_ABSTRACT("英文摘要", new HashMap<String, String>() {{
        put("引题", "Abstract:");
        put("内容", "");
    }}),
    ENGLISH_KEYWORDS("英文关键词", new HashMap<String, String>() {{
        put("引题", "Key words:");
        put("内容", "");
    }});

    private final String fieldName;
    private final Object defaultValue;

    ContentFields(String fieldName, Object defaultValue) {
        this.fieldName = fieldName;
        this.defaultValue = defaultValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
