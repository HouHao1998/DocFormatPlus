package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * AI对话记录实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-25 10:13:32
 */
@Data
@TableName("chat_record")
public class ChatRecordEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    @TableField("session_id")
    private Long sessionId;
    /**
     * 用户输入内容
     */
    @TableField("user_message")
    private String userMessage;
    /**
     * AI回复内容
     */
    @TableField("ai_response")
    private String aiResponse;
    /**
     * DeepSeek消耗token
     */
    @TableField("deepseek_token")
    private Integer deepseekToken;
    /**
     * 爱校验消耗token
     */
    @TableField("check_token")
    private Integer checkToken;
    /**
     * 创建者
     */
    @TableField("creator")
    private String creator;
    /**
     * 更新者
     */
    @TableField("updater")
    private String updater;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除标记
     */
    @TableField("deleted")
    private Integer deleted;
    /**
     * 推理内容
     */
    @TableField("reasoning_content")
    private String reasoningContent;
    /**
     * 模型类型
     */
    @TableField("model_type")
    private String modelType;
    /**
     * 消息状态
     */
    @TableField("response_state")
    private String responseState;
}
