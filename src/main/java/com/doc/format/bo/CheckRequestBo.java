package com.doc.format.bo;

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
 * @date 2025/3/24 11:12
 */
@Data
public class CheckRequestBo {
    private String text;
    private Integer id;
}
