package com.doc.format.util.spire;

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
import java.util.ArrayList;
import java.util.Base64;
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
public class WordRevisionTest {
    public static void main(String[] args) {
        String filePath = "/Users/houhao/Documents/论文要求/论文测试样例/30 检测认证 2024.03.0114-棉、聚酰胺纤维和氨纶混纺面料定量分析研究.doc";
        try {
            List<DocumentElement> elements = getDocumentElements(filePath);

            // 将结果转换为JSON并输出
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(elements);

            List<ValidationResult> validationResults = new ArrayList<>();
            DocumentFormatChecker.checkDocumentContent(elements, validationResults);

            System.out.println("检测完成的内容" + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(validationResults));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<DocumentElement> getDocumentElements(String filePath) {
        // 加载测试文档
        Document document = new Document(filePath);

        //判断文档是否有修改
        if (document.hasChanges()) {

            //接受修订
            document.acceptChanges();
            //拒绝修订
            //doc.rejectChanges();
        }

        // 用于存储所有提取的段落、表格、图片元素
        List<DocumentElement> elements = new ArrayList<>();
        int startIndex = 1;
        Integer idx = 0;
        // 循环遍历各个节
        for (int i = 0; i < document.getSections().getCount(); i++) {

            // 获取当前节
            Section section = document.getSections().get(i);
            Body body = document.getSections().get(i).getBody();
            // 循环遍历特定节的段落
            for (int j = 0; j < body.getChildObjects().getCount(); j++) {
                DocumentObject documentObject = body.getChildObjects().get(j);

                if (documentObject instanceof Paragraph) {
                    // 获取特定段落
                    Paragraph paragraph = (Paragraph) documentObject;
                    // 创建 ParagraphElement 实例
                    ParagraphElement paragraphElement = new ParagraphElement();
                    paragraphElement.setType(ElementType.TEXT);
                    paragraphElement.setAlign(paragraph.getFormat().getHorizontalAlignment().name());
                    paragraphElement.setFirstLineIndent(String.valueOf(paragraph.getFormat().getFirstLineIndent()));
                    paragraphElement.setSectionIndex(i);
                    paragraphElement.setParagraphIndex(j);

                    List<Field> fields = new ArrayList<>();
                    // 处理段落中的文字
                    List<TextElement> textElements = new ArrayList<>();
                    StringBuilder content = new StringBuilder();
                    int k = 0;
                    int startIndexOnParagraph = 0;
                    paragraph.setText("12313");
                    for (Object obj : paragraph.getChildObjects()) {

                        if (obj instanceof TextRange) {
                            TextRange textRange = (TextRange) obj;
                            TextElement textElement = new TextElement();
                            textElement.setType(ElementType.TEXT);
                            textElement.setContent(textRange.getText());
                            textElement.setFont(textRange.getCharacterFormat().getFontName());
                            textElement.setFontSize(String.valueOf(textRange.getCharacterFormat().getFontSize()));
                            textElement.setBold(textRange.getCharacterFormat().getBold());
                            textElement.setItalic(textRange.getCharacterFormat().getItalic());
                            textElement.setColor(convertColorToHex(textRange.getCharacterFormat().getTextColor()));
                            textElement.setUnderline(textRange.getCharacterFormat().getUnderlineStyle() != UnderlineStyle.None);
                            textElement.setSectionIndex(i);
                            textElement.setParagraphIndex(j);
                            textElement.setChildObjectsIndex(k);
                            textElement.setIdx(idx++);
                            textElement.setStartIndex(startIndex);
                            textElement.setStartOnPage(startIndexOnParagraph);
                            startIndex += textRange.getText().length();
                            startIndexOnParagraph += textRange.getText().length();
                            textElement.setEndIndex(startIndex);
                            textElement.setEndOnPage(startIndexOnParagraph);
                            textElements.add(textElement);

                            content.append(textRange.getText());

                        } else if (obj instanceof DocPicture) {
                            // 处理图片
                            DocPicture picture = (DocPicture) obj;
                            ImageElement imageElement = new ImageElement();
                            imageElement.setType(ElementType.IMAGE);
                            imageElement.setWidth((long) picture.getWidth());
                            imageElement.setHeight((long) picture.getHeight());
                            imageElement.setAlignment(paragraph.getFormat().getHorizontalAlignment().name());

                            // 保存图片并将其路径存储到 localPath
                            String localPath = saveImage(picture.getImage(), j);
                            // 保存图片到本地
                            imageElement.setLocalPath(localPath);
                            imageElement.setSectionIndex(i);
                            imageElement.setParagraphIndex(j);
                            imageElement.setChildObjectsIndex(k);

                            elements.add(imageElement);
                        }
                        // 如果是 Field，表示段落中的超链接部分
                        if (obj instanceof Field) {
                            Field field = (Field) obj;
                            if (field.getType().equals(FieldType.Field_Hyperlink)) {
                                // 记录超链接字段
                                fields.add(field);
                            }
                        }
                        k++;
                    }
                    for (Field field : fields) {
                        String hyperlinkAddress = field.getValue();
                        ApplyHyperlinkToText(field, hyperlinkAddress, section.getBody(), textElements);
                    }

                    paragraphElement.setTextElements(textElements);
                    paragraphElement.setContent(content.toString().trim());
                    elements.add(paragraphElement);
                } else if (documentObject instanceof Table) {
                    // 遍历当前节中的表格
                    Table table = (Table) documentObject;
                    TableElement tableElement = new TableElement();
                    tableElement.setType(ElementType.TABLE);
                    List<List<String>> tableContent = new ArrayList<>();

                    // 遍历表格中的行
                    for (int row = 0; row < table.getRows().getCount(); row++) {
                        TableRow tableRow = table.getRows().get(row);
                        List<String> rowData = new ArrayList<>();

                        // 遍历每行中的单元格¡
                        for (int cell = 0; cell < tableRow.getCells().getCount(); cell++) {
                            TableCell tableCell = tableRow.getCells().get(cell);
                            StringBuilder cellContent = new StringBuilder();

                            // 遍历单元格中的段落
                            for (int paragraphIndex = 0; paragraphIndex < tableCell.getParagraphs().getCount(); paragraphIndex++) {
                                Paragraph paragraph = tableCell.getParagraphs().get(paragraphIndex);
                                // 获取段落中的文本内容
                                cellContent.append(paragraph.getText().trim());

                                // 遍历段落中的所有子对象，检查是否包含图片
                                for (int objectIndex = 0; objectIndex < paragraph.getChildObjects().getCount(); objectIndex++) {
                                    Object object = paragraph.getChildObjects().get(objectIndex);

                                    // 检查对象是否为图片
                                    if (object instanceof DocPicture) {
                                        DocPicture picture = (DocPicture) object;
                                        // 将图片的路径或其他信息添加到 cellContent
                                        String pictureInfo = "Image: " + picture.getImage();
                                        cellContent.append(pictureInfo);
                                    }
                                }
                            }
                            // 将单元格的所有内容添加到行数据中
                            rowData.add(cellContent.toString().trim());
                        }
                        // 将每行的数据添加到表格内容中
                        tableContent.add(rowData);
                    }

                    // 将表格内容存储到 TableElement 对象中
                    tableElement.setTableContent(tableContent);
                    // 假设你有一个段落索引
                    tableElement.setSectionIndex(i);
                    elements.add(tableElement);
                }

            }

        }
        document.saveToFile("DeleteBlankParas.docx", FileFormat.Docx_2013);
        return elements;
    }

    // 将 BufferedImage 转换为 Base64 字符串
    private static String convertImageToBase64(BufferedImage image) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将图片保存到指定路径
    private static String saveImage(BufferedImage image, int index) {
        try {
            String filePath = String.format("/Users/houhao/Downloads/file/extracted_image_%d.png", index);
            File outputfile = new File(filePath);
            ImageIO.write(image, "png", outputfile);
            // 返回图片保存的本地路径
            return outputfile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 颜色转换方法：将 Color 转换为十六进制颜色代码
    private static String convertColorToHex(Color color) {
        if (color == null) {
            // 默认返回黑色
            return "#000000";
        }
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private static void writeHtmlToFile(String htmlContent, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(htmlContent);
            System.out.println("HTML 文件已成功写入到: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 应用超链接到指定范围的 TextRange
    private static void ApplyHyperlinkToText(Field field, String hyperlinkAddress, Body ownerBody, List<TextElement> textElements) {
        // 获取分隔符的段落和索引
        Paragraph separatorPara = field.getSeparator().getOwnerParagraph();
        int sepOwnerParaIndex = field.getSeparator().getOwnerParagraph().getOwnerTextBody().getChildObjects().indexOf(field.getSeparator().getOwnerParagraph());
        int sepIndex = field.getSeparator().getOwnerParagraph().getChildObjects().indexOf(field.getSeparator());

        // 获取结束符的段落和索引
        int endOwnerParaIndex = field.getEnd().getOwnerParagraph().getOwnerTextBody().getChildObjects().indexOf(field.getEnd().getOwnerParagraph());
        int endIndex = field.getEnd().getOwnerParagraph().getChildObjects().indexOf(field.getEnd());

        // 遍历从 Separator 到 End 范围内的 TextRange 并应用超链接
        for (int i = sepOwnerParaIndex; i <= endOwnerParaIndex; i++) {
            Paragraph para = (Paragraph) ownerBody.getChildObjects().get(i);

            // 如果起始段落和结束段落是同一个段落
            if (i == sepOwnerParaIndex && i == endOwnerParaIndex) {
                for (int j = sepIndex + 1; j < endIndex; j++) {
                    FormatText((TextRange) para.getChildObjects().get(j), hyperlinkAddress, textElements);
                }
            }
            // 如果是起始段落，但不是结束段落
            else if (i == sepOwnerParaIndex) {
                for (int j = sepIndex + 1; j < para.getChildObjects().getCount(); j++) {
                    FormatText((TextRange) para.getChildObjects().get(j), hyperlinkAddress, textElements);
                }
            }
            // 如果是结束段落
            else if (i == endOwnerParaIndex) {
                for (int j = 0; j < endIndex; j++) {
                    FormatText((TextRange) para.getChildObjects().get(j), hyperlinkAddress, textElements);
                }
            }
            // 如果是中间段落
            else {
                for (int j = 0; j < para.getChildObjects().getCount(); j++) {
                    FormatText((TextRange) para.getChildObjects().get(j), hyperlinkAddress, textElements);
                }
            }
        }
    }

    // 将超链接应用到 TextRange
    private static void FormatText(TextRange textRange, String hyperlinkAddress, List<TextElement> textElements) {
        for (TextElement textElement : textElements) {
            if (textElement.getContent().equals(textRange.getText())) {
                textElement.setHyperlink(hyperlinkAddress);
            }
        }

    }
}