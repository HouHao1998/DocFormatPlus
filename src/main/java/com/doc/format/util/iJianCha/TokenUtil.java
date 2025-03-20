package com.doc.format.util.iJianCha;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/8/28 14:37
 */
public class TokenUtil {

    private static final String API_TOKEN_URL = "https://www.ijiaodui.com:8080/component/v1/api_token";
    private static final String APP_ID = "EyjUrJhkSoNKRXjhVXyIPo0iefMHeG4Q";
    private static final String APP_SECRET = "GlE07lhORGIdRVDso3ITD4Q577szZAnB";
    private static final int TOKEN_VALIDITY = 7200; // Token有效期为7200秒（2小时）
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static String accessToken;
    private static long tokenExpiryTime;

    /**
     * @return
     * @throws Exception
     */
    public static synchronized String getAccessToken() throws Exception {
        if (accessToken == null || System.currentTimeMillis() >= tokenExpiryTime) {
            requestNewToken();
        }
        return accessToken;
    }

    /**
     * 请求新的令牌并处理 token 的有效期
     *
     * @throws Exception 请求新令牌时抛出的异常
     */
    private static void requestNewToken() throws Exception {
        URL url = new URL(API_TOKEN_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/json");

        String requestBody = String.format("{\"app_id\":\"%s\",\"app_secret\":\"%s\"}", APP_ID, APP_SECRET);
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode responseJson = mapper.readTree(connection.getInputStream());

            accessToken = responseJson.get("access_token").asText();
            int expiresIn = responseJson.get("expires_in").asInt();

            tokenExpiryTime = System.currentTimeMillis() + (expiresIn - 600) * 1000L; // 提前10分钟刷新
            scheduleTokenRefresh();
        } else {
            throw new RuntimeException("Failed to fetch access_token, HTTP response code: " + connection.getResponseCode());
        }
    }

    /**
     * 安排令牌刷新任务
     */
    private static void scheduleTokenRefresh() {
        long delay = tokenExpiryTime - System.currentTimeMillis() - 600 * 1000L; // 提前10分钟刷新
        scheduler.schedule(() -> {
            try {
                requestNewToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, delay, TimeUnit.MILLISECONDS);
    }

    public static void main(String[] args) {
        try {
            String token = TokenUtil.getAccessToken();
            System.out.println("Access Token: " + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}