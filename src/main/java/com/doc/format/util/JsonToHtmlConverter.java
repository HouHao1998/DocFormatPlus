// package com.doc.format.util;
//
// import java.io.FileOutputStream;
// import java.io.OutputStreamWriter;
// import java.nio.charset.StandardCharsets;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * <b>请输入名称</b>
//  * <pre>
//  * 描述<br/>
//  * 作用：；<br/>
//  * 限制：；<br/>
//  * </pre>
//  *
//  * @author 侯浩(1272)
//  * @date 2024/8/23 14:51
//  */
// public class JsonToHtmlConverter {
//
//
//     public static void writeHtmlFromParagraphs(List<Map<String, Object>> paragraphsData, String outputFile) throws Exception {
//         try (OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
//
//             StringBuilder htmlContent = new StringBuilder();
//             htmlContent.append("<html><head><meta charset=\"UTF-8\"><title>Document</title></head><body>");
//
//             for (Map<String, Object> paragraphData : paragraphsData) {
//                 String content = (String) paragraphData.get("content");
//                 String alignment = (String) paragraphData.get("alignment");
//                 boolean indentation = (boolean) paragraphData.get("indentation");
//
//                 htmlContent.append("<p style='text-align: ")
//                         .append(alignment)
//                         .append(";");
//
//                 if (indentation) {
//                     htmlContent.append(" text-indent: 2em;");
//                 }
//
//                 htmlContent.append("'>")
//                         .append(content)
//                         .append("</p>");
//             }
//
//             htmlContent.append("</body></html>");
//             out.write(htmlContent.toString());
//         }
//     }
// }