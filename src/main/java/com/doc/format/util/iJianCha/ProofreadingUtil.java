package com.doc.format.util.iJianCha;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doc.format.util.entity.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    public static void main(String[] args) {
        wordBatchCheck("/Users/houhao/Downloads/word/7241629a-36f9-40ab-b07e-2ebd65c211fd/documentElements.json");
    }

    /**
     * 教研word批量校对示例
     */

    public static String wordBatchCheck(String path) {
        try {
            // 获取 accessToken
            String accessToken = TokenUtil.getAccessToken();

            // 读取 JSON 文件并解析为 List<DocumentElement>
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(path);
            List<DocumentElement> documentElements = parseDocumentElements(objectMapper.readTree(file).toString());

            List<String> batches = new ArrayList<>();
            List<List<ParagraphElement>> batchParagraphs = new ArrayList<>(); // 记录每批次的段落
            List<CheckResponse.Result.Mistake> mistakes = new ArrayList<>();

            StringBuilder currentBatch = new StringBuilder();
            int currentBatchSize = 0;
            int batchSize = 1000;
            List<ParagraphElement> currentBatchParagraphs = new ArrayList<>();

            for (DocumentElement element : documentElements) {
                if (element instanceof ParagraphElement) {
                    ParagraphElement paragraph = (ParagraphElement) element;
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 如果当前批次内容接近上限，提交当前批次
                    if (currentBatchSize + paragraphLength > batchSize) {
                        batches.add(currentBatch.toString());
                        batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));

                        // 重置当前批次
                        currentBatch = new StringBuilder(paragraphContent).append("\n"); // 注意：保留换行符
                        currentBatchSize = paragraphLength;
                        currentBatchParagraphs.clear();
                        currentBatchParagraphs.add(paragraph);
                    } else {
                        currentBatch.append(paragraphContent).append("\n"); // 每个段落末尾加换行符
                        currentBatchSize += paragraphLength;
                        currentBatchParagraphs.add(paragraph);
                    }
                }
            }

            // 添加最后的批次内容
            if (currentBatchSize > 0) {
                batches.add(currentBatch.toString());
                batchParagraphs.add(new ArrayList<>(currentBatchParagraphs));
            }

            // 调用检测接口并处理错误映射
            for (int i = 0; i < batches.size(); i++) {
                String batch = batches.get(i);
                List<ParagraphElement> paragraphs = batchParagraphs.get(i);

                CheckRequest request = new CheckRequest(batch);
                CheckResponse response = ProofreadingUtil.checkText(accessToken, request);

                // 映射错误到对应段落
                if (response != null && response.getResult() != null) {
                    mapResponseToParagraphs(response, paragraphs, mistakes);
                }
            }

            return JSON.toJSONString(mistakes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 将校对返回的错误信息映射到对应段落
    private static void mapResponseToParagraphs(CheckResponse response, List<ParagraphElement> paragraphElements, List<CheckResponse.Result.Mistake> mistakes) {
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
}

