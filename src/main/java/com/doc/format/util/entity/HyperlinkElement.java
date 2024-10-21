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
 * @date 2024/9/20 09:38
 */

import lombok.Data;

/**
 * 超链接元素
 */
@Data
public class HyperlinkElement extends DocumentElement {
    /**
     * 超链接的文本内容
     */
    private String content;

    /**
     * 超链接的URL
     */
    private String url;
}
