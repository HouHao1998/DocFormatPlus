package com.doc.format.util.user;

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
 * @date 2025/3/24 20:58
 */
@Data // 使用Lombok注解
public class User {
    private Long id;
    private String name;
    // 其他字段（按需保留）
}
