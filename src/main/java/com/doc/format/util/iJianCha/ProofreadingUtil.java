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

    }

    /**
     * 教研word批量校对示例
     */

    public static String wordBatchCheck(String path) {
        try {
            // 假设从TokenUtil获取到的access_token
            String accessToken = TokenUtil.getAccessToken();
            // 读取 JSON 文件
            ObjectMapper objectMapper = new ObjectMapper();
            File file = new File(path);

            // 将 JSON 文件映射为 List<DocumentElement>
            List<DocumentElement> documentElements = parseDocumentElements(objectMapper.readTree(file).toString());

            // 调用接口的批次
            List<String> batches = new ArrayList<>();
            Map<String, List<ParagraphElement>> batchToParagraphMap = new HashMap<>();  // 记录每个批次对应的段落
            StringBuilder currentBatch = new StringBuilder();
            int currentBatchSize = 0;
            int batchSize = 1000;
            List<ParagraphElement> currentBatchParagraphs = new ArrayList<>();  // 记录当前批次的段落

            for (DocumentElement element : documentElements) {
                if (element instanceof ParagraphElement) {
                    ParagraphElement paragraph = (ParagraphElement) element;
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 分批处理逻辑
                    if (currentBatchSize + paragraphLength > batchSize) {
                        int differenceIfAdded = Math.abs((currentBatchSize + paragraphLength) - batchSize);
                        int differenceWithoutAdding = Math.abs(currentBatchSize - batchSize);

                        if (differenceIfAdded < differenceWithoutAdding) {
                            currentBatch.append(paragraphContent).append("\n");
                            currentBatchSize += paragraphLength;
                            currentBatchParagraphs.add(paragraph);
                            // 加入当前批次后分组并记录段落
                            batches.add(currentBatch.toString());
                            batchToParagraphMap.put(currentBatch.toString(), new ArrayList<>(currentBatchParagraphs));

                            // 清空当前批次信息
                            currentBatch = new StringBuilder();
                            currentBatchSize = 0;
                            currentBatchParagraphs.clear();
                        } else {
                            // 先分组，不加当前段落
                            batches.add(currentBatch.toString());
                            batchToParagraphMap.put(currentBatch.toString(), new ArrayList<>(currentBatchParagraphs));

                            // 开始新的批次
                            currentBatch = new StringBuilder(paragraphContent).append("\n");
                            currentBatchSize = paragraphLength;
                            currentBatchParagraphs.clear();
                            currentBatchParagraphs.add(paragraph);
                        }
                    } else {
                        currentBatch.append(paragraphContent).append("\n");
                        currentBatchSize += paragraphLength;
                        currentBatchParagraphs.add(paragraph);
                    }
                }
            }

            if (currentBatchSize > 0) {
                batches.add(currentBatch.toString());
                batchToParagraphMap.put(currentBatch.toString(), new ArrayList<>(currentBatchParagraphs));
            }
            List<CheckResponse.Result.Mistake> mistakes = new ArrayList<>();

            // 调用检测接口
            for (String batch : batches) {
                CheckRequest request = new CheckRequest(batch);
                CheckResponse response = ProofreadingUtil.checkText(accessToken, request);

                // 处理校对结果并回填段落
                mapResponseToParagraphs(response, batchToParagraphMap.get(batch), mistakes);
            }
            return JSON.toJSONString(mistakes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // 将校对返回的错误映射到段落，并将段落信息和段落索引位置回填到response中
    private static void mapResponseToParagraphs(CheckResponse response, List<ParagraphElement> paragraphElements, List<CheckResponse.Result.Mistake> mistakes) {
        if (response != null && response.getResult() != null && response.getResult().getMistakes() != null) {
            int currentOffset = 0;  // 当前批次文本中的偏移量

            // 遍历校对结果中的每个错误
            for (CheckResponse.Result.Mistake mistake : response.getResult().getMistakes()) {
                int mistakeStart = mistake.getL();  // 错误全局起始位置
                int mistakeEnd = mistake.getR();    // 错误全局结束位置
                boolean isMapped = false;           // 用于标记该错误是否已被映射


                // 遍历段落，查找错误所在的段落
                for (ParagraphElement paragraph : paragraphElements) {
                    String paragraphContent = paragraph.getContent();
                    int paragraphLength = paragraphContent.length();

                    // 判断错误是否落在当前段落范围内
                    if (mistakeStart >= currentOffset && mistakeStart < currentOffset + paragraphLength) {
                        // 该错误属于此段落，计算错误在段落中的位置
                        mistake.setPIndex(paragraphElements.indexOf(paragraph));  // 设置段落索引
                        mistake.setPl(mistakeStart - currentOffset);  // 段落中的左索引
                        mistake.setPr(Math.min(mistakeEnd, currentOffset + paragraphLength) - currentOffset);  // 段落中的右索引

                        isMapped = true;

                        // 如果错误结束位置也在当前段落内，说明该错误完全属于当前段落
                        if (mistakeEnd <= currentOffset + paragraphLength) {
                            break;  // 完全属于当前段落时，跳出循环
                        } else {
                            // 如果错误跨段落，则处理下一段落的部分
                            mistakeStart = currentOffset + paragraphLength;  // 更新错误起点
                        }
                    }

                    // 更新偏移量以处理下一个段落
                    currentOffset += paragraphLength + 1;  // +1 是因为批次内容中每个段落之间有换行符
                }
                mistakes.add(mistake);
                // 如果错误没有被映射，处理特殊情况（如超出段落边界的错误）
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

