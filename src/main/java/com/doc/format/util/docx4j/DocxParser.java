package com.doc.format.util.docx4j;

import com.doc.format.util.entity.DocumentElement;
import org.apache.poi.ss.usermodel.Drawing;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.P;
import org.docx4j.wml.R;

import java.util.ArrayList;
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
 * @date 2024/9/23 14:28
 */

public class DocxParser {

    public static void main(String[] args) throws Exception {
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File("/Users/houhao/Documents/论文要求/论文测试样例/32 检测认证 2024.03.0070-燃料电池汽车低温冷启动测试方法对比分析.docx"));

        List<DocumentElement> jsonResults = new ArrayList<>();
        List<Object> elements = wordMLPackage.getMainDocumentPart().getContent();
        int currentIndex = 0;

        for (Object obj : elements) {
            if (obj instanceof P) {
                P paragraph = (P) obj;
                processParagraph(paragraph);
            } else {
                System.out.println("未处理的元素类型: " + obj.getClass().getName());
            }
        }
    }

    // 处理段落内容
    private static void processParagraph(P paragraph) {
        System.out.println("处理段落: " + TextUtils.getText(paragraph));

        List<Object> paragraphContent = paragraph.getContent();
        for (Object contentObj : paragraphContent) {
            if (contentObj instanceof R) {
                R run = (R) contentObj;
                processRun(run);
            } else {
                System.out.println("未处理的段落内容类型: " + contentObj.getClass().getName());
            }
        }
    }

    // 处理 Run 中的内容
    private static void processRun(R run) {
        List<Object> runContent = run.getContent();
        for (Object runObj : runContent) {
            if (runObj instanceof Drawing) {
                System.out.println("检测到 Drawing 元素（图片）。");
                // 这里可以添加处理图片的逻辑
            } else if (runObj instanceof org.docx4j.wml.Pict) {
                System.out.println("检测到 VML（Pict）元素（图片）。");
                // 这里可以添加处理 VML 图片的逻辑
            } else {
                System.out.println("未处理的 Run 内容类型: " + runObj.getClass().getName());
            }
        }
    }
}
