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
 * @date 2024/9/20 09:37
 */

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

import java.util.List;

/**
 * 表格元素
 */
@Data
@JSONType(typeName = "table")
public class TableElement extends DocumentElement {
    /**
     * 表格内容，按行存储
     */
    private List<List<String>> tableContent;
}
