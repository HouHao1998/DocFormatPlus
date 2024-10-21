package com.doc.format.util.iJianCha;

/**
 * <b>请输入名称</b>
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

/**
 * CheckRequest类用于构造发送给校对接口的请求体。
 */
@Data
public class CheckRequest {
    /**
     * 要校对的文本
     */
    private String text;
    /**
     * 校对模式，默认为2（检查全部错误）
     */
    private int checkMode = 2;
    /**
     * 校对功能参数，默认为-1，表示使用默认的校对功能集合
     */
    private int checkFunctions = -1;

    /**
     * 构造函数，仅传入文本。
     *
     * @param text 要校对的文本
     */
    public CheckRequest(String text) {
        this.text = text;
    }

    /**
     * 构造函数，传入文本、校对模式和校对功能参数。
     *
     * @param text           要校对的文本
     * @param checkMode      校对模式
     * @param checkFunctions 校对功能参数
     */
    public CheckRequest(String text, int checkMode, int checkFunctions) {
        this.text = text;
        this.checkMode = checkMode;
        this.checkFunctions = checkFunctions;
    }

}
