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
 * AI对话会话查询实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Data
@ApiModel
public class AiSessionQueryBo extends PageBo implements Serializable {
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
     * 会话ID
     */
    @ApiModelProperty(value = "会话ID")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 会话名称（用户可编辑）
     */
    @ApiModelProperty(value = "会话名称（用户可编辑）")
    private String sessionName;

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
