package com.doc.format.util.docx4j;


import org.docx4j.openpackaging.packages.WordprocessingMLPackage;


/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/8/29 09:43
 */

import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import java.util.ArrayList;


public class DocxToJsonParser {

    public static void main(String[] args) throws Exception {
        // 加载Word文件
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File("/Users/houhao/Documents/论文要求/论文测试样例/48 医疗医务标准化 2024.04.0107-规范化管理在检验科医院感染的预防与控制中的运用探讨 石庆衡.docx"));

        // 获取文档的所有段落
        List<Object> paragraphs = wordMLPackage.getMainDocumentPart().getContent();

        // 用于存储每个段落的解析结果
        List<Map<String, Object>> jsonResults = new ArrayList<>();

        for (Object obj : paragraphs) {
            if (obj instanceof P) {
                P paragraph = (P) obj;

                // 用于存储单个段落的内容
                Map<String, Object> paragraphData = new HashMap<>();

                // 获取段落文本内容
                String text = TextUtils.getText(paragraph);  // 使用TextUtils提取文本
                paragraphData.put("content", text);

                // 获取段落的字体、字号和样式
                RPr rPr = getRunProperties(paragraph);
                if (rPr != null) {
                    paragraphData.put("font", getFontName(rPr));
                    paragraphData.put("fontSize", getFontSize(rPr));
                    paragraphData.put("bold", isBold(rPr));
                    paragraphData.put("italic", isItalic(rPr));
                }

                // 将该段落的内容添加到结果列表
                jsonResults.add(paragraphData);
            }
        }

        // 将整个列表转换为JSON字符串
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonResults);
        System.out.println(jsonString);
    }

    // 获取Run Properties（字体、样式等）
    private static RPr getRunProperties(P paragraph) {
        List<Object> runs = paragraph.getContent();
        for (Object runObj : runs) {
            if (runObj instanceof R) {
                return ((R) runObj).getRPr();
            }
        }
        return null;
    }

    // 获取字体名称
    private static String getFontName(RPr rPr) {
        if (rPr.getRFonts() != null) {
            return rPr.getRFonts().getAscii();
        }
        return "default";
    }

    // 获取字体大小
    private static String getFontSize(RPr rPr) {
        if (rPr.getSz() != null) {
            return rPr.getSz().getVal().toString();
        }
        return "default";
    }

    // 判断是否加粗
    private static boolean isBold(RPr rPr) {
        return rPr.getB() != null && rPr.getB().isVal();
    }

    // 判断是否斜体
    private static boolean isItalic(RPr rPr) {
        return rPr.getI() != null && rPr.getI().isVal();
    }
}
