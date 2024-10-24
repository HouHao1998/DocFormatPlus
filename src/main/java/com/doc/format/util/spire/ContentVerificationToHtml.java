package com.doc.format.util.spire;


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
public class ContentVerificationToHtml {

    public static void main(String[] args) throws IOException {
        addIdx("/Users/houhao/Downloads/word/6ebea590-6624-4e89-aab3-a416b635e880/result.html","/Users/houhao/Downloads/word/6ebea590-6624-4e89-aab3-a416b635e880/contentVerification.json");
    }

    public static void addIdx(String filePath, String jsonFilePath) throws IOException {
        // 读取 HTML 文件内容
        String htmlContent = new String(Files.readAllBytes(Paths.get(filePath)), "UTF-8");
        String replace = htmlContent.replace("&#xa0;", "[NBSP]");

        // 解析 HTML，并将所有 &nbsp; 替换为 [NBSP] 占位符
        Document doc = Jsoup.parse(replace);

        // 读取 JSON 数据
        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)), "UTF-8");
        JSONObject jsonObject = new JSONObject(jsonContent);

        // 获取段落索引和字符索引信息
        int paragraphIndex = jsonObject.getInt("PIndex");
        JSONArray infos = jsonObject.getJSONArray("infos");

        // 查找父标签是 div 的 p 标签
        Elements pElements = doc.select("div > p");
        if (paragraphIndex < pElements.size()) {
            Element pElement = pElements.get(paragraphIndex);

            // 遍历 span 标签（如果有）
            Elements spanElements = pElement.select("span");
            for (Element spanElement : spanElements) {
                // 获取 span 标签的文本内容，并处理字符索引
                String spanText = spanElement.text();

                // 在合适的字符索引处插入带有样式的元素
                for (int i = 0; i < infos.size(); i++) {
                    JSONObject info = infos.getJSONObject(i);
                    int startIdx = info.getInt("l");
                    int endIdx = info.getInt("r");

                    // 根据索引插入新的 span 标签
                    String modifiedText = spanText.substring(0, startIdx)
                            + "<span data-proof-id=\"" + info.getInt("pl") + "\" class=\"custom-underline-red idx "
                            + info.getInt("pr") + "\">" + spanText.substring(startIdx, endIdx)
                            + "</span>" + spanText.substring(endIdx);

                    spanElement.text(modifiedText);
                }
            }
        }

        // 保存修改后的 HTML 文件
        Files.write(Paths.get(filePath), doc.outerHtml().getBytes(StandardCharsets.UTF_8));
    }



}
