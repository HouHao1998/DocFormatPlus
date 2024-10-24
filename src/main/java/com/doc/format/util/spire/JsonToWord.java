package com.doc.format.util.spire;

import com.doc.format.util.entity.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spire.doc.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.TextRange;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.lang.reflect.Type;
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
 * @date 2024/9/24 18:20
 */
public class JsonToWord {
    public static void main(String[] args) {
        String filePath = "/Users/houhao/Documents/论文要求/论文测试样例/30 检测认证 2024.03.0114-棉、聚酰胺纤维和氨纶混纺面料定量分析研究.doc";
        String jsonResult = "/Users/houhao/Downloads/word/9d150664-8786-40a3-a4c1-9719c44c9ce1/validationResults.json";
        try {
            jsonToWord(filePath, jsonResult, true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String jsonToWord(String filePath, String jsonResult, Boolean isAddPicture) throws IOException {
        // 加载测试文档
        Document document = new Document(filePath);

        //判断文档是否有修改
        if (document.hasChanges()) {

            //接受修订
            document.acceptChanges();
            //拒绝修订
            //doc.rejectChanges();
        }

        // 解析 JSON 为 List<ValidationResult>
        List<ValidationResult> validationResults = parseJsonToValidationResults(jsonResult);

        // 使用默认枚举修改文档
        modifyWordDocument(document, validationResults, isAddPicture);

        // 保存修改后的文档
        String outputFilePath = generateOutputFilePath(jsonResult);
        document.saveToFile(outputFilePath, FileFormat.Docx_2010);
        InputStream is = new FileInputStream(outputFilePath);
        XWPFDocument document2 = new XWPFDocument(is);
        //以上Spire.Doc 生成的文件会自带警告信息，这里来删除Spire.Doc 的警告
        document2.removeBodyElement(0);
        //输出word内容文件流，新输出路径位置
        OutputStream os = new FileOutputStream(outputFilePath);
        try {
            document2.write(os);
            System.out.println("生成docx文档成功！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回结果文件的地址
        return outputFilePath;


    }


    /**
     * 使用 Hutool 将 JSON 字符串解析为 List<ValidationResult>
     */
    private static List<ValidationResult> parseJsonToValidationResults(String jsonResult) throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(jsonResult)) {
            Type listType = new TypeToken<List<ValidationResult>>() {
            }.getType();
            return gson.fromJson(reader, listType);
        }
    }

    /**
     * 根据输入文件生成输出文件路径
     */
    private static String generateOutputFilePath(String inputFilePath) {
        // 生成输出文件路径，例如 "your-document_modified.docx"
        return inputFilePath.replace(".json", "_modified.docx");
    }

    /**
     * 使用 List<ValidationResult> 来修改 Word 文档的字体和字号
     *
     * @param isAddPicture 如果为 true，使用枚举中定义的字体和字号；如果为 false，使用 ValidationResult 中的字体和字号
     */
    public static void modifyWordDocument(Document document, List<ValidationResult> validationResults, Boolean isAddPicture) {
        for (ValidationResult result : validationResults) {
            int paragraphIndex = result.getParagraphIndex();
            int sectionIndex = result.getSectionIndex();
            Section section = document.getSections().get(sectionIndex);
            if (section == null) {
                continue;
            }
            DocumentObject documentObject = section.getBody().getChildObjects().get(paragraphIndex);
            if (documentObject == null) {
                continue;
            }
            // 定位到文档中的段落
            if (documentObject instanceof Paragraph) {
                Paragraph paragraph = (Paragraph) documentObject;

                // 遍历 ValidationResult 中的 TextElement
                for (TextElement textElement : result.getTextElements()) {
                    int childObjectsIndex = textElement.getChildObjectsIndex();  // 文字元素的索引

                    if (childObjectsIndex < paragraph.getChildObjects().getCount()) {
                        DocumentObject obj = paragraph.getChildObjects().get(childObjectsIndex);

                        // 修改字体和字号
                        if (obj instanceof TextRange) {
                            TextRange textRange = (TextRange) obj;

                            // 根据参数决定使用哪种字体和字号
                            if (isAddPicture) {
                                // 使用枚举中的字体和字号
                                ContentType contentType = ContentType.valueOf(result.getTypeName());
                                textRange.getCharacterFormat().setFontName(contentType.getExpectedFont());
                                float v = Float.parseFloat(contentType.getExpectedFontSize());
                                textRange.getCharacterFormat().setFontSize(v);
                            } else {
                                // 使用 ValidationResult 中的字体和字号
                                if (textElement.getFont() != null) {
                                    textRange.getCharacterFormat().setFontName(textElement.getFont());
                                }
                                if (textElement.getFontSize() != null) {
                                    textRange.getCharacterFormat().setFontSize(Float.parseFloat(textElement.getFontSize()));
                                }
                            }
                        }
                    }

                }
            }
        }
    }

}