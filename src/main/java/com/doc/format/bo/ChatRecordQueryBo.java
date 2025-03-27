package com.doc.format.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Date;

import java.io.Serializable;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;


/**
 * AI对话记录查询实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-25 10:13:32
 */
@Data
@ApiModel
public class ChatRecordQueryBo extends PageBo implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 批量查询主键id
     */
    private List<Long> ids;
    /**
     * 模糊查询关键字
     */
    @ApiModelProperty(value = "关键字")
    private String keyword;
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

    /**
     * 推理内容
     */
    @ApiModelProperty(value = "推理内容")
    private String reasoningContent;

    /**
     * 模型类型
     */
    @ApiModelProperty(value = "模型类型")
    private String modelType;

    /**
     * 消息状态
     */
    @ApiModelProperty(value = "消息状态")
    private String responseState;

}
