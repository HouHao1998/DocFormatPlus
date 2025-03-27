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
 * 文档格式化任务查询实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-25 16:32:34
 */
@Data
@ApiModel
public class FormatTaskQueryBo extends PageBo implements Serializable {
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
     * 任务ID
     */
    @ApiModelProperty(value = "任务ID")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 原始文件存储路径
     */
    @ApiModelProperty(value = "原始文件存储路径")
    private String originalFile;

    /**
     * 解析JSON文件路径
     */
    @ApiModelProperty(value = "解析JSON文件路径")
    private String parsedJson;

    /**
     * 格式化文件路径
     */
    @ApiModelProperty(value = "格式化文件路径")
    private String formattedFile;

    /**
     * DeepSeek消耗token
     */
    @ApiModelProperty(value = "DeepSeek消耗token")
    private Integer deepseekToken;

    /**
     * 任务状态
     */
    @ApiModelProperty(value = "任务状态")
    private String taskStatus;

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
     * 原始文件网址
     */
    @ApiModelProperty(value = "原始文件网址")
    private String originalUrl;

    /**
     * 校验后文件网址
     */
    @ApiModelProperty(value = "校验后文件网址")
    private String formattedUrl;

}
