package com.doc.format.util.excel;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <b>数据集选项</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/6/4 16:31
 */
@Data
public class FilterField implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 过滤字段名称
     */
    private String fieldName;
    /**
     * 过滤字段备注
     */
    private String fieldComment;
    /**
     * 过滤字段类型
     */
    private String fieldType;
    /**
     * 选项类型*CUSTOM**SINGLE**MULTI
     */
    private String inputType;
    /**
     * 对应值
     */
    private String value;
    /**
     * 选项列表
     */
    private List<String> option;
}
