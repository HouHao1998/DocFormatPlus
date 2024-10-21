package com.doc.format.util.entity;

import com.alibaba.fastjson.annotation.JSONCreator;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/20 09:34
 */
import com.alibaba.fastjson.annotation.JSONField;

public enum ElementType {
    TEXT("TEXT"),
    LIST("LIST"),
    TABLE("TABLE"),
    IMAGE("IMAGE"),
    HYPERLINK("HYPERLINK"),
    FORM_FIELD("FORM_FIELD"),
    HEADER("HEADER"),
    PARAGRAPH("PARAGRAPH"),
    FOOTER("FOOTER");

    private final String value;

    ElementType(String value) {
        this.value = value;
    }

    @JSONCreator
    public static ElementType fromValue(String value) {
        for (ElementType type : ElementType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown type: " + value);
    }

    @JSONField
    public String getValue() {
        return value;
    }
}
