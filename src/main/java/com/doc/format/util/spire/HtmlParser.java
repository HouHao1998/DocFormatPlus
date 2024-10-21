package com.doc.format.util.spire;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


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
public class HtmlParser {

    public static void main(String[] args) throws IOException {
        addIdx("/Users/houhao/Downloads/word/e2f23dbd-80fe-43ee-98c3-6c5a3b0e3119/result.html");
    }

    public static void addIdx(String filePath) throws IOException {
        String htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        String replace = htmlContent.replace("&#xa0;", "[NBSP]");

        // 解析 HTML，并将所有 &nbsp; 替换为 [NBSP] 占位符
        Document doc = Jsoup.parse(replace);


        // 查找父标签是 div 的 p 标签
        Elements pElements = doc.select("div > p");
        int size = pElements.size();
        System.out.println("父标签为 div 的 p 标签数量：" + size);

        // 字符索引从 0 开始
        int charIndex = 0;

        // 遍历每个 p 标签下的 span 标签
        for (Element pElement : pElements) {
            Elements spanElements = pElement.select("span");
            for (Element spanElement : spanElements) {

                // 检查 span 内容是否完全为占位符 [NBSP]
                if ("[NBSP]".equals(spanElement.text())) {
                    // 如果内容仅为 &nbsp; 占位符，跳过此 span 的 idx 处理
                    continue;
                }
                if (!spanElement.hasText() && spanElement.html().trim().isEmpty()) {
                    // 如果 span 没有文本内容或没有闭合，跳过
                    continue;
                }
                // 为非 [NBSP] 的 span 增加 data-proof-id 属性
                spanElement.attr("data-proof-id", String.valueOf(charIndex));

                // 增加或修改 class 属性
                String existingClass = spanElement.attr("class");
                String newClass = existingClass.isEmpty() ? "" : existingClass + " ";
                newClass += "custom-underline-red idx " + charIndex;
                spanElement.attr("class", newClass);

                // 递增索引
                charIndex++;
            }
        }

        // 设置输出时的选项，保持 HTML 实体不被转义
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .escapeMode(Entities.EscapeMode.xhtml) // 保留 &#xa0;
                .charset("UTF-8");

        // 输出之前将 [NBSP] 占位符替换回 &nbsp;
        String outputHtml = doc.html().replace("[NBSP]", "&#xa0;");
        doc = Jsoup.parse(outputHtml);
        doc.outputSettings(outputSettings);


        // 将修改后的 HTML 内容写回文件
        Files.write(Paths.get(filePath), outputHtml.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("HTML 文件处理完成并保存到原路径: " + filePath);
    }

    public static void updateImgSrc(String filePath, String httpUrl) throws IOException {
        String htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");

        // 将字符串转为 Document 对象
        Document doc = Jsoup.parse(htmlContent);

        // 查找所有 <img> 标签
        Elements imgElements = doc.select("img");

        // 遍历每个 <img> 标签并修改 src 属性
        for (Element imgElement : imgElements) {
            String src = imgElement.attr("src");

            // 如果 src 以 "result_images/" 开头，替换为完整的 URL 地址
            if (src.startsWith("result_images/")) {
                String newSrc = httpUrl + src;
                imgElement.attr("src", newSrc);
            }
        }

        // 设置输出时的选项，保持 HTML 实体不被转义
        Document.OutputSettings outputSettings = new Document.OutputSettings()
                .escapeMode(org.jsoup.nodes.Entities.EscapeMode.xhtml)
                .charset("UTF-8");

        // 生成修改后的 HTML 字符串
        doc.outputSettings(outputSettings);
        String outputHtml = doc.outerHtml();

        // 将修改后的 HTML 内容写回文件
        Files.write(Paths.get(filePath), outputHtml.getBytes("UTF-8"), StandardOpenOption.TRUNCATE_EXISTING);

    }

}
