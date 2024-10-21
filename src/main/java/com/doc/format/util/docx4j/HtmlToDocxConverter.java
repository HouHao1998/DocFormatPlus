package com.doc.format.util.docx4j;

import jakarta.xml.bind.JAXBElement;
import org.docx4j.TraversalUtil;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.fonts.*;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

import java.io.File;
import java.net.URI;
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
 * @date 2024/8/29 09:43
 */
import org.docx4j.TraversalUtil.CallbackImpl;

public class HtmlToDocxConverter {

    public static void convertHtmlToDocx(String htmlContent, String outputPath) throws Exception {
        // 创建 WordprocessingMLPackage 实例
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

        // 使用 HtmlConversion 将 HTML 内容转换为 DOCX


        // 保存生成的 DOCX 文件
        wordMLPackage.save(new File(outputPath));
    }

    public static void main(String[] args) {
        String htmlContent = "<h1>标题</h1><p>这是一个段落。</p>";
        String outputPath = "output.docx";

        try {
            convertHtmlToDocx(htmlContent, outputPath);
            System.out.println("转换成功，文件已保存到 " + outputPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}