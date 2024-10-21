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

/**
 * 文本段落元素
 */
@Data
@JSONType(typeName = "text")
public class TextElement extends DocumentElement {
    /**
     * 文本内容
     */
    private String content;

    /**
     * 字体名称
     */
    private String font;

    /**
     * 字号大小
     */
    private String fontSize;

    /**
     * 是否加粗
     */
    private boolean bold;

    /**
     * 是否斜体
     */
    private boolean italic;

    /**
     * 文本颜色
     */
    private String color;

    /**
     * 是否有下划线
     */
    private boolean underline;
    /**
     * 超链接，如果有的话
     */
    private String hyperlink;

    /**
     * 字体是否正确
     */

    private boolean isFontCorrect;

    /**
     * 字体大小是否正确
     */
    private boolean isFontSizeCorrect;
    /**
     * html标识
     */
    private Integer idx;
    /**
     * 在段落中的开始位置
     */
    private Integer startOnPage;
    /**
     * 在段落中的结束位置
     */
    private Integer endOnPage;

}

