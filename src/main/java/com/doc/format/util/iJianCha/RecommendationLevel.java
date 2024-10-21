package com.doc.format.util.iJianCha;

/**
 * <b>RecommendationLevel类表示推荐程度信息的类型和描述</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/8/28 15:28
 */

import lombok.Data;

@Data
public class RecommendationLevel {
    private int type;
    private String description;

    public RecommendationLevel(int type, String description) {
        this.type = type;
        this.description = description;
    }

}
