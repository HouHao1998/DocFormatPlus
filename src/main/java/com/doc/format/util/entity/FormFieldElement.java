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
 * @date 2024/9/20 09:40
 */

import lombok.Data;

/**
 * 表单域元素
 */
@Data
public class FormFieldElement extends DocumentElement {
    /**
     * 表单域的文本内容
     */
    private String content;
}

