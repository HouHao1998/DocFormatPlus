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
 * @date 2024/9/21 20:02
 */

import com.doc.format.util.entity.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonToHtmlConverter {

    // 将文档元素的列表转换为 HTML
    public static String convertToHtml(List<DocumentElement> elements) {
        StringBuilder htmlBuilder = new StringBuilder();
        int currentIndex = 0;
        // 添加 HTML 文档头部，包含 UTF-8 编码声明
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n<head>\n");
        htmlBuilder.append("<meta charset=\"UTF-8\">\n");
        htmlBuilder.append("<title>Document</title>\n");
        htmlBuilder.append("</head>\n<body>\n");

        // 遍历所有文档元素，生成对应的 HTML
        for (DocumentElement element : elements) {
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraphElement = (ParagraphElement) element;
                htmlBuilder.append(convertParagraphElementToHtml(paragraphElement));
            } else if (element instanceof ImageElement) {
                ImageElement imageElement = (ImageElement) element;
                htmlBuilder.append(convertImageElementToHtml(imageElement));
            } else if (element instanceof TableElement) {
                TableElement tableElement = (TableElement) element;
                htmlBuilder.append(convertTableElementToHtml(tableElement));
            } else if (element instanceof HeaderFooterElement) {
                HeaderFooterElement headerFooterElement = (HeaderFooterElement) element;
                htmlBuilder.append(convertHeaderFooterElementToHtml(headerFooterElement));
            } else if (element instanceof HyperlinkElement) {
                HyperlinkElement hyperlinkElement = (HyperlinkElement) element;
                htmlBuilder.append(convertHyperlinkElementToHtml(hyperlinkElement));
            } else if (element instanceof FormFieldElement) {
                FormFieldElement formFieldElement = (FormFieldElement) element;
                htmlBuilder.append(convertFormFieldElementToHtml(formFieldElement));
            }
        }

        // 添加 HTML 文档的尾部
        htmlBuilder.append("\n</body>\n</html>");
        return htmlBuilder.toString();
    }

    // 转换文本元素为 HTML
    private static String convertParagraphElementToHtml(ParagraphElement paragraphElement) {
        StringBuilder sb = new StringBuilder();
        sb.append("<p style=\"");

        // 设置对齐方式
        if (paragraphElement.getAlign() != null) {
            sb.append("text-align:").append(paragraphElement.getAlign()).append(";");
        }

        // 设置首行缩进
        if (paragraphElement.getFirstLineIndent() != null && !paragraphElement.getFirstLineIndent().equals("none")) {
            sb.append("text-indent:").append(paragraphElement.getFirstLineIndent()).append("px;");
        }

        sb.append("\">");

        // 添加文本内容
        for (TextElement textElement : paragraphElement.getTextElements()) {
            if (textElement.getHyperlink() != null) {
                // 如果有超链接，外层包裹 <a> 标签
                sb.append("<a href=").append(textElement.getHyperlink()).append(">");
            }
            sb.append("<span iclass=\"idx_").append(textElement.getIdx()).append("\" style=\"");

            // 设置字体名称
            if (textElement.getFont() != null) {
                sb.append("font-family:").append(textElement.getFont()).append(";");
            }

            // 设置字号
            if (textElement.getFontSize() != null) {
                sb.append("font-size:").append(textElement.getFontSize()).append("pt;");
            }

            // 设置颜色
            if (textElement.getColor() != null) {
                sb.append("color:").append(textElement.getColor()).append(";");
            }

            // 设置加粗
            if (textElement.isBold()) {
                sb.append("font-weight:bold;");
            }

            // 设置斜体
            if (textElement.isItalic()) {
                sb.append("font-style:italic;");
            }

            // 设置下划线
            if (textElement.isUnderline()) {
                sb.append("text-decoration:underline;");
            }

            sb.append("\">");

            sb.append(textElement.getContent());


            sb.append("</span>");
            if (textElement.getHyperlink() != null) {
                // 关闭超链接 <a> 标签
                sb.append("</a>");
            }
        }

        sb.append("</p>");
        return sb.toString();
    }


    // 转换图片元素为 HTML
    private static String convertImageElementToHtml(ImageElement imageElement) {
        StringBuilder sb = new StringBuilder();
        if (imageElement.getLocalPath() != null) {
            try {
                // 读取本地图片文件
                File imageFile = new File(imageElement.getLocalPath());
                byte[] fileContent = Files.readAllBytes(imageFile.toPath());

                // 将图片内容编码为 Base64
                String encodedImage = Base64.getEncoder().encodeToString(fileContent);

                // 将图片放在一个居中的 div 中
                sb.append("<div style=\"text-align: center;\">");
                sb.append("<img src=\"data:image/png;base64,");
                sb.append(encodedImage);

                // 添加宽度和高度属性
                // if (imageElement.getWidth() != null) {
                //     sb.append("\" width=\"").append(imageElement.getWidth()).append("\"");
                // }
                // if (imageElement.getHeight() != null) {
                //     sb.append("\" height=\"").append(imageElement.getHeight()).append("\"");
                // }

                sb.append("\" alt=\"Image\" />");
                sb.append("</div>"); // 关闭 div

            } catch (IOException e) {
                e.printStackTrace();
                sb.append("<p>图片加载失败</p>");
            }
        }

        return sb.toString();
    }


    // 转换表格元素为 HTML
    private static String convertTableElementToHtml(TableElement tableElement) {
        StringBuilder sb = new StringBuilder();
        sb.append("<table border=\"1\" style=\"border-collapse:collapse;\">");

        // 遍历表格行
        for (List<String> row : tableElement.getTableContent()) {
            sb.append("<tr>");
            for (String cell : row) {
                sb.append("<td>");
                sb.append(cell); // 添加单元格内容
                sb.append("</td>");
            }
            sb.append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    // 转换页眉和页脚元素为 HTML
    private static String convertHeaderFooterElementToHtml(HeaderFooterElement headerFooterElement) {
        StringBuilder sb = new StringBuilder();
        if (headerFooterElement.getType() == ElementType.HEADER) {
            sb.append("<header>");
            sb.append(headerFooterElement.getContent());
            sb.append("</header>");
        } else if (headerFooterElement.getType() == ElementType.FOOTER) {
            sb.append("<footer>");
            sb.append(headerFooterElement.getContent());
            sb.append("</footer>");
        }
        return sb.toString();
    }

    // 转换超链接元素为 HTML
    private static String convertHyperlinkElementToHtml(HyperlinkElement hyperlinkElement) {
        StringBuilder sb = new StringBuilder();
        sb.append("<a href=\"");
        sb.append(hyperlinkElement.getUrl());  // 设置超链接地址
        sb.append("\">");
        sb.append(hyperlinkElement.getContent()); // 设置显示的文本
        sb.append("</a>");
        return sb.toString();
    }

    // 转换表单域为 HTML
    private static String convertFormFieldElementToHtml(FormFieldElement formFieldElement) {
        StringBuilder sb = new StringBuilder();
        sb.append("<input type=\"text\" value=\"");
        sb.append(formFieldElement.getContent()); // 表单域的默认值
        sb.append("\" />");
        return sb.toString();
    }

    // 解析 HTML 并根据 idx 填充 JSON 的 startIndex 和 endIndex
    public static void mapHtmlToJson(String htmlContent, List<DocumentElement> jsonElements) {
        // 用来记录 HTML 文件中的当前索引
        int globalIndex = 0; // 全局索引，从文件的第一个字符开始

        // 使用正则表达式匹配带有 idx 的 <span> 标签
        Pattern pattern = Pattern.compile("<span\\s+class=\"idx_(\\d+)\".*?>(.*?)</span>");

        Matcher matcher = pattern.matcher(htmlContent);

        // 每次找到匹配项时，都需要将整个匹配的 span 标签及其内容计算在内
        while (matcher.find()) {
            String idxStr = matcher.group(1);  // 获取 idx 值
            int idx = Integer.parseInt(idxStr);  // 将 idx 转换为整数
            String spanText = matcher.group(2);  // 获取 span 标签内的文本内容

            // 获取 span 开始的全局位置（匹配之前的文本长度 + 标签内文本长度）
            int startIndex = globalIndex + matcher.start(2);
            int endIndex = startIndex + spanText.length() - 1;

            // 根据 idx 查找 JSON 中对应的 textElement 并回填 startIndex 和 endIndex
            fillJsonWithIndexes(jsonElements, idx, startIndex, endIndex);

            // 更新 globalIndex 到下一个字符
            globalIndex = matcher.end(); // 更新到整个span标签之后的索引
        }
    }

    // 根据 idx 查找 JSON 中的 textElement 并填充 startIndex 和 endIndex
    private static void fillJsonWithIndexes(List<DocumentElement> jsonElements, int idx, int startIndex, int endIndex) {
        for (DocumentElement element : jsonElements) {
            if (element instanceof ParagraphElement) {
                ParagraphElement paragraphElement = (ParagraphElement) element;
                for (TextElement textElement : paragraphElement.getTextElements()) {
                    if (textElement.getIdx() == idx) {
                        textElement.setStartIndex(startIndex);
                        textElement.setEndIndex(endIndex);
                        return;  // 找到匹配的 idx 后就可以返回了
                    }
                }
            }
        }
    }


}
