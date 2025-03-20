package com.doc.format.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;


/**
 * AI对话记录列表实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Data
@ApiModel
public class ChatRecordListVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ApiModelProperty(value = "记录ID")
    private Long id;

    /**
     * 所属会话ID
     */
    @ApiModelProperty(value = "所属会话ID")
    private Long sessionId;

    /**
     * 用户输入内容
     */
    @ApiModelProperty(value = "用户输入内容")
    private String userMessage;

    /**
     * AI回复内容
     */
    @ApiModelProperty(value = "AI回复内容")
    private String aiResponse;

    /**
     * DeepSeek消耗token
     */
    @ApiModelProperty(value = "DeepSeek消耗token")
    private Integer deepseekToken;

    /**
     * 爱校验消耗token
     */
    @ApiModelProperty(value = "爱校验消耗token")
    private Integer checkToken;

    /**
     * 创建者
     */
    @ApiModelProperty(value = "创建者")
    private String creator;

    /**
     * 更新者
     */
    @ApiModelProperty(value = "更新者")
    private String updater;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 删除标记
     */
    @ApiModelProperty(value = "删除标记")
    private Integer deleted;

}
