package com.doc.format.util.entity;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/20 09:35
 */

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.util.List;

/**
 * 文本段落元素
 */
@Data
@JSONType(typeName = "paragraph")
public class ParagraphElement extends DocumentElement {
    /**
     * 文本对齐方式
     */
    private String align;

    /**
     * 首行缩进信息
     */
    private String firstLineIndent;
    /**
     * 段落内的文字元素
     */
    private List<TextElement> textElements;
    /**
     * 内容
     */
    private String content;
}

