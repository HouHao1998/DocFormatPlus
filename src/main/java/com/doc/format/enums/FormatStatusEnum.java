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
public enum FormatStatusEnum {
    /**
     * 文件已上传
     * 作用：表示文件已成功上传到服务器。
     * 限制：仅表示上传完成，未进行后续处理。
     */
    UPLOADED("UPLOADED", "文件已上传"),

    /**
     * 数据已经解析
     * 作用：表示文件内容已解析为结构化数据。
     * 限制：解析结果需进一步校验和处理。
     */
    VERIFIED("VERIFIED", "数据已校验"),

    /**
     * 数据解析失败
     * 作用：表示文件内容解析失败。
     * 限制：需检查文件格式或内容是否符合要求。
     */
    PARSE_FAILED("PARSE_FAILED", "数据解析失败"),

    /**
     * 已经调用deepSeek
     * 作用：表示已调用deepSeek接口进行处理。
     * 限制：需确认接口调用是否成功。
     */
    DEEPSEEK_CALLED("DEEPSEEK_CALLED", "已经调用deepSeek"),

    /**
     * 接口调用失败
     * 作用：表示调用deepSeek接口失败。
     * 限制：需检查网络或接口配置。
     */
    API_CALL_FAILED("API_CALL_FAILED", "接口调用失败"),

    /**
     * 结果已导出
     * 作用：表示格式化结果已导出为文件。
     * 限制：导出文件需进一步验证其正确性。
     */
    EXPORTED("EXPORTED", "结果已导出"),

    /**
     * 内容已格式化
     * 作用：表示文档内容已完成格式化处理。
     * 限制：格式化结果需进一步确认是否符合要求。
     */
    FORMATTED("FORMATTED", "内容已格式化"),

    /**
     * 格式化调用失败
     * 作用：表示文档格式化处理失败。
     * 限制：需检查格式化逻辑或输入数据。
     */
    FORMAT_FAILED("FORMAT_FAILED", "格式化调用失败");


    private final String code;
    private final String description;

    FormatStatusEnum(String code, String description) {
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
