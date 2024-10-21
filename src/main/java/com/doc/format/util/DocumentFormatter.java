package com.doc.format.util;

import com.doc.format.domain.ContentFields;
import com.spire.doc.FileFormat;

import org.apache.commons.io.FilenameUtils;

import java.io.FileOutputStream;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.spire.doc.Document;

import org.apache.poi.xwpf.usermodel.*;


public class DocumentFormatter {

    public static void setRunFont(XWPFRun run, String fontName, int fontSize) {
        run.setFontFamily(fontName);
        run.setFontSize(fontSize);
    }


    public static String applyFormatting(String filePath, Map<String, Object> content, String originalFilename) throws IOException {
        if (filePath.endsWith(".doc")) {
            String docxPath = filePath.replace(".doc", ".docx");
            convertDocToDocx(filePath, docxPath);
            filePath = docxPath;
        }
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(filePath)))) {
            applyFormattingToDocx(doc, content);
            return saveDocxFile(doc, originalFilename);
        }
    }

    private static void applyFormattingToDocx(XWPFDocument doc, Map<String, Object> content) {
        // 中文题名
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("中文题名"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 19);
                }
            }
        }

        // 作者姓名
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("作者姓名"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "楷体", 12);
                }
            }
        }

        // 工作单位及通信方式
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("工作单位及通信方式"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "宋体", 9);
                }
            }
        }

        // 中文摘要和关键词
        Map<String, String> chineseAbstract = (Map<String, String>) content.get("中文摘要");
        Map<String, String> chineseKeywords = (Map<String, String>) content.get("中文关键词");

        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().contains(chineseAbstract.get("引题"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 9);
                }
            }
            if (para.getText().contains(chineseAbstract.get("内容"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "仿宋", 9);
                }
            }
            if (para.getText().contains(chineseKeywords.get("引题"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 9);
                }
            }
            if (para.getText().contains(chineseKeywords.get("内容"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "仿宋", 9);
                }
            }
        }

        // 英文题名
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("英文题名"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 14);
                }
            }
        }

        // 英文作者姓名
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("英文作者姓名"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "宋体", 11);
                }
            }
        }

        // 英文工作单位及通信方式
        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().trim().equals(content.get("英文工作单位及通信方式"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "宋体", 9);
                }
            }
        }

        // 英文摘要、关键词
        Map<String, String> englishAbstract = (Map<String, String>) content.get("英文摘要");
        Map<String, String> englishKeywords = (Map<String, String>) content.get("英文关键词");

        for (XWPFParagraph para : doc.getParagraphs()) {
            if (para.getText().contains(englishAbstract.get("引题"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 9);
                }
            }
            if (para.getText().contains(englishAbstract.get("内容")) || para.getText().contains(englishKeywords.get("内容"))) {
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "宋体", 9);
                }
            }
        }

        // 章节编号和标题
        List<String> chapters = (List<String>) content.get("章节编号和标题");
        for (String chapter : chapters) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                if (para.getText().trim().equals(chapter)) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "黑体", 14);
                    }
                }
            }
        }

        // 节编号和标题
        List<String> sections = (List<String>) content.get("节编号和标题");
        for (String section : sections) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                if (para.getText().trim().equals(section)) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "黑体", 11);
                    }
                }
            }
        }

        // 正文内容
        List<String> fullContent = (List<String>) content.get("正文内容");
        for (String text : fullContent) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                if (para.getText().trim().equals(text)) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "宋体", 11);
                    }
                }
            }
        }

        // 表格内容
        for (XWPFTable table : doc.getTables()) {
            for (XWPFTableRow row : table.getRows()) {
                for (XWPFTableCell cell : row.getTableCells()) {
                    for (XWPFParagraph para : cell.getParagraphs()) {
                        for (XWPFRun run : para.getRuns()) {
                            setRunFont(run, "宋体", 9);
                        }
                    }
                }
            }
        }

        // 插图、表格编号和标题
        List<Map<String, String>> tables = (List<Map<String, String>>) content.get("表格");
        for (Map<String, String> table : tables) {
            for (XWPFParagraph para : doc.getParagraphs()) {
                if (para.getText().trim().equals(table.get("编号和标题"))) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "黑体", 9);
                    }
                }
                if (para.getText().trim().equals(table.get("表注"))) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "宋体", 9);
                    }
                }
            }
        }

        // 参考文献
        boolean insideReferences = false;
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText().trim();
            if (text.startsWith("参考文献")) {
                insideReferences = true;
                for (XWPFRun run : para.getRuns()) {
                    setRunFont(run, "黑体", 12);
                }
            } else if (insideReferences) {
                if (text.matches("^\\[\\d+\\]")) {
                    for (XWPFRun run : para.getRuns()) {
                        setRunFont(run, "宋体", 9);
                    }
                }
            }
        }
    }


    private static String saveDocxFile(XWPFDocument doc, String originalFilename) throws IOException {
        String uniqueId = UUID.randomUUID().toString();
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String outputDir = "/Users/houhao/Downloads/" + dateStr + "/" + uniqueId;
        Files.createDirectories(Paths.get(outputDir));

        String originalBaseName = FilenameUtils.getBaseName(originalFilename);
        String formattedFilename = originalBaseName + "格式化.docx";
        String outputPath = outputDir + "/" + formattedFilename;

        try (FileOutputStream out = new FileOutputStream(outputPath)) {
            doc.write(out);
        }

        return outputPath;
    }


    public static List<String> extractChapterTitles(XWPFDocument doc) {
        List<String> chapters = new ArrayList<>();
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText().trim();
            if (text.matches("^\\d+[^.\\s].*") && text.length() <= 50) {
                chapters.add(text);
            }
        }
        return chapters;
    }

    public static List<String> extractSectionTitles(XWPFDocument doc) {
        List<String> sections = new ArrayList<>();
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText().trim();
            if (text.matches("^\\d+\\.\\d+[^\\s].*") && text.length() <= 50) {
                sections.add(text);
            }
        }
        return sections;
    }

    public static List<String> extractFullContentExcludingReferences(XWPFDocument doc) {
        List<String> fullContent = new ArrayList<>();
        boolean insideChapterOrSection = false;
        boolean insideReferences = false;

        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText().trim();
            if (text.startsWith("参考文献")) {
                insideReferences = true;
            }
            if (insideReferences) {
                continue;
            }
            if (text.matches("^\\d+[^.\\s].*") || text.matches("^\\d+\\.\\d+[^\\s].*")) {
                insideChapterOrSection = true;
                continue;
            }
            if (insideChapterOrSection) {
                fullContent.add(text);
            }
        }
        return fullContent;
    }

    public static List<Map<String, String>> extractTables(XWPFDocument doc) {
        List<Map<String, String>> tables = new ArrayList<>();
        for (int i = 0; i < doc.getParagraphs().size(); i++) {
            XWPFParagraph para = doc.getParagraphs().get(i);
            String text = para.getText().trim();
            if (text.matches("^表\\s?\\d+")) {
                Map<String, String> tableContent = new HashMap<>();
                tableContent.put("编号和标题", text);
                tableContent.put("内容", "");
                tableContent.put("表注", "");
                for (int j = i + 1; j < doc.getParagraphs().size(); j++) {
                    String nextText = doc.getParagraphs().get(j).getText().trim();
                    if (nextText.startsWith("表注:")) {
                        tableContent.put("表注", nextText);
                        break;
                    } else if (nextText.matches("^图\\s?\\d+") || nextText.matches("^\\d+[^.\\s].*") || nextText.matches("^\\d+\\.\\d+[^\\s].*")) {
                        break;
                    } else {
                        tableContent.put("内容", tableContent.get("内容") + "\n" + nextText);
                    }
                }
                tables.add(tableContent);
            }
        }
        return tables;
    }

    public static List<String> extractReferences(XWPFDocument doc) {
        List<String> references = new ArrayList<>();
        boolean insideReferences = false;
        String regex = "\\[\\d+\\]";
        Pattern pattern = Pattern.compile(regex);
        for (XWPFParagraph para : doc.getParagraphs()) {
            String text = para.getText().trim();
            if (text.startsWith("参考文献")) {
                insideReferences = true;
            }
            if (insideReferences) {
                Matcher matcher = pattern.matcher(text);
                while (matcher.find()) {
                    references.add(text);
                }
            }
        }
        return references;
    }

    public static Map<String, Object> formatExtractPaperInfo(String filePath) throws IOException {
        if (filePath.endsWith(".doc")) {
            String docxPath = filePath.replace(".doc", ".docx");
            convertDocToDocx(filePath, docxPath);
            filePath = docxPath;
        }
        try (XWPFDocument doc = new XWPFDocument(Files.newInputStream(Paths.get(filePath)))) {
            return formatExtractPaperInfoFromDocx(doc);
        }
    }

    public static void convertDocToDocx(String docPath, String docxPath) {
        try {

            Document document = new Document();
            document.loadFromFile(docPath);
            document.saveToFile(docxPath, FileFormat.Docx);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static Map<String, Object> formatExtractPaperInfoFromDocx(XWPFDocument doc) {
        List<String> chapters = extractChapterTitles(doc);
        List<String> sections = extractSectionTitles(doc);
        List<String> fullContent = extractFullContentExcludingReferences(doc);
        List<Map<String, String>> tables = extractTables(doc);
        List<String> references = extractReferences(doc);

        Map<String, Object> content = new HashMap<>();

        for (ContentFields field : ContentFields.values()) {
            content.put(field.getFieldName(), field.getDefaultValue());
        }

        content.put(ContentFields.CHAPTER_TITLES.getFieldName(), chapters);
        content.put(ContentFields.SECTION_TITLES.getFieldName(), sections);
        content.put(ContentFields.FULL_CONTENT.getFieldName(), fullContent);
        content.put(ContentFields.TABLES.getFieldName(), tables);
        content.put(ContentFields.REFERENCES.getFieldName(), references);

        if (doc.getParagraphs().size() > 3) {
            content.put(ContentFields.CHINESE_TITLE.getFieldName(), doc.getParagraphs().get(0).getText().trim());
            content.put(ContentFields.AUTHOR_NAME.getFieldName(), doc.getParagraphs().get(1).getText().trim());
            content.put(ContentFields.WORK_UNIT.getFieldName(), doc.getParagraphs().get(2).getText().trim());

            for (int i = 3; i < doc.getParagraphs().size(); i++) {
                String text = doc.getParagraphs().get(i).getText().trim();
                if (text.startsWith("摘要")) {
                    Map<String, String> chineseAbstract = (Map<String, String>) content.get(ContentFields.CHINESE_ABSTRACT.getFieldName());
                    chineseAbstract.put("内容", text.substring(3).trim());
                    content.put(ContentFields.CHINESE_ABSTRACT.getFieldName(), chineseAbstract);
                } else if (text.startsWith("关键词")) {
                    Map<String, String> chineseKeywords = (Map<String, String>) content.get(ContentFields.CHINESE_KEYWORDS.getFieldName());
                    chineseKeywords.put("内容", text.substring(4).trim());
                    content.put(ContentFields.CHINESE_KEYWORDS.getFieldName(), chineseKeywords);
                    break;
                }
            }

            boolean foundEnglishTitle = false;
            for (int i = 4; i < doc.getParagraphs().size(); i++) {
                String text = doc.getParagraphs().get(i).getText().trim();
                if (!foundEnglishTitle && text.matches("^[A-Z][A-Za-z\\s]+$")) {
                    content.put(ContentFields.ENGLISH_TITLE.getFieldName(), text);
                    foundEnglishTitle = true;
                    continue;
                }
                if (foundEnglishTitle && !content.containsKey(ContentFields.ENGLISH_AUTHOR_NAME.getFieldName())) {
                    content.put(ContentFields.ENGLISH_AUTHOR_NAME.getFieldName(), text);
                    continue;
                }
                if (content.containsKey(ContentFields.ENGLISH_AUTHOR_NAME.getFieldName()) && !content.containsKey(ContentFields.ENGLISH_WORK_UNIT.getFieldName())) {
                    content.put(ContentFields.ENGLISH_WORK_UNIT.getFieldName(), text);
                    continue;
                }
                if (text.startsWith("Abstract:")) {
                    Map<String, String> englishAbstract = (Map<String, String>) content.get(ContentFields.ENGLISH_ABSTRACT.getFieldName());
                    englishAbstract.put("内容", text.substring(9).trim());
                    content.put(ContentFields.ENGLISH_ABSTRACT.getFieldName(), englishAbstract);
                    continue;
                }
                if (text.startsWith("Key words:")) {
                    Map<String, String> englishKeywords = (Map<String, String>) content.get(ContentFields.ENGLISH_KEYWORDS.getFieldName());
                    englishKeywords.put("内容", text.substring(10).trim());
                    content.put(ContentFields.ENGLISH_KEYWORDS.getFieldName(), englishKeywords);
                    break;
                }
            }
        }

        for (Map.Entry<String, Object> entry : content.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        return content;
    }


    public static void main(String[] args) {
        // 测试代码
        try {
            String filePath = "/Users/houhao/Documents/论文要求/论文测试样例/28 检测认证 2024.01.0136-基于高效液相色谱技术的食品安全检测研究.doc";
            Map<String, Object> content = formatExtractPaperInfo(filePath);
            String formattedPath = applyFormatting(filePath, content, "");
            System.out.println("Formatted document saved to: " + formattedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
