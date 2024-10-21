package com.doc.format.util.docx4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;

import org.docx4j.Docx4J;
import org.docx4j.convert.out.ConversionFeatures;
import org.docx4j.convert.out.HTMLSettings;
import org.docx4j.convert.out.html.SdtToListSdtTagHandler;
import org.docx4j.convert.out.html.SdtWriter;
import org.docx4j.fonts.*;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.FontTablePart;

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
public class DocxToHtmlConverter {

    public static void convertDocxToHtml(String inputFilePath, String outputFilePath, boolean nestLists) {
        System.setProperty("javax.xml.bind.context.factory", "org.eclipse.persistence.jaxb.JAXBContextFactory");

        try {
            // 加载 DOCX 文件
            WordprocessingMLPackage wordMLPackage = Docx4J.load(new File(inputFilePath));

            // 设置 HTML 导出选项
            HTMLSettings htmlSettings = Docx4J.createHTMLSettings();
            htmlSettings.setImageDirPath(outputFilePath + "_files");
            htmlSettings.setImageTargetUri(outputFilePath.substring(outputFilePath.lastIndexOf("/") + 1) + "_files");
            htmlSettings.setOpcPackage(wordMLPackage);

            // 获取并打印文档中的字体名称以确认
            FontTablePart fontTablePart = wordMLPackage.getMainDocumentPart().getFontTablePart();


            // 使用 BestMatchingMapper 确保字体能够匹配
            Mapper fontMapper = new BestMatchingMapper();
            wordMLPackage.setFontMapper(fontMapper);

            // 确保字体路径和名称匹配
            PhysicalFonts.addPhysicalFonts("宋体", new URI("file:///Users/houhao/Downloads/font/FZSSK.TTF"));
            // 对应"黑体"字体名称
            PhysicalFonts.addPhysicalFonts("黑体", new URI("file:///Users/houhao/Downloads/font/Fzhtk.ttf"));
            // 对应"仿宋"字体名称
            PhysicalFonts.addPhysicalFonts("仿宋", new URI("file:///Users/houhao/Downloads/font/FZFSK.TTF"));
            // 对应"楷体"字体名称
            PhysicalFonts.addPhysicalFonts("楷体", new URI("file:///Users/houhao/Downloads/font/FZKTK.TTF"));

            // 确保在字体映射中使用实际的字体名称
            fontMapper.put("宋体", PhysicalFonts.get("宋体"));
            fontMapper.put("黑体", PhysicalFonts.get("黑体"));
            fontMapper.put("仿宋", PhysicalFonts.get("仿宋"));
            fontMapper.put("楷体", PhysicalFonts.get("楷体"));

            // 根据是否嵌套列表选择合适的 CSS
            String userCSS;
            if (nestLists) {
                userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img,  table, caption, tbody, tfoot, thead, tr, th, td { margin: 0; padding: 0; border: 0; } body {line-height: 1;}";
                SdtWriter.registerTagHandler("HTML_ELEMENT", new SdtToListSdtTagHandler());
            } else {
                userCSS = "html, body, div, span, h1, h2, h3, h4, h5, h6, p, a, img, ol, ul, li, table, caption, tbody, tfoot, thead, tr, th, td { margin: 0; padding: 0; border: 0; } body {line-height: 1;}";
                htmlSettings.getFeatures().remove(ConversionFeatures.PP_HTML_COLLECT_LISTS);
            }
            htmlSettings.setUserCSS(userCSS);

            // 输出到指定的 HTML 文件
            OutputStream os = new FileOutputStream(new File(outputFilePath));
            Docx4J.toHTML(htmlSettings, os, Docx4J.FLAG_NONE);
            os.close();

            System.out.println("HTML 文件生成成功，文件路径: " + outputFilePath);

            // 清理嵌入字体的临时文件
            if (wordMLPackage.getMainDocumentPart().getFontTablePart() != null) {
                wordMLPackage.getMainDocumentPart().getFontTablePart().deleteEmbeddedFontTempFiles();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("转换失败！");
        }
    }

    public static void main(String[] args) {
        String inputFilePath = "/Users/houhao/Documents/论文要求/论文测试样例/32 检测认证 2024.03.0070-燃料电池汽车低温冷启动测试方法对比分析.docx";
        String outputFilePath = "converted.html";
        boolean nestLists = true; // 是否嵌套列表

        convertDocxToHtml(inputFilePath, outputFilePath, nestLists);
    }
}