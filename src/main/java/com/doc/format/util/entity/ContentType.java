package com.doc.format.util.entity;

import java.util.ArrayList;
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
 * @date 2024/9/20 09:34
 */
public enum ContentType {
    //中文title，作者姓名，工作单位及通信方式，中文摘要标题，中文摘要内容，英文title，英文作者姓名，英文工作单位及通信方式，英文摘要标题，英文摘要内容，其他项目，章标题，节标题，正文内容，插图和表格标题，表格和图注内容，致谢标题，致谢内容，参考文献标题，参考文献内容，附录标题，附录内容
    CHINESE_TITLE("宋体", "20", "中文题名"),
    AUTHOR_NAME("微软雅黑", "12.5", "作者姓名"),
    WORK_UNIT("华文楷体", "12.5", "工作单位及通信方式"),
    CHINESE_ABSTRACT_TITLE("黑体", "11", "中文摘要标题"),
    CHINESE_ABSTRACT_CONTENT("华文楷体", "11", "中文摘要内容"),
    CHINESE_KEYWORDS_TITLE("黑体", "11", "中文关键词标题"),
    CHINESE_KEYWORDS_CONTENT("华文楷体", "11", "中文关键词内容"),
    ENGLISH_TITLE("Times New Roman", "19", "英文题名"),
    ENGLISH_AUTHOR_NAME("Times New Roman", "12.5", "英文作者姓名"),
    ENGLISH_WORK_UNIT("Times New Roman", "12.5", "英文工作单位及通信方式"),
    ENGLISH_ABSTRACT_TITLE("Times New Roman", "11", "英文摘要标题"),
    ENGLISH_ABSTRACT_CONTENT("Times New Roman", "11", "英文摘要内容"),
    ENGLISH_KEYWORDS_TITLE("Times New Roman", "11", "英文关键词标题"),
    ENGLISH_KEYWORDS_CONTENT("Times New Roman", "11", "英文关键词内容"),
    OTHER_PROJECTS("宋体", "9", "其他项目"),

    // 正文部分
    MAIN_SECTION_TITLE("黑体", "14", "章标题"),
    SUB_SECTION_TITLE("黑体", "12.5", "节标题"),
    MAIN_CONTENT("宋体", "11", "正文内容"),
    IMAGE_TABLE_TITLE("黑体", "9", "插图和表格标题"),
    IMAGE_TABLE_CONTENT("宋体", "9", "表格和图注内容"),

    // 致谢和参考文献
    ACKNOWLEDGEMENT_TITLE("黑体", "11", "致谢标题"),
    ACKNOWLEDGEMENT_CONTENT("楷体", "11", "致谢内容"),
    REFERENCES_TITLE("黑体", "14", "参考文献标题"),
    REFERENCES_CONTENT("宋体", "11", "参考文献内容"),

    // 附录部分
    APPENDIX_TITLE("黑体", "14", "附录标题"),
    APPENDIX_CONTENT("宋体", "11", "附录内容");

    private final String expectedFont;
    private final String expectedFontSize;
    private final String description;

    ContentType(String expectedFont, String expectedFontSize, String description) {
        this.expectedFont = expectedFont;
        this.expectedFontSize = expectedFontSize;
        this.description = description;
    }

    public String getExpectedFont() {
        return expectedFont;
    }

    public String getExpectedFontSize() {
        return expectedFontSize;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 校验 TextElement 的字体和字号，并返回包含 TextElement 列表的 ValidationResult
     */
    public ValidationResult validate(TextElement textElement,  int pIndex,int sectionIndex ) {
        // 检测内容是否存在
        boolean isContentDetected = textElement.getContent() != null;

        // 校验字体和字号，并在 TextElement 中设置对应字段
        textElement.setFontCorrect(textElement.getFont().equals(this.expectedFont));
        textElement.setFontSizeCorrect(textElement.getFontSize().equals(this.expectedFontSize));

        // 创建 ValidationResult，并将该 TextElement 加入到 textElements 列表
        List<TextElement> textElementList = new ArrayList<>();
        textElementList.add(textElement);

        return new ValidationResult(
                this.description,          // 文档元素名称
                this.name(),                // 文档元素类型名称
                isContentDetected,          // 是否检测到内容
                pIndex,                      // 文档元素在 DocumentElement 列表中的位置
                sectionIndex,                      // 文档元素在 DocumentElement 列表中的位置
                textElementList             // 包含检测结果的 TextElement 列表
        );
    }
}
