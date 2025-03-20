package com.doc.format.util.entity;

import lombok.Data;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/12/2 14:46
 */
@Data
public class SimpleWord {
    /**
     * 内容 (content)
     */
    private String c;

    /**
     * 索引 (index)
     */
    private int wi;
}
