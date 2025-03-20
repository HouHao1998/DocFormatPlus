package com.doc.format.util.iJianCha;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doc.format.util.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spire.doc.Body;
import com.spire.doc.Document;
import com.spire.doc.DocumentObject;
import com.spire.doc.documents.Paragraph;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/8/28 14:50
 */


public class ProofreadingUtil {

    private static final String CHECK_API_URL = "https://www.ijiaodui.com:8080/component/v2/check?access_token=";
    private static final String CHECK_TIME_API_URL = "https://www.ijiaodui.com:8080/component/v1/get_check_time?access_token=";
    private static final String REMAINING_NUM_API_URL = "https://www.ijiaodui.com:8080/component/v1/get_remaining_num?access_token=";
    private static final String RECOMMENDATION_LEVEL_API_URL = "https://www.ijiaodui.com:8080/component/v1/get_recommendation_level_info?access_token=";
    private static final String RECOMMENDATION_CATEGORIES_API_URL = "https://www.ijiaodui.com:8080/component/v1/get_recommendation_categories_info?access_token=";


    /**
     * 校对文本
     *
     * @param accessToken 获取到的access_token
     * @param request     校对请求参数封装对象
     * @return 校对结果的CheckResponse对象
     * @throws Exception 调用校对接口时抛出的异常
     */
    public static CheckResponse checkText(String accessToken, CheckRequest request) throws Exception {
        String apiUrl = CHECK_API_URL + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        ObjectMapper mapper = new ObjectMapper();
        String requestBody = mapper.writeValueAsString(request);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            System.out.println(connection.getInputStream());
            return mapper.readValue(connection.getInputStream(), CheckResponse.class);
        } else {
            throw new RuntimeException("Failed to call check API, HTTP response code: " + connection.getResponseCode());
        }
    }

    /**
     * 估算校对耗时
     *
     * @param accessToken 获取到的access_token
     * @param textLen     需要校对的文本长度
     * @return 预估的校对耗时（秒）
     * @throws Exception 调用估算时间接口时抛出的异常
     */
    public static int getCheckTime(String accessToken, int textLen) throws Exception {
        String apiUrl = CHECK_TIME_API_URL + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // 构建请求体
        String requestBody = String.format("{\"text_len\":%d}", textLen);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 处理响应
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            // 从响应中解析出cost_time
            JsonNode responseJson = mapper.readTree(connection.getInputStream());
            return responseJson.get("cost_time").asInt();
        } else {
            throw new RuntimeException("Failed to call get_check_time API, HTTP response code: " + connection.getResponseCode());
        }
    }

    /**
     * 查询剩余校对字数
     *
     * @param accessToken 获取到的access_token
     * @return 剩余的校对字数
     * @throws Exception 调用查询剩余校对字数接口时抛出的异常
     */
    public static int getRemainingNum(String accessToken) throws Exception {
        String apiUrl = REMAINING_NUM_API_URL + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        // 发送空的请求体
        String requestBody = "{}";
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // 处理响应
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            // 从响应中解析出available_count
            JsonNode responseJson = mapper.readTree(connection.getInputStream());
            return responseJson.get("available_count").asInt();
        } else {
            throw new RuntimeException("Failed to call get_remaining_num API, HTTP response code: " + connection.getResponseCode());
        }
    }

    /**
     * 获取推荐程度类型以及描述信息
     *
     * @param accessToken 获取到的access_token
     * @return 推荐程度信息列表，包含类型和描述
     * @throws Exception 调用获取推荐程度信息接口时抛出的异常
     */
    public static List<RecommendationLevel> getRecommendationLevelInfo(String accessToken) throws Exception {
        String apiUrl = RECOMMENDATION_LEVEL_API_URL + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        // 处理响应
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(connection.getInputStream());

            List<RecommendationLevel> recommendationLevels = new ArrayList<>();
            JsonNode levelInfoArray = responseJson.get("recommendation_level_info");
            for (JsonNode levelInfo : levelInfoArray) {
                int type = levelInfo.get(0).asInt();
                String description = levelInfo.get(1).asText();
                recommendationLevels.add(new RecommendationLevel(type, description));
            }
            return recommendationLevels;
        } else {
            throw new RuntimeException("Failed to call get_recommendation_level_info API, HTTP response code: " + connection.getResponseCode());
        }
    }

    /**
     * 获取推荐类别信息
     *
     * @param accessToken 获取到的access_token
     * @return 推荐类别信息列表，包含类别代码和描述
     * @throws Exception 调用获取推荐类别信息接口时抛出的异常
     */
    public static List<RecommendationCategory> getRecommendationCategoriesInfo(String accessToken) throws Exception {
        String apiUrl = RECOMMENDATION_CATEGORIES_API_URL + accessToken;
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        // 处理响应
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(connection.getInputStream());

            List<RecommendationCategory> recommendationCategories = new ArrayList<>();
            JsonNode categoriesInfoArray = responseJson.get("recommendation_categories_info");
            for (JsonNode categoryInfo : categoriesInfoArray) {
                String code = categoryInfo.get(0).asText();
                String description = categoryInfo.get(1).asText();
                recommendationCategories.add(new RecommendationCategory(code, description));
            }
            return recommendationCategories;
        } else {
            throw new RuntimeException("Failed to call get_recommendation_categories_info API, HTTP response code: " + connection.getResponseCode());
        }
    }


    // 将校对返回的错误信息映射到对应段落
    public static void mapResponseToParagraphs(CheckResponse response, List<ParagraphElement> paragraphElements, List<CheckResponse.Result.Mistake> mistakes) {
        if (response.getResult() != null && response.getResult().getMistakes() != null) {
            int currentOffset = 0; // 用于追踪批次内的段落偏移量
            int lastParagraphIndex = 0; // 记录上一个错误所在的段落索引

            for (CheckResponse.Result.Mistake mistake : response.getResult().getMistakes()) {
                int mistakeStart = mistake.getL();
                int mistakeEnd = mistake.getR();
                boolean isMapped = false;

                for (int i = lastParagraphIndex; i < paragraphElements.size(); i++) { // 从上次处理的段落索引开始
                    ParagraphElement paragraph = paragraphElements.get(i);
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 检查错误是否在当前段落内
                    if (mistakeStart >= currentOffset && mistakeStart < currentOffset + paragraphLength) {
                        String sentence = response.getResult().getSentence();
                        mistake.setPIndex(paragraph.getParagraphIndex()); // 设置段落索引
                        mistake.setPl(mistakeStart - currentOffset);
                        mistake.setPr(Math.min(mistakeEnd, currentOffset + paragraphLength) - currentOffset);
                        mistake.setParagraph(paragraphContent);
                        mistake.setErrorText(paragraphContent.substring(mistake.getPl(), mistake.getPr()));
                        mistake.setErrorTextNew(sentence.substring(mistake.getL(), mistake.getR()));
                        isMapped = true;

                        // 输出校对错误对比
                        if (!mistake.getErrorText().equals(mistake.getErrorTextNew())) {
                            System.out.println("校对错误：" + mistake.getErrorText() + " -> " + mistake.getErrorTextNew());
                        }

                        // 如果错误的结束位置也在当前段落内，说明该错误完全属于当前段落
                        if (mistakeEnd <= currentOffset + paragraphLength) {
                            lastParagraphIndex = i; // 记录当前段落索引，便于下次处理
                            break;
                        } else {
                            // 如果错误跨段落，则继续处理下一个段落
                            mistakeStart = currentOffset + paragraphLength;
                        }
                    }

                    currentOffset += paragraphLength + 1; // 更新偏移量，包含换行符
                }
                mistake.setIdx(mistakes.size());
                mistakes.add(mistake);

                if (!isMapped) {
                    System.out.println("未映射的错误: " + JSON.toJSONString(mistake));
                }
            }
        }
    }

    public static List<DocumentElement> pathToDocumentElements(String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File(path);
        List<DocumentElement> documentElements = null;
        try {
            documentElements = parseDocumentElements(objectMapper.readTree(file).toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return documentElements;
    }

    // 手动解析方法
    public static List<DocumentElement> parseDocumentElements(String json) {
        List<DocumentElement> elements = new ArrayList<>();
        List<JSONObject> jsonArray = JSON.parseArray(json, JSONObject.class);

        for (JSONObject jsonObject : jsonArray) {
            String type = jsonObject.getString("type");

            DocumentElement element;
            switch (type) {
                case "PARAGRAPH":
                    element = jsonObject.toJavaObject(ParagraphElement.class);
                    break;
                case "TABLE":
                    element = jsonObject.toJavaObject(TableElement.class);
                    break;
                case "TEXT":
                    element = jsonObject.toJavaObject(TextElement.class);
                    break;
                case "IMAGE":
                    element = jsonObject.toJavaObject(ImageElement.class);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown type: " + type);
            }

            elements.add(element);
        }
        return elements;
    }

    public static void main(String[] args) throws Exception {
        String accessToken = TokenUtil.getAccessToken();
        //判断文档是否有修改
        String documentElements = getDocumentElements("/Users/houhao/Documents/论文要求/论文测试样例/30 检测认证 2024.03.0114-棉、聚酰胺纤维和氨纶混纺面料定量分析研究.doc");
        CheckResponse checkResponse = checkText(accessToken, new CheckRequest(documentElements));
        System.out.println(JSON.toJSONString(checkResponse));
    }

    public static String getDocumentElements(String filePath) {
        // 加载测试文档
        Document document = new Document(filePath);
        //判断文档是否有修改
        if (document.hasChanges()) {
            //接受修订
            document.acceptChanges();
        }
        StringBuilder text = new StringBuilder();

        // 循环遍历各个节
        for (int i = 0; i < document.getSections().getCount(); i++) {

            Body body = document.getSections().get(i).getBody();
            // 循环遍历特定节的段落
            for (int j = 0; j < body.getChildObjects().getCount(); j++) {
                DocumentObject documentObject = body.getChildObjects().get(j);
                if (documentObject instanceof Paragraph) {
                    // 获取特定段落
                    Paragraph paragraph = (Paragraph) documentObject;
                    text.append(paragraph.getText()).append("\n");

                }
            }

        }
        return text.toString();
    }
}

