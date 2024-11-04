package com.doc.format.util.spire;

import com.doc.format.util.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spire.doc.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.documents.UnderlineStyle;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.Field;
import com.spire.doc.fields.TextRange;
import org.jsoup.Jsoup;
import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ParagraphElement;
import com.doc.format.util.iJianCha.CheckResponse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;

import static com.doc.format.util.spire.ContentVerificationToHtml.extractProofData;
import static com.doc.format.util.spire.JsonToWord.removeTite;


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
public class WordRevisionTest {

    public static String getDocumentElements(String docxFilePath, String htmlFilePath) {
        String output = docxFilePath.replace(".docx", "_文字检查完成.docx");
        Map<String, String> map = extractProofData(htmlFilePath);
        // 加载测试文档
        Document document = new Document(docxFilePath);

        //判断文档是否有修改
        if (document.hasChanges()) {
            //接受修订
            document.acceptChanges();
        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String text = entry.getValue();

            // 按 "-" 拆分键以获取 i 和 j
            String[] indices = key.split("-");
            // 假设 "sid" 后是 i 的数值
            int i = Integer.parseInt(indices[0].replace("sid", ""));
            // 假设 "pid" 后是 j 的数值
            int j = Integer.parseInt(indices[1].replace("pid", ""));
            // 获取指定的 DocumentObject
            if (i < document.getSections().getCount() && j < document.getSections().get(i).getBody().getChildObjects().getCount()) {
                DocumentObject documentObject = document.getSections().get(i).getBody().getChildObjects().get(j);

                if (documentObject instanceof Paragraph) {
                    Paragraph paragraph = (Paragraph) documentObject;
                    paragraph.setText(text);
                }
            }


            document.saveToFile(output, FileFormat.Docx_2013);
            try {
                removeTite(output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return output;
    }


}