package com.doc.format.util.docx4j;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/20 09:43
 */

import com.doc.format.factory.DocumentElementFactory;
import com.doc.format.factory.WordDocumentElementFactory;
import com.doc.format.parser.DocumentElementParser;
import com.doc.format.util.entity.*;


import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.docx4j.XmlUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.wml.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;


public class WordDocumentParser {

    static Set<Drawing> processedDrawings = new HashSet<>();

    public static void main(String[] args) throws Exception {

        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File("result.docx"));
        List<DocumentElement> jsonResults = new ArrayList<>();

        // 使用工厂模式
        DocumentElementFactory factory = new WordDocumentElementFactory();
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();


        // 获取文档内容
        List<Object> elements = documentPart.getContent();

        // 遍历并处理文档内容
        for (int i = 0; i < elements.size(); i++) {
            Object element = elements.get(i);
            processContentElement(element, factory, jsonResults, wordMLPackage);
        }

        // 将结果转为 JSON 并输出
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonResults);
        System.out.println(jsonString);

        // 转换为 HTML
        String htmlContent = JsonToHtmlConverter.convertToHtml(jsonResults);

        // 将 HTML 内容写入文件
        writeHtmlToFile(htmlContent, "output.html");
    }

    private static void writeHtmlToFile(String htmlContent, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
            System.out.println("HTML 文件已成功写入到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 递归处理文档中的每个元素，包括段落、表格、图片等
    private static void processContentElement(Object element, DocumentElementFactory factory,
                                              List<DocumentElement> jsonResults, WordprocessingMLPackage wordMLPackage) throws Exception {
        DocumentElementParser parser = null;

        // 如果元素是 JAXBElement，需要解包
        if (element instanceof jakarta.xml.bind.JAXBElement) {
            element = ((jakarta.xml.bind.JAXBElement<?>) element).getValue();
            processContentElement(element, factory, jsonResults, wordMLPackage);
        }

        // 检测是否是段落（P），并递归处理段落中的内容
        if (element instanceof P) {
            P paragraph = (P) element;
            // 递归处理段落中的内容
            for (Object content : paragraph.getContent()) {
                processContentElement(content, factory, jsonResults, wordMLPackage);
            }

            // 解析段落本身
            parser = factory.createParagraphParser();
        }
        // 检测是否是 Run（R）
        else if (element instanceof R) {
            R run = (R) element;
            // 递归处理 Run 中的内容
            for (Object runContent : run.getContent()) {
                processContentElement(runContent, factory, jsonResults, wordMLPackage);
            }

        }
        // 检测是否是 RunIns（插入内容的 Run）
        else if (element instanceof RunIns) {
            RunIns runIns = (RunIns) element;
            // 递归处理 RunIns 中的内容
            for (Object runContent : runIns.getCustomXmlOrSmartTagOrSdt()) {
                processContentElement(runContent, factory, jsonResults, wordMLPackage);
            }

        }
        // 检测表格（Tbl）
        else if (element instanceof Tbl) {
            System.out.println("检测到表格元素 (Tbl)");
            parser = factory.createTableParser();
        }
        // 检测图片（Drawing）
        else if (element instanceof Drawing) {
            Drawing drawing = (Drawing) element;

            // 检查是否已经处理过
            if (processedDrawings.contains(drawing)) {
                System.out.println("此图片已处理，跳过: " + drawing);
                return; // 跳过处理
            }

            // 添加到已处理集合
            processedDrawings.add(drawing);

            System.out.println("检测到图片元素 (Drawing)");
            parser = factory.createImageParser();
        }


        // 如果有相应的解析器，进行解析并添加到结果中
        if (parser != null) {
            DocumentElement parsedElement = parser.parse(element, wordMLPackage);
            if (parsedElement != null) {
                jsonResults.add(parsedElement);
            }
        } else {
            // 未处理的元素类型记录
            System.out.println("未处理的元素类型: " + element.getClass().getName());
        }
    }

}

