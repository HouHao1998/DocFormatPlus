package com.doc.format.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.doc.format.entity.AiSessionEntity;
import com.doc.format.entity.Result;
import cn.hutool.core.bean.BeanUtil;
import com.doc.format.entity.ChatRecordEntity;
import com.doc.format.mapper.AiSessionMapper;
import com.doc.format.mapper.ChatRecordMapper;
import com.doc.format.service.IChatRecordService;
import com.doc.format.bo.ChatRecordQueryBo;
import com.doc.format.bo.ChatRecordSaveBo;
import com.doc.format.service.IUserService;
import com.doc.format.vo.ChatRecordDetailVo;
import com.doc.format.vo.ChatRecordListVo;
import com.doc.format.vo.UserDetailVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * AI对话记录Service实现类
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Service
public class ChatRecordServiceImpl extends ServiceImpl<ChatRecordMapper, ChatRecordEntity> implements IChatRecordService {
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("${deepSheep.api.key}")
    private String apiKey;
    @Resource
    private AiSessionMapper aiSessionMapper;
    @Resource
    private IUserService userService;

    @Override
    public Result<Page<ChatRecordListVo>> page(ChatRecordQueryBo queryBo) {
        // 转换参数实体
        Page<ChatRecordListVo> page = new Page<>(queryBo.getCurrent(), queryBo.getSize());

        // 调用分页查询方法
        List<ChatRecordListVo> list = baseMapper.getList(page, queryBo);

        // 设置分页结果
        page.setRecords(list);
        page.setTotal(page.getTotal());

        return Result.success(page);
    }

    @Override
    public Result<List<ChatRecordListVo>> list(ChatRecordQueryBo queryBo) {
        // 调用列表查询方法
        return Result.success(baseMapper.getList(queryBo));
    }

    @Override
    public Result<ChatRecordDetailVo> get(long id) {
        // 调用查询方法
        ChatRecordEntity entity = baseMapper.selectById(id);
        // 转换返回实体
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
    }

    @Override
    public Result<ChatRecordDetailVo> insert(ChatRecordSaveBo saveBo) {
        // 转换参数实体
        ChatRecordEntity entity = BeanUtil.copyProperties(saveBo, ChatRecordEntity.class);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        int row = baseMapper.insert(entity);
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
    }

    @Override
    public Result<ChatRecordDetailVo> update(ChatRecordSaveBo saveBo) {
        // 转换参数实体
        ChatRecordEntity entity = BeanUtil.copyProperties(saveBo, ChatRecordEntity.class);
        entity.setUpdateTime(new Date());
        int row = baseMapper.updateById(entity);
        return Result.success(BeanUtil.copyProperties(entity, ChatRecordDetailVo.class));
    }

    @Override
    public Result<String> remove(List<Long> ids) {
        int row = baseMapper.deleteBatchIds(ids);
        if (row != 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }

    @Override
    public Result<String> remove(long id) {
        int row = baseMapper.deleteById(id);
        if (row != 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败");
        }
    }

    @Override
    public Result<List<ChatRecordListVo>> selectIdsList(List<Long> ids) {
        List<ChatRecordEntity> teacherFeedbackEntities = baseMapper.selectList(new LambdaQueryWrapper<ChatRecordEntity>()
                .in(ChatRecordEntity::getId, ids));
        List<ChatRecordListVo> teacherFeedbackListVos = new ArrayList<>();
        for (ChatRecordEntity teacherFeedbackEntity : teacherFeedbackEntities) {
            if (teacherFeedbackEntity.getResponseState().equals("2")) {
                teacherFeedbackEntity.setAiResponse("网络异常，请稍后再试");
            }
            teacherFeedbackListVos.add(BeanUtil.copyProperties(teacherFeedbackEntity, ChatRecordListVo.class));
        }
        return Result.success(teacherFeedbackListVos);
    }

    @Override
    public SseEmitter handleStreamChat(ChatRecordQueryBo request) {
        UserDetailVo user = userService.getUser();
        if (request.getSessionId() == null) {
            AiSessionEntity aiSessionEntity = new AiSessionEntity();
            String userMsg = request.getUserMessage();

            if (userMsg != null && userMsg.length() > 10) {
                userMsg = userMsg.substring(0, 10);
            }
            aiSessionEntity.setSessionName(userMsg);
            aiSessionEntity.setCreateTime(new Date());
            aiSessionEntity.setUserId(user.getId());
            aiSessionMapper.insert(aiSessionEntity);
            request.setSessionId(aiSessionEntity.getId());
        }

        SseEmitter emitter = new SseEmitter(10 * 60 * 1000L); // 10分钟后自动关闭连接
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Long sessionId = request.getSessionId();
        baseMapper.deleteBySessionId(sessionId);
        List<ChatRecordEntity> chatRecordEntities = baseMapper.selectList(new LambdaQueryWrapper<ChatRecordEntity>()
                .eq(ChatRecordEntity::getSessionId, sessionId)
                .ne(ChatRecordEntity::getResponseState, 2)
                .orderByDesc(ChatRecordEntity::getCreateTime) // 改为降序
                .last("LIMIT 5"));
        Collections.reverse(chatRecordEntities);

        executor.execute(() -> {
            try {
                // 1. 创建聊天记录实体（先保存用户消息）
                ChatRecordEntity record = new ChatRecordEntity();
                record.setSessionId(request.getSessionId());
                record.setCreator(String.valueOf(user.getId()));
                record.setUserMessage(request.getUserMessage());
                record.setCreateTime(new Date());
                baseMapper.insert(record);
                chatRecordEntities.add(record);
                // 2. 调用DeepSeek API（示例使用WebClient）
                WebClient client = WebClient.create("https://api.deepseek.com/v1/chat/completions");
                Flux<String> responseStream = client.post()
                        .header("Authorization", "Bearer " + apiKey)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(buildDeepseekRequest(chatRecordEntities))
                        .retrieve()
                        .bodyToFlux(String.class);

                // 3. 流式处理响应
                StringBuilder aiResponse = new StringBuilder();
                StringBuilder aiReasoningContent = new StringBuilder();
                responseStream.subscribe(
                        chunk -> {
                            // 解析chunk并提取内容（需根据实际API响应格式调整）
                            String content = parseChunk(chunk);
                            String reasoningContent = parseReasoningContent(chunk);
                            if (!content.equals("null")) {
                                aiResponse.append(content);
                            }
                            if (!reasoningContent.equals("null")) {
                                aiReasoningContent.append(reasoningContent);
                            }
                            try {
                                emitter.send(SseEmitter.event().data(chunk));
                            } catch (IOException e) {
                                emitter.completeWithError(e);
                                throw new RuntimeException(e);
                            }
                        },
                        error -> {
                            emitter.completeWithError(error);
                            log.error("Stream error", error);
                        },
                        () -> {
                            // 4. 流结束后更新记录
                            record.setAiResponse(aiResponse.toString());
                            record.setReasoningContent(aiReasoningContent.toString());
                            record.setUpdateTime(new Date());
                            baseMapper.updateById(record);
                            emitter.complete();
                        }
                );
            } catch (Exception e) {
                emitter.completeWithError(e);
                log.error("Stream processing failed", e);
            } finally {
                executor.shutdown();
            }
        });

        return emitter;
    }

    private String buildDeepseekRequest(List<ChatRecordEntity> list) {
        // 1. 构建消息列表
        List<Map<String, String>> messages = new ArrayList<>();

        // 添加系统提示（从配置读取）
        messages.add(Map.of("role", "system", "content", "你是一个专业的AI助手，需要专业的帮我解答一切问题")); // 需要根据实际配置修改

        // 添加历史对话记录（排除最后一个未完成的用户消息）
        for (ChatRecordEntity record : list) {
            if (record.getUserMessage() != null) {
                messages.add(Map.of("role", "user", "content", record.getUserMessage()));
            }
            if (record.getAiResponse() != null) {
                messages.add(Map.of("role", "assistant", "content", record.getAiResponse()));
            }
        }
        try {
            return mapper.writeValueAsString(Map.of(
                    // "model", "deepseek-reasoner",
                    "model", "deepseek-reasoner",
                    "max_tokens", 8192,
                    // "response_format", Map.of("type", "json_object"),
                    // "temperature", 1.0,
                    "stream", true,
                    "messages", messages
            ));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    // 根据实际API响应结构实现解析逻辑
    private String parseChunk(String chunk) {
        try {
            JsonNode node = new ObjectMapper().readTree(chunk);
            return node.path("choices").get(0).path("delta").path("content").asText();
        } catch (Exception e) {
            return "";
        }
    }

    private String parseReasoningContent(String chunk) {
        try {
            JsonNode node = new ObjectMapper().readTree(chunk);
            return node.path("choices").get(0).path("delta").path("reasoning_content").asText();
        } catch (Exception e) {
            return "";
        }
    }

}
