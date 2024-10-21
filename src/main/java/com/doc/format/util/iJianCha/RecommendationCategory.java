package com.doc.format.util.iJianCha;

/**
 * <b>表示推荐类别信息的代码和描述</b>
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
public class RecommendationCategory {
    private String code;
    private String description;

    public RecommendationCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }

}
