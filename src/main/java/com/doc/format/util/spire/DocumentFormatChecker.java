package com.doc.format.util.spire;

import com.doc.format.util.docx4j.JsonToHtmlConverter;
import com.doc.format.util.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spire.doc.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.UnderlineStyle;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.Field;
import com.spire.doc.fields.TextRange;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;


/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/24 18:20
 */

public class DocumentFormatChecker {
    private static final Pattern CHAPTER_PATTERN = Pattern.compile("^\\d+$"); // 匹配章编号，如 "1", "2", "3"
    private static final Pattern SECTION_PATTERN = Pattern.compile("^\\d+\\.\\d+$"); // 匹配节编号，如 "1.1", "2.1"
    // 用于匹配参考文献内容的正则表达式，检测是否以 [数字] 开头
    private static final Pattern REFERENCE_PATTERN = Pattern.compile("^\\[\\d+\\]");
    // 正则表达式用于检测段落是否由英文或数字组成
    private static final Pattern ENGLISH_OR_NUMBER_PATTERN = Pattern.compile("^[A-Za-z0-9\\s:,.]+$");

    /**
     * 主方法，遍历 List<DocumentElement> 并调用相关的检测方法
     */
    public static void checkDocumentContent(List<DocumentElement> documentElements, List<ValidationResult> validationResults) {
        int index = 0;

        // 处理第一段：中文标题
        if (index < documentElements.size() && documentElements.get(index) instanceof ParagraphElement) {

            ParagraphElement titleParagraph = (ParagraphElement) documentElements.get(index);
            if (titleParagraph.getTextElements() == null || titleParagraph.getTextElements().isEmpty()) {
                index++;
                titleParagraph = (ParagraphElement) documentElements.get(index);
            }
            for (int i = 0; i < titleParagraph.getTextElements().size(); i++) {
                validationResults.add(ContentType.CHINESE_TITLE.validate(titleParagraph.getTextElements().get(i), index));
            }
        } else {
            validationResults.add(new ValidationResult(
                    ContentType.CHINESE_TITLE.getDescription(),
                    ContentType.CHINESE_TITLE.name(),
                    false,
                    index,
                    new ArrayList<>()
            ));
        }
        index++;

        // 处理第二段：作者姓名
        if (index < documentElements.size() && documentElements.get(index) instanceof ParagraphElement) {
            ParagraphElement authorParagraph = (ParagraphElement) documentElements.get(index);
            if (authorParagraph.getTextElements() == null || authorParagraph.getTextElements().isEmpty()) {
                index++;
                authorParagraph = (ParagraphElement) documentElements.get(index);
            }
            for (int i = 0; i < authorParagraph.getTextElements().size(); i++) {
                validationResults.add(ContentType.AUTHOR_NAME.validate(authorParagraph.getTextElements().get(i), index));
            }
        } else {
            validationResults.add(new ValidationResult(
                    ContentType.AUTHOR_NAME.getDescription(),
                    ContentType.AUTHOR_NAME.name(),
                    false,
                    index,
                    new ArrayList<>()
            ));
        }
        index++;

        // 处理第三段：工作单位
        if (index < documentElements.size() && documentElements.get(index) instanceof ParagraphElement) {
            ParagraphElement workUnitParagraph = (ParagraphElement) documentElements.get(index);
            if (workUnitParagraph.getTextElements() == null || workUnitParagraph.getTextElements().isEmpty()) {
                index++;
                workUnitParagraph = (ParagraphElement) documentElements.get(index);
            }
            for (int i = 0; i < workUnitParagraph.getTextElements().size(); i++) {
                validationResults.add(ContentType.WORK_UNIT.validate(workUnitParagraph.getTextElements().get(i), index));
            }
        } else {
            validationResults.add(new ValidationResult(
                    ContentType.WORK_UNIT.getDescription(),
                    ContentType.WORK_UNIT.name(),
                    false,
                    index,
                    new ArrayList<>()
            ));
        }
        index++;

        // 遍历剩下的段落，检测摘要、关键词、以及英文内容
        for (; index < documentElements.size(); index++) {
            DocumentElement element = documentElements.get(index);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();

                // 检测中文摘要和关键词
                checkParagraphContent(paragraph, index, validationResults);

                // 如果找到中文关键词之后，开始检测英文部分
                if (content.contains("关键词")) {
                    // 检测英文标题、英文作者、英文组织
                    checkEnglishTitleAuthorOrg(documentElements, index + 1, validationResults);
                    break;  // 检测完成后跳出循环
                }
            }
        }

        // 继续遍历剩余的英文摘要和关键词段落
        for (; index < documentElements.size(); index++) {
            DocumentElement element = documentElements.get(index);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                // 调用英文摘要和关键词检测方法
                checkEnglishParagraphContent(paragraph, index, validationResults);
                // 检测英文关键词之后的 "其他项目"
                checkOtherProjects(documentElements, index, validationResults);
            }
        }
        //判断段落主体是否符合要求
        checkContentSections(documentElements, validationResults);
        //检测插图和表格的编号和标题
        checkTableAndImageCaptions(documentElements, validationResults);
        //检测参考文献的标题和内容
        checkReferences(documentElements, validationResults);

    }

    /**
     * 检测参考文献的标题和内容
     */
    public static void checkReferences(List<DocumentElement> documentElements, List<ValidationResult> validationResults) {
        boolean foundReferenceTitle = false;

        for (int i = 0; i < documentElements.size(); i++) {
            DocumentElement element = documentElements.get(i);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();
                List<TextElement> textElements = paragraph.getTextElements();

                // 检测参考文献标题
                if (!foundReferenceTitle && content.equals("参考文献")) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.REFERENCES_TITLE);  // 小四号 黑体
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.REFERENCES_TITLE.getDescription(),
                            ContentType.REFERENCES_TITLE.name(),
                            true,
                            i,
                            textElements
                    ));
                    foundReferenceTitle = true;
                    continue;  // 跳过本段，继续检测内容部分
                }

                // 检测参考文献内容
                if (foundReferenceTitle && REFERENCE_PATTERN.matcher(content).find()) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.REFERENCES_CONTENT);  // 小五号 宋体
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.REFERENCES_CONTENT.getDescription(),
                            ContentType.REFERENCES_CONTENT.name(),
                            true,
                            i,
                            textElements
                    ));
                }
            }
        }
    }

    /**
     * 检测插图和表格的编号和标题
     */
    public static void checkTableAndImageCaptions(List<DocumentElement> documentElements, List<ValidationResult> validationResults) {
        for (int i = 0; i < documentElements.size(); i++) {
            DocumentElement element = documentElements.get(i);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();
                String align = paragraph.getAlign();
                List<TextElement> textElements = paragraph.getTextElements();

                // 检测段落是否居中，且内容包含 "图" 或 "表"
                if (isCentered(align) && (content.contains("图") || content.contains("表"))) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.IMAGE_TABLE_TITLE); // 小五号 黑体 9 pt
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.IMAGE_TABLE_TITLE.getDescription(),
                            ContentType.IMAGE_TABLE_TITLE.name(),
                            true,
                            i,
                            textElements
                    ));
                }
            }
        }
    }

    // 判断段落是否居中
    private static boolean isCentered(String align) {
        return align != null && align.equalsIgnoreCase("center");
    }

    private static void checkParagraphContent(ParagraphElement paragraph, int index, List<ValidationResult> validationResults) {
        List<TextElement> textElements = paragraph.getTextElements();
        boolean isTitleDetected = false;
        boolean isContentDetected = false;
        boolean isKeywordDetected = false;
        List<TextElement> titleTextElements = new ArrayList<>();
        List<TextElement> contentTextElements = new ArrayList<>();
        List<TextElement> keywordTitleTextElements = new ArrayList<>();
        List<TextElement> keywordContentTextElements = new ArrayList<>();

        // 查找摘要标题，定位到冒号之前的部分作为标题，之后的部分作为内容
        String content = paragraph.getContent();
        int colonIndex = Math.max(content.indexOf("："), content.indexOf(":"));

        // 检测是否包含“摘要”或“摘 要”
        if ((content.contains("摘要") || content.startsWith("摘 要")) && colonIndex != -1) {
            // 提取摘要标题部分
            for (TextElement textElement : textElements) {
                if (textElement.getEndOnPage() <= colonIndex + 1) {
                    titleTextElements.add(textElement); // 摘要标题部分
                } else {
                    contentTextElements.add(textElement);  // 剩余的部分作为摘要内容
                }
            }
            isTitleDetected = true;
            isContentDetected = !contentTextElements.isEmpty();
        }

        // 检测是否包含“关键词”并分割为标题和内容
        if (content.contains("关键词")) {
            // 查找“关键词”的位置，分离标题和内容
            int keywordIndex = content.indexOf("关键词");
            int keywordColonIndex = Math.max(content.indexOf("：", keywordIndex), content.indexOf(":", keywordIndex));
            for (TextElement textElement : textElements) {
                if (textElement.getEndOnPage() <= keywordColonIndex + 1) {
                    keywordTitleTextElements.add(textElement);  // 关键词标题部分
                } else {
                    keywordContentTextElements.add(textElement);  // 剩余的部分作为关键词内容
                }
            }
            isKeywordDetected = true;
        }

        // 检测摘要标题部分
        if (isTitleDetected) {
            for (TextElement textElement : titleTextElements) {
                validateTextElement(textElement, ContentType.CHINESE_ABSTRACT_TITLE);
            }
            validationResults.add(new ValidationResult(
                    ContentType.CHINESE_ABSTRACT_TITLE.getDescription(),
                    ContentType.CHINESE_ABSTRACT_TITLE.name(),
                    true,
                    index,
                    titleTextElements
            ));
        }

        // 检测摘要内容部分
        if (isContentDetected) {
            for (TextElement textElement : contentTextElements) {
                validateTextElement(textElement, ContentType.CHINESE_ABSTRACT_CONTENT);
            }
            validationResults.add(new ValidationResult(
                    ContentType.CHINESE_ABSTRACT_CONTENT.getDescription(),
                    ContentType.CHINESE_ABSTRACT_CONTENT.name(),
                    true,
                    index,
                    contentTextElements
            ));
        }

        // 检测关键词标题部分
        if (isKeywordDetected && !keywordTitleTextElements.isEmpty()) {
            for (TextElement textElement : keywordTitleTextElements) {
                validateTextElement(textElement, ContentType.CHINESE_KEYWORDS_TITLE);  // 新增关键词标题的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.CHINESE_KEYWORDS_TITLE.getDescription(),
                    ContentType.CHINESE_KEYWORDS_TITLE.name(),
                    true,
                    index,
                    keywordTitleTextElements
            ));
        }

        // 检测关键词内容部分
        if (isKeywordDetected && !keywordContentTextElements.isEmpty()) {
            for (TextElement textElement : keywordContentTextElements) {
                validateTextElement(textElement, ContentType.CHINESE_KEYWORDS_CONTENT);  // 新增关键词内容的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.CHINESE_KEYWORDS_CONTENT.getDescription(),
                    ContentType.CHINESE_KEYWORDS_CONTENT.name(),
                    true,
                    index,
                    keywordContentTextElements
            ));
        }
    }

    // 校验 TextElement 的字体和字号
    private static void validateTextElement(TextElement textElement, ContentType contentType) {
        textElement.setFontCorrect(textElement.getFont().equals(contentType.getExpectedFont()));
        textElement.setFontSizeCorrect(textElement.getFontSize().equals(contentType.getExpectedFontSize()));
    }

    /**
     * 检测段落是否为英文关键词或英文摘要，并分别处理标题和内容
     */
    private static void checkEnglishParagraphContent(ParagraphElement paragraph, int index, List<ValidationResult> validationResults) {
        List<TextElement> textElements = paragraph.getTextElements();
        boolean isEnglishDetected = false;
        boolean isEnglishAbstractDetected = false;
        boolean isEnglishKeywordDetected = false;
        List<TextElement> abstractTitleTextElements = new ArrayList<>();
        List<TextElement> abstractContentTextElements = new ArrayList<>();
        List<TextElement> keywordTitleTextElements = new ArrayList<>();
        List<TextElement> keywordContentTextElements = new ArrayList<>();

        // 提取段落内容
        String content = paragraph.getContent().trim();

        // 首先检测该段落是否全部由英文或数字组成
        if (isEnglishOrNumber(content)) {
            isEnglishDetected = true;

            // 检测是否包含英文摘要 "Abstract:"
            if (content.startsWith("Abstract:")) {
                // 查找冒号位置并分割为摘要标题和内容
                int colonIndex = content.indexOf(":");
                for (TextElement textElement : textElements) {
                    if (textElement.getEndOnPage() <= colonIndex + 1) {
                        abstractTitleTextElements.add(textElement);  // 摘要标题部分
                    } else {
                        abstractContentTextElements.add(textElement);  // 剩余的部分作为摘要内容
                    }
                }
                isEnglishAbstractDetected = true;
            }

            // 检测是否包含英文关键词 "Key words:"
            if (content.startsWith("Key words:")) {
                // 查找冒号位置并分割为关键词标题和内容
                int colonIndex = content.indexOf(":");
                for (TextElement textElement : textElements) {
                    if (textElement.getEndOnPage() <= colonIndex + 1) {
                        keywordTitleTextElements.add(textElement);  // 关键词标题部分
                    } else {
                        keywordContentTextElements.add(textElement);  // 剩余的部分作为关键词内容
                    }
                }
                isEnglishKeywordDetected = true;
            }
        }

        // 如果检测到英文摘要标题
        if (isEnglishAbstractDetected && !abstractTitleTextElements.isEmpty()) {
            for (TextElement textElement : abstractTitleTextElements) {
                validateTextElement(textElement, ContentType.ENGLISH_ABSTRACT_TITLE);  // 新增英文摘要标题的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.ENGLISH_ABSTRACT_TITLE.getDescription(),
                    ContentType.ENGLISH_ABSTRACT_TITLE.name(),
                    true,
                    index,
                    abstractTitleTextElements
            ));
        }

        // 如果检测到英文摘要内容
        if (isEnglishAbstractDetected && !abstractContentTextElements.isEmpty()) {
            for (TextElement textElement : abstractContentTextElements) {
                validateTextElement(textElement, ContentType.ENGLISH_ABSTRACT_CONTENT);  // 新增英文摘要内容的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.ENGLISH_ABSTRACT_CONTENT.getDescription(),
                    ContentType.ENGLISH_ABSTRACT_CONTENT.name(),
                    true,
                    index,
                    abstractContentTextElements
            ));
        }

        // 如果检测到英文关键词标题
        if (isEnglishKeywordDetected && !keywordTitleTextElements.isEmpty()) {
            for (TextElement textElement : keywordTitleTextElements) {
                validateTextElement(textElement, ContentType.ENGLISH_KEYWORDS_TITLE);  // 新增英文关键词标题的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.ENGLISH_KEYWORDS_TITLE.getDescription(),
                    ContentType.ENGLISH_KEYWORDS_TITLE.name(),
                    true,
                    index,
                    keywordTitleTextElements
            ));
        }

        // 如果检测到英文关键词内容
        if (isEnglishKeywordDetected && !keywordContentTextElements.isEmpty()) {
            for (TextElement textElement : keywordContentTextElements) {
                validateTextElement(textElement, ContentType.ENGLISH_KEYWORDS_CONTENT);  // 新增英文关键词内容的 ContentType
            }
            validationResults.add(new ValidationResult(
                    ContentType.ENGLISH_KEYWORDS_CONTENT.getDescription(),
                    ContentType.ENGLISH_KEYWORDS_CONTENT.name(),
                    true,
                    index,
                    keywordContentTextElements
            ));
        }
    }

    // 检测段落是否全部由英文或数字组成
    private static boolean isEnglishOrNumber(String content) {
        return ENGLISH_OR_NUMBER_PATTERN.matcher(content).matches();
    }

    private static void checkOtherProjects(List<DocumentElement> documentElements, int startIndex, List<ValidationResult> validationResults) {
        for (int i = startIndex; i < documentElements.size(); i++) {
            DocumentElement element = documentElements.get(i);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();

                // 检测是否包含 "项目："
                if (content.contains("项目：")) {
                    List<TextElement> textElements = paragraph.getTextElements();
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.OTHER_PROJECTS);  // 其他项目
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.OTHER_PROJECTS.getDescription(),
                            ContentType.OTHER_PROJECTS.name(),
                            true,
                            i,
                            textElements
                    ));
                    break;  // 找到 "其他项目" 后即可结束
                }
            }
        }
    }

    // 用于检测英文标题、英文作者和英文工作单位
    private static void checkEnglishTitleAuthorOrg(List<DocumentElement> documentElements, int startIndex, List<ValidationResult> validationResults) {
        boolean foundEnglishTitle = false;
        boolean foundEnglishAuthor = false;
        boolean foundEnglishOrg = false;

        for (int i = startIndex; i < documentElements.size(); i++) {
            DocumentElement element = documentElements.get(i);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();
                List<TextElement> textElements = paragraph.getTextElements();

                // 检测是否为英文标题
                if (!foundEnglishTitle && isEnglishTitle(content)) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.ENGLISH_TITLE);  // 英文标题
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.ENGLISH_TITLE.getDescription(),
                            ContentType.ENGLISH_TITLE.name(),
                            true,
                            i,
                            textElements
                    ));
                    foundEnglishTitle = true;
                    continue;
                }

                // 检测是否为英文作者
                if (foundEnglishTitle && !foundEnglishAuthor && isEnglishText(content)) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.ENGLISH_AUTHOR_NAME);  // 英文作者
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.ENGLISH_AUTHOR_NAME.getDescription(),
                            ContentType.ENGLISH_AUTHOR_NAME.name(),
                            true,
                            i,
                            textElements
                    ));
                    foundEnglishAuthor = true;
                    continue;
                }

                // 检测是否为英文组织
                if (foundEnglishAuthor && !foundEnglishOrg && isEnglishText(content)) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.ENGLISH_WORK_UNIT);  // 英文组织
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.ENGLISH_WORK_UNIT.getDescription(),
                            ContentType.ENGLISH_WORK_UNIT.name(),
                            true,
                            i,
                            textElements
                    ));
                    foundEnglishOrg = true;
                    break;  // 检测完毕，退出循环
                }
            }
        }
    }

    /**
     * 检测引言、主体、结论的段落信息
     */
    public static void checkContentSections(List<DocumentElement> documentElements, List<ValidationResult> validationResults) {
        for (int i = 0; i < documentElements.size(); i++) {
            DocumentElement element = documentElements.get(i);
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraph = (ParagraphElement) element;
                String content = paragraph.getContent().trim();
                String firstLineIndent = paragraph.getFirstLineIndent();
                List<TextElement> textElements = paragraph.getTextElements();

                // 章编号和标题检测
                if (hasNoFirstLineIndent(firstLineIndent) && CHAPTER_PATTERN.matcher(content).matches()) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.MAIN_SECTION_TITLE); // 小四号 黑体
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.MAIN_SECTION_TITLE.getDescription(),
                            ContentType.MAIN_SECTION_TITLE.name(),
                            true,
                            i,
                            textElements
                    ));
                }

                // 节编号和标题检测
                else if (hasNoFirstLineIndent(firstLineIndent) && SECTION_PATTERN.matcher(content).matches()) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.SUB_SECTION_TITLE); // 五号 黑体
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.SUB_SECTION_TITLE.getDescription(),
                            ContentType.SUB_SECTION_TITLE.name(),
                            true,
                            i,
                            textElements
                    ));
                }

                // 正文内容检测
                else if (hasFirstLineIndent(firstLineIndent)) {
                    for (TextElement textElement : textElements) {
                        validateTextElement(textElement, ContentType.MAIN_CONTENT); // 五号 宋体
                    }
                    validationResults.add(new ValidationResult(
                            ContentType.MAIN_CONTENT.getDescription(),
                            ContentType.MAIN_CONTENT.name(),
                            true,
                            i,
                            textElements
                    ));
                }
            }
        }
    }

    // 判断段落是否为英文标题
    private static boolean isEnglishTitle(String content) {
        return content.matches("^[A-Z][A-Za-z\\s]+$");
    }

    // 判断段落是否为英文文本（用于英文作者和英文组织）
    private static boolean isEnglishText(String content) {
        return content.matches("^[A-Za-z\\s]+$");
    }

    // 判断是否有首行缩进
    private static boolean hasFirstLineIndent(String firstLineIndent) {
        return firstLineIndent != null && !firstLineIndent.equals("0") && !firstLineIndent.equals("0.0");
    }

    // 判断是否没有首行缩进
    private static boolean hasNoFirstLineIndent(String firstLineIndent) {
        return firstLineIndent == null || firstLineIndent.equals("0") || firstLineIndent.equals("0.0");
    }

}