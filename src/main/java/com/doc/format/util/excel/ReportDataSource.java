package com.doc.format.util.excel;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/13 15:23
 */
@Data
public class ReportDataSource {
    private long dataSourceSn;
    private String code;
    private String name;
    private Long groupSn;

    private LinkedList<Field> fields;
    private List<FilterField> filterField = new ArrayList<>();
}

