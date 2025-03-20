package com.doc.format.util.deep;

import com.doc.format.enums.DocElementTypeEnum;
import com.doc.format.util.JedisUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.spire.doc.*;
import com.spire.doc.documents.Paragraph;
import com.spire.doc.fields.TextRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;


import static com.doc.format.util.JsonBracketCompleter.completeBrackets;
@Service
@Slf4j
public class DocumentClassifierUtil {
    @Value("${deepSheep.api.key}")
    private String apiKey;
    private static final String FORMAT_REASONING_CONTENT = "format:{id}:reasoning_content";
    private static final String FORMAT_CONTENT = "format:{id}:content";
    @Resource
    private JedisUtil jedisUtil;
    private final ObjectMapper mapper = new ObjectMapper();
    private final CloseableHttpClient httpClient = HttpClients.createDefault();

    public void saveDocumentParagraphs(List<DocumentParagraph> paragraphs, String outputFilePath) {
        try {
            File file = new File(outputFilePath);
            // 序列化为JSON
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, paragraphs);

            log.info("成功保存到: " + file.getAbsolutePath());
        } catch (IOException e) {
            log.error("保存失败: " + e.getMessage());
        }
    }

    // 构建分类请求
    public String callClassificationApi(String requestBody,Long id) throws Exception {
        String API_URL = "https://api.deepseek.com/v1/chat/completions";
        HttpPost request = new HttpPost(API_URL);
        request.setHeader("Authorization", "Bearer " + apiKey);
        request.setHeader("Content-Type", "application/json; charset=UTF-8");
        // 如果 API 要求，可以设置 Accept 头为流式返回格式
        request.setHeader("Accept", "text/event-stream");

        // 设置请求体编码
        StringEntity entity = new StringEntity(requestBody, "UTF-8");
        request.setEntity(entity);

        // 用于拼接流式返回的内容
        StringBuilder resultBuilder = new StringBuilder();
        StringBuilder reasoningContentBuilder = new StringBuilder();

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            InputStream inputStream = response.getEntity().getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            // 持续读取流式返回的数据
            while ((line = reader.readLine()) != null) {
                log.info(line);
                // 过滤掉非数据行，比如 SSE 格式可能包含空行或其他前缀
                if (line.startsWith("data: ")) {
                    // 去掉 "data: " 前缀
                    String jsonPart = line.substring("data: ".length());
                    if (jsonPart.isEmpty() || jsonPart.equals("[DONE]")) {
                        // 跳过空或结束标记
                        continue;
                    }
                    // 使用 Jackson 解析 JSON 字符串
                    JsonNode jsonNode = mapper.readTree(jsonPart);
                    JsonNode choices = jsonNode.get("choices");
                    if (choices != null && choices.isArray()) {
                        for (JsonNode choice : choices) {
                            JsonNode delta = choice.get("delta");
                            if (delta != null) {
                                JsonNode content = delta.get("content");
                                if (content != null && !content.asText().equals("null")) {
                                    // 将当前 chunk 的文本内容拼接到结果中
                                    resultBuilder.append(content.asText());
                                    jedisUtil.set(FORMAT_CONTENT.replace("{id}", String.valueOf(id)), content.asText());

                                }
                                JsonNode reasoningContent = delta.get("reasoning_content");
                                if (reasoningContent != null && !reasoningContent.asText().equals("null")) {
                                    reasoningContentBuilder.append(reasoningContent.asText());
                                   jedisUtil.set(FORMAT_REASONING_CONTENT.replace("{id}", String.valueOf(id)), reasoningContent.asText());
                                }
                            }
                        }
                    }
                }
            }
        }
        // 返回最终拼接后的字符串
        return resultBuilder.toString();
    }

    public String buildClassificationRequest(List<DocumentParagraph> paragraphs) throws Exception {
        ObjectNode requestNode = mapper.createObjectNode();
        ArrayNode paragraphsNode = requestNode.putArray("paragraphs");

        for (DocumentParagraph para : paragraphs) {
            ObjectNode paraNode = mapper.createObjectNode();
            paraNode.put("paragraph_id", para.id);
            paraNode.put("text_content", para.content);
            paragraphsNode.add(paraNode);
        }

        // 注意：添加 "stream": true 参数以启用流式响应
        return mapper.writeValueAsString(Map.of(
                // "model", "deepseek-reasoner",
                "model", "deepseek-chat",
                "temperature", 1.0,
                "stream", true,
                "messages", new Object[]{
                        Map.of("role", "system", "content", buildSystemPrompt()),
                        Map.of("role", "user", "content", requestNode.toString())
                }
        ));
    }

    // 构建系统提示词
    private String buildSystemPrompt() {
        String template = "  请严格按照以下规则对文档段落进行分类：\n" +
                "\n" +
                "            ## 分类规则\n" +
                "            1. 根据段落内容和位置识别以下类型，每个段落可能对应多个类型和内容，但这些类型和内容必须作为一个整体进行处理：\n" +
                "               ${type_mapping}\n" +
                "\n" +
                "            2. 识别优先级：\n" +
                "               a) 显式标识符（如\"摘要：\"、\"关键词：\"）\n" +
                "               b) 位置特征（如第一个段落通常为标题）\n" +
                "               c) 内容模式（如参考文献的编号格式）\n" +
                "\n" +
                "            3. 严格要求：\n" +
                "               - 不得修改段落编码（paragraph_id），保持原始编码不变。\n" +
                "\n" +
                "            4. 返回JSON格式：\n" +
                "               {\n" +
                "                 \"results\": [\n" +
                "                   {\n" +
                "                     \"paragraph_id\": 0,\n" +
                "                     \"details\": [\n" +
                "                       {\n" +
                "                         \"type\": \"CN_TITLE\",\n" +
                "                         \"content\": \"...\"\n" +
                "                       },\n" +
                "                       {\n" +
                "                         \"type\": \"CN_ABSTRACT_TITLE\",\n" +
                "                         \"content\": \"...\"\n" +
                "                       }\n" +
                "                     ]\n" +
                "                   }\n" +
                "                 ]\n" +
                "               }\n" +
                "\n" +
                "            ## 类型映射表\n" +
                "            ${type_table}";

        return template
                .replace("${type_mapping}", buildTypeMapping())
                .replace("${type_table}", buildTypeTable());
    }

    private String buildTypeMapping() {
        return String.join("\n", Arrays.stream(DocElementTypeEnum.values())
                .map(t -> String.format("- %-15s → %s", t.name(), getTypeDescription(t)))
                .toArray(String[]::new));
    }

    private String buildTypeTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("  | 类型名称       | 识别特征                       | 字号字体                     | 排版字号 |\n");
        sb.append("  |----------------|--------------------------------|-----------------------------|----------|\n");
        for (DocElementTypeEnum type : DocElementTypeEnum.values()) {
            sb.append(String.format("  | %-14s | %-30s | %-27s | %-8s |\n",
                    type.getTypeName(), type.getFeature(), type.getFontSize(), type.getLayoutSize()));
        }
        return sb.toString();
    }

    private String getTypeDescription(DocElementTypeEnum type) {
        return type.getDescription();
    }

    // 解析API响应
    // private  List<ClassificationResult> parseApiResponse(String json) throws Exception {
    //     // 解析整体返回结构
    //     JsonNode root = mapper.readTree(json);
    //     // 获取 message 中的 content 字段，该字段包含 markdown 格式的代码块
    //     String content = root.path("choices").get(0)
    //             .path("message").path("content").asText();
    //
    //     // 去除 markdown 代码块标记，例如：```json 和结尾的 ```
    //     content = content.replaceAll("^```json\\s*", "").replaceAll("```\\s*$", "");
    //
    //     // 解析提取出的纯 JSON 字符串
    //     JsonNode contentNode = mapper.readTree(content);
    //     ArrayNode results = (ArrayNode) contentNode.path("results");
    //
    //     List<ClassificationResult> list = new ArrayList<>();
    //     for (JsonNode node : results) {
    //         // 获取段落编码，不允许修改
    //         int paragraphId = node.has("paragraph_id") ? node.get("paragraph_id").asInt() : -1;
    //         // 遍历每个段落下的详情列表，构造 Detail 列表
    //         List<Detail> detailsList = new ArrayList<>();
    //         ArrayNode details = (ArrayNode) node.path("details");
    //         for (JsonNode detail : details) {
    //             String typeStr = detail.has("type") ? detail.get("type").asText() : "";
    //             try {
    //                 // 安全转换枚举类型
    //                 DocElementTypeEnum type = DocElementTypeEnum.valueOf(typeStr);
    //                 String detailContent = detail.has("content") ? detail.get("content").asText() : "";
    //                 detailsList.add(new Detail(type, detailContent));
    //             } catch (IllegalArgumentException e) {
    //                 log.error("无效的类型值: " + typeStr + "，该段落中的该详情将被跳过");
    //             }
    //         }
    //         list.add(new ClassificationResult(paragraphId, detailsList));
    //     }
    //     return list;
    // }
    public List<ClassificationResult> parseApiResponse(String json) throws Exception {

        // 去除 markdown 代码块标记，例如：```json 和结尾的 ```
        json = json.replaceAll("^```json\\s*", "").replaceAll("```\\s*$", "");
        completeBrackets(json);
        // 解析提取出的纯 JSON 字符串
        JsonNode contentNode = mapper.readTree(json);
        ArrayNode results = (ArrayNode) contentNode.path("results");

        List<ClassificationResult> list = new ArrayList<>();
        for (JsonNode node : results) {
            // 获取段落编码，不允许修改
            int paragraphId = node.has("paragraph_id") ? node.get("paragraph_id").asInt() : -1;
            // 遍历每个段落下的详情列表，构造 Detail 列表
            List<Detail> detailsList = new ArrayList<>();
            ArrayNode details = (ArrayNode) node.path("details");
            for (JsonNode detail : details) {
                String typeStr = detail.has("type") ? detail.get("type").asText() : "";
                try {
                    // 安全转换枚举类型
                    DocElementTypeEnum type = DocElementTypeEnum.valueOf(typeStr);
                    String detailContent = detail.has("content") ? detail.get("content").asText() : "";
                    detailsList.add(new Detail(type, detailContent));
                } catch (IllegalArgumentException e) {
                    log.error("无效的类型值: " + typeStr + "，该段落中的该详情将被跳过");
                }
            }
            list.add(new ClassificationResult(paragraphId, detailsList));
        }
        return list;
    }

    // 生成输出报告，按照新的数据结构输出
    public void generateOutputReport(List<ClassificationResult> results) throws Exception {
        ObjectNode report = mapper.createObjectNode();
        ArrayNode items = report.putArray("classification_results");

        for (ClassificationResult result : results) {
            ObjectNode paragraphNode = mapper.createObjectNode();
            paragraphNode.put("paragraph_id", result.paragraphId);
            ArrayNode detailsArray = paragraphNode.putArray("details");
            for (Detail detail : result.details) {
                detailsArray.add(mapper.createObjectNode()
                        .put("type", detail.type.name())
                        .put("content", detail.content));
            }
            items.add(paragraphNode);
        }

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(new File("classification_report.json"), report);
    }

    public List<DocumentParagraph> getDocumentElements(String filePath) {
        // 加载测试文档
        Document document = new Document(filePath);

        //判断文档是否有修改
        if (document.hasChanges()) {

            //接受修订
            document.acceptChanges();
        }

        // 用于存储所有提取的段落、表格、图片元素
        List<DocumentParagraph> elements = new ArrayList<>();
        int pid = 0;
        // 循环遍历各个节
        for (int i = 0; i < document.getSections().getCount(); i++) {

            Body body = document.getSections().get(i).getBody();
            // 循环遍历特定节的段落
            for (int j = 0; j < body.getChildObjects().getCount(); j++) {
                DocumentObject documentObject = body.getChildObjects().get(j);
                pid++;
                if (documentObject instanceof Paragraph) {
                    // 获取特定段落
                    Paragraph paragraph = (Paragraph) documentObject;
                    DocumentParagraph documentParagraph = new DocumentParagraph(pid, paragraph.getText());

                    if (!paragraph.getText().equals("Evaluation Warning: The document was created with Spire.Doc for JAVA.")) {
                        elements.add(documentParagraph);
                    }

                }
            }

        }
        return elements;
    }

    public void getDocumentElements(String filePath, List<ClassificationResult> classificationResults, String outputFilePath) {
        // 加载测试文档
        Document document = new Document(filePath);

        // 判断文档是否有修改，接受修订
        if (document.hasChanges()) {
            document.acceptChanges();
        }

        int pid = 0;
        // 循环遍历各个节
        for (int i = 0; i < document.getSections().getCount(); i++) {
            Body body = document.getSections().get(i).getBody();
            // 遍历当前节中的所有子对象
            for (int j = 0; j < body.getChildObjects().getCount(); j++) {
                DocumentObject documentObject = body.getChildObjects().get(j);
                pid++;
                if (documentObject instanceof Paragraph) {
                    // 记录段落序号（假设文档中每个 Paragraph 都对应一个段落编号）
                    Paragraph paragraph = (Paragraph) documentObject;

                    // 尝试从 classificationResults 中查找与当前段落 pid 匹配的结果
                    ClassificationResult result = null;
                    for (ClassificationResult cr : classificationResults) {
                        if (cr.getParagraphId() == pid) {
                            result = cr;
                            break;
                        }
                    }
                    if (result != null) {
                        // 清空当前段落原有内容（不保留图片）
                        paragraph.getChildObjects().clear();

                        // 针对该段落中的每个 detail，创建新的 TextRange 并设置文本和格式
                        for (Detail detail : result.getDetails()) {
                            // 根据 detail.type 获取对应的枚举，注意枚举名称必须匹配 detail.type
                            DocElementTypeEnum elementType = detail.getType();
                            // 创建新的 TextRange 并设置内容
                            TextRange textRange = new TextRange(document);
                            textRange.setText(detail.getContent());
                            // 设置字体：使用枚举中定义的 font
                            textRange.getCharacterFormat().setFontName(elementType.getFont());
                            // 设置字号：使用枚举中定义的 layoutSize（字符串数值转换为 float）
                            try {
                                float size = Float.parseFloat(elementType.getLayoutSize());
                                textRange.getCharacterFormat().setFontSize(size);
                            } catch (NumberFormatException e) {
                                // 转换失败时，可设置默认字号
                                textRange.getCharacterFormat().setFontSize(12);
                            }
                            // 可根据需要进一步设置字体样式，如粗体、斜体等

                            // 将设置好的 TextRange 追加到当前段落
                            paragraph.getChildObjects().add(textRange);
                        }
                    }
                } else if (documentObject instanceof Table) {
                    log.info("段落" + pid + "是表格");
                }
            }
        }
        // 保存修改后的文档
        document.saveToFile(outputFilePath, FileFormat.Docx);
    }

    /**
     * 解析本地 JSON 文件 classification_report.json，返回 List<ClassificationResult>
     *
     * @param jsonFilePath JSON 文件的路径
     * @return List<ClassificationResult>
     * @throws Exception 当解析文件时发生异常
     */
    public List<ClassificationResult> parseClassificationReport(String jsonFilePath) throws Exception {
        // 创建 Jackson 的 ObjectMapper 对象
        ObjectMapper mapper = new ObjectMapper();
        // 从文件读取 JSON 数据
        File jsonFile = new File(jsonFilePath);
        JsonNode rootNode = mapper.readTree(jsonFile);

        // 根据文件格式，获取根节点中的 classification_results 数组
        ArrayNode results = (ArrayNode) rootNode.path("classification_results");

        List<ClassificationResult> list = new ArrayList<>();
        // 遍历每个段落对应的节点
        for (JsonNode node : results) {
            // 获取段落编号，若不存在则返回 -1
            int paragraphId = node.has("paragraph_id") ? node.get("paragraph_id").asInt() : -1;
            // 遍历当前段落下的 details 数组，构造 Detail 列表
            List<Detail> detailsList = new ArrayList<>();
            ArrayNode details = (ArrayNode) node.path("details");
            for (JsonNode detail : details) {
                String typeStr = detail.has("type") ? detail.get("type").asText() : "";
                String content = detail.has("content") ? detail.get("content").asText() : "";
                try {
                    // 安全转换枚举类型
                    DocElementTypeEnum elementType = DocElementTypeEnum.valueOf(typeStr);
                    detailsList.add(new Detail(elementType, content));
                } catch (IllegalArgumentException e) {
                    log.error("无效的类型值: " + typeStr + "，该段落中的该详情将被跳过");
                }
            }
            list.add(new ClassificationResult(paragraphId, detailsList));
        }
        return list;
    }


}