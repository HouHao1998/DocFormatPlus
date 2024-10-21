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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
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
public class WordToHtml {
    public static void main(String[] args) {
        String path = "/Users/houhao/Documents/论文要求/论文测试样例/30 检测认证 2024.03.0114-棉、聚酰胺纤维和氨纶混纺面料定量分析研究.doc";
        wordToHtml(path);
    }


    public static String wordToHtml(String path) {
        try {
            // 加载测试文档
            Document document = new Document(path);

            // 判断文档是否有修改
            if (document.hasChanges()) {
                // 接受修订
                document.acceptChanges();
            }

            // 获取输入文件的目录
            String parentDir = Paths.get(path).getParent().toString();

            // 构建输出文件路径（同一目录下的 result.html）
            String outputFilePath = Paths.get(parentDir, "result.html").toString();

            // 将文档保存为HTML格式
            document.saveToFile(outputFilePath, FileFormat.Html);
            System.out.println("转换成功，保存路径：" + outputFilePath);

            // 读取生成的HTML文件
            String htmlContent = new String(Files.readAllBytes(Paths.get(outputFilePath)));

            // 调用方法去除评估警告内容
            String cleanedHtml = removeEvaluationWarning(htmlContent);

            // 将处理后的内容保存回HTML文件
            Files.write(Paths.get(outputFilePath), cleanedHtml.getBytes());
            System.out.println("处理后的HTML文件已保存：" + outputFilePath);

            return outputFilePath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String removeEvaluationWarning(String htmlContent) {
        // 定义正则表达式，匹配包含指定文字的整个 <p> 标签或 <ins> 标签块
        String regex = "(<p[^>]*?>.*?Evaluation Warning: The document was created with Spire\\.Doc for JAVA.*?</p>)|(<ins[^>]*?>.*?Evaluation Warning: The document was created with Spire\\.Doc for JAVA.*?</ins>)";

        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(htmlContent);

        // 替换匹配到的内容为空字符串
        return matcher.replaceAll("");
    }

}