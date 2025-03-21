package com.doc.format.util.spire;

import com.alibaba.fastjson2.JSON;
import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ParagraphElement;
import com.doc.format.util.iJianCha.CheckResponse;
import org.docx4j.wml.P;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


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
public class ContentVerificationToHtml {
    /**
     * 解析 HTML 内容，提取所有 data-proof-sid 和 data-proof-pid 属性值，并将其作为键，对应的值为 <p> 标签下所有的 <span> 标签的文本内容，组成 Map 返回。
     *
     * @param html
     * @return
     */
    public static Map<String, String> extractProofData(String html) {
        Map<String, String> result = new HashMap<>();
        try {
            String htmlContent = new String(Files.readAllBytes(Paths.get(html)), "UTF-8");

            // 使用 Jsoup 解析 HTML
            Document document = Jsoup.parse(htmlContent);

            // 查找所有在 div 下的 <1p> 标签
            Elements pElements = document.select("div > p");

            for (Element paragraph : pElements) {
                // 获取 data-proof-sid 和 data-proof-pid 属性值
                String dataProofSid = paragraph.attr("data-proof-sid");
                String dataProofPid = paragraph.attr("data-proof-pid");

                // 拼接键值
                String key = dataProofSid + "-" + dataProofPid;

                // 初始化一个 StringBuilder 来拼接文本内容
                StringBuilder textBuilder = new StringBuilder();

                // 遍历 <p> 的所有子节点，保持顺序处理
                for (Node childNode : paragraph.childNodes()) {
                    if (childNode instanceof TextNode) {
                        // 如果是文本节点，直接获取文本内容
                        textBuilder.append(((TextNode) childNode).text());
                    } else if (childNode instanceof Element) {
                        // 如果是子标签，判断是否为 <span> 标签并获取其文本内容
                        Element element = (Element) childNode;
                        if (element.tagName().equals("span")) {
                            textBuilder.append(element.text());
                        }
                    }
                }
                // 将拼接结果存入 map
                result.put(key, textBuilder.toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    public static void addIdx(String htmlFilePath, String jsonFilePath, List<DocumentElement> documentElements) throws Exception {
        // 读取 HTML 文件
        String htmlContent = new String(Files.readAllBytes(Paths.get(htmlFilePath)), "UTF-8");
        String replace = htmlContent.replace("&#xa0;", "[NBSP]");

        // 解析 HTML，并将所有 &nbsp; 替换为 [NBSP] 占位符
        Document doc = Jsoup.parse(replace);

        // 读取 JSON 数据，并解析为 Mistake 列表
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8");
        List<CheckResponse.Result.Mistake> mistakes = JSON.parseArray(jsonContent, CheckResponse.Result.Mistake.class);

        // 按照 pIndex 分组 mistakes
        Map<Integer, List<CheckResponse.Result.Mistake>> mistakesGroupedByPIndex = mistakes.stream()
                .collect(Collectors.groupingBy(CheckResponse.Result.Mistake::getPIndex));
        List<ParagraphElement> paragraphElements = new ArrayList<>();
        for (DocumentElement documentElement : documentElements) {
            if (documentElement instanceof ParagraphElement) {
                paragraphElements.add((ParagraphElement) documentElement);
            }
        }
        Elements pElementAll = doc.select("div > p");
        for (int i = 0, pElementsSize = pElementAll.size(); i < pElementsSize && i < paragraphElements.size(); i++) {
            Element p = pElementAll.get(i);
            p.attr("data-proof-pid", String.valueOf(paragraphElements.get(i).getParagraphIndex()));
            p.attr("data-proof-sid", String.valueOf(paragraphElements.get(i).getSectionIndex()));
        }
        // 遍历每个 pIndex 相关的 mistakes 列表
        mistakesGroupedByPIndex.forEach((pIndex, mistakesList) -> {
            Elements pElements = doc.select("div > p");
            if (pIndex < pElements.size()) {
                Element pElement = pElements.get(pIndex);
                String paragraphText = pElement.text();

                // 用 StringBuilder 保存新段落内容
                StringBuilder updatedParagraphHtml = new StringBuilder();

                // 当前索引位置
                int currentPos = 0;

                // 遍历当前 pIndex 下的所有 mistakes
                for (int i = 0, mistakesListSize = mistakesList.size(); i < mistakesListSize; i++) {
                    paragraphText = paragraphText.replace("[NBSP]", " ");
                    CheckResponse.Result.Mistake mistake = mistakesList.get(i);
                    int pl = mistake.getPl();  // 段落中错误位置的左索引
                    int pr = mistake.getPr();  // 段落中错误位置的右索引
                    // 将错误前的文本添加为没有 class 的 span
                    if (currentPos < pl) {
                        String normalText = paragraphText.substring(currentPos, pl);
                        Element normalSpan = new Element(Tag.valueOf("span"), "");
                        normalSpan.text(normalText);
                        updatedParagraphHtml.append(normalSpan.outerHtml());
                    }

                    // 为错误文本添加 custom-underline-red class 的 span
                    String errorText = paragraphText.substring(pl, pr);
                    if (!(mistake.getErrorText().equals(errorText))) {
                        System.out.println("对不上 *****" + mistake.getErrorText() + " =====" + errorText);
                    }
                    Element errorSpan = new Element(Tag.valueOf("span"), "");
                    errorSpan.attr("class", "custom-underline-red idx " + mistake.getIdx());
                    errorSpan.attr("data-proof-id", String.valueOf(mistake.getIdx()));
                    errorSpan.text(errorText);
                    updatedParagraphHtml.append(errorSpan.outerHtml());

                    // 更新当前处理位置
                    currentPos = pr;
                }
                // 将剩余文本添加为没有 class 的 span
                if (currentPos < paragraphText.length()) {
                    String remainingText = paragraphText.substring(currentPos);
                    Element remainingSpan = new Element(Tag.valueOf("span"), "");
                    remainingSpan.text(remainingText);
                    updatedParagraphHtml.append(remainingSpan.outerHtml());
                }

                // 更新 p 标签中的 HTML 内容
                pElement.html(updatedParagraphHtml.toString());
            }
        });

        // 获取原文件路径并生成新文件名（增加后缀 _文件校验后.html）
        Path originalPath = Paths.get(htmlFilePath);
        String newFileName = originalPath.getFileName().toString().replace(".html", "_文件校验后.html");
        Path newFilePath = originalPath.resolveSibling(newFileName);

        // 输出之前将 [NBSP] 占位符替换回 &nbsp;
        String outputHtml = doc.html().replace("[NBSP]", "&#xa0;");
        // 将修改后的 HTML 内容写入新文件
        Files.write(newFilePath, outputHtml.getBytes("UTF-8"), StandardOpenOption.CREATE);
    }

}
