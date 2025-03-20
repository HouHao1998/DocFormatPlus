package com.doc.format.enums;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2025/3/20 13:46
 */
/**
 * 解析过程状态枚举
 */
public enum CheckStatusEnum {
    /**
     * 文件已上传
     */
    UPLOADED("UPLOADED", "文件已上传"),

    /**
     * 数据已校验
     */
    VERIFIED("VERIFIED", "数据已校验"),

    /**
     * 结果已导出
     */
    EXPORTED("EXPORTED", "结果已导出"),

    /**
     * 内容已格式化
     */
    FORMATTED("FORMATTED", "内容已格式化");

    private final String code;
    private final String description;

    CheckStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
