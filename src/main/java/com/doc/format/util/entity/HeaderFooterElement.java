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
 * @date 2024/9/20 09:39
 */

import lombok.Data;

/**
 * 页眉或页脚元素
 */
@Data
public class HeaderFooterElement extends DocumentElement {
    /**
     * 页眉或页脚的文本内容
     */
    private String content;
}

