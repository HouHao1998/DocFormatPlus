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
public enum DocElementTypeEnum {
    // 单一样式的枚举项
    CN_TITLE("CN_TITLE", "文档首个段落，通常为中文标题", "黑体", "小二号", "19"),
    AUTHOR_NAME("AUTHOR_NAME", "作者姓名列表", "楷体", "小四号", "12.5"),
    AFFILIATION("AFFILIATION", "工作单位及通信方式", "宋体", "小五号", "9"),
    EN_TITLE("EN_TITLE", "英文标题段落", "黑体", "四号", "14"),
    EN_AUTHOR_NAME("EN_AUTHOR_NAME", "英文作者姓名", "宋体", "五号", "11"),
    EN_AFFILIATION("EN_AFFILIATION", "英文工作单位信息", "宋体", "小五号", "9"),
    CHAPTER_TITLE("CHAPTER_TITLE", "\"第X章\"或数字编号开头的段落", "黑体", "小四号", "14"),
    SECTION_TITLE("SECTION_TITLE", "节编号标题（如1.1）", "黑体", "五号", "11"),
    BODY_CONTENT("BODY_CONTENT", "正文段落内容", "宋体", "五号", "11"),
    FIGURE_CAPTION("FIGURE_CAPTION", "插图编号和说明", "黑体", "小五号", "9"),
    TABLE_CAPTION("TABLE_CAPTION", "表格编号和标题", "黑体", "小五号", "9"),

    // 需要拆分的枚举项：中文摘要和关键词
    CN_ABSTRACT_TITLE("CN_ABSTRACT_TITLE", "包含\"摘要：\"开头的段落 - 引题（即摘要两个字及后续部分）", "黑体", "引题小五", "9"),
    CN_ABSTRACT_CONTENT("CN_ABSTRACT_CONTENT", "包含\"摘要：\"开头的段落 - 内容", "仿宋", "内容小五", "9"),
    CN_KEYWORDS_TITLE("CN_KEYWORDS_TITLE", "包含\"关键词：\"开头的段落 - 引题（即关键词三个字及后续部分）", "黑体", "引题小五", "9"),
    CN_KEYWORDS_CONTENT("CN_KEYWORDS_CONTENT", "包含\"关键词：\"开头的段落 - 内容", "仿宋", "内容小五", "9"),

    // 英文摘要和关键词也相应拆分
    EN_ABSTRACT_TITLE("EN_ABSTRACT_TITLE", "英文摘要内容 - 引题", "黑体", "引题小五", "9"),
    EN_ABSTRACT_CONTENT("EN_ABSTRACT_CONTENT", "英文摘要内容 - 内容", "宋体", "内容小五", "9"),
    EN_KEYWORDS_TITLE("EN_KEYWORDS_TITLE", "英文关键词段落 - 引题", "黑体", "引题小五", "9"),
    EN_KEYWORDS_CONTENT("EN_KEYWORDS_CONTENT", "英文关键词段落 - 内容", "宋体", "内容小五", "9"),

    // 需要拆分的枚举项：致谢、参考文献、附录
    ACKNOWLEDGMENT_TITLE("ACKNOWLEDGMENT_TITLE", "致谢段落 - 引题", "黑体", "五号", "11"),
    ACKNOWLEDGMENT_CONTENT("ACKNOWLEDGMENT_CONTENT", "致谢段落 - 内容", "楷体", "五号", "11"),
    REFERENCE_TITLE("REFERENCE_TITLE", "参考文献列表 引题（及章编号）", "黑体", "小四", "12.5"),
    REFERENCE_CONTENT("REFERENCE_CONTENT", "参考文献列表 - 内容", "宋体", "小五", "9"),
    APPENDIX_TITLE("APPENDIX_TITLE", "附录内容 - 编号、标题", "黑体", "小四", "9"),
    APPENDIX_CONTENT("APPENDIX_CONTENT", "附录内容 - 内容", "宋体", "小五", "9");

    private final String typeName;
    private final String feature;
    private final String font;      // 字体
    private final String fontSize;  // 字号
    private final String layoutSize; // 版面大小（不含“pt”）

    DocElementTypeEnum(String typeName, String feature, String font, String fontSize, String layoutSize) {
        this.typeName = typeName;
        this.feature = feature;
        this.font = font;
        this.fontSize = fontSize;
        this.layoutSize = layoutSize;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getFeature() {
        return feature;
    }

    public String getFont() {
        return font;
    }

    public String getFontSize() {
        return fontSize;
    }

    public String getLayoutSize() {
        return layoutSize;
    }

    public String getDescription() {
        return String.format("%s（字体：%s，字号：%s，版面大小：%s）", typeName, font, fontSize, layoutSize);
    }
}
