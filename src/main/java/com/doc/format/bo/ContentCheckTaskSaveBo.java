package com.doc.format.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;


/**
 * 内容校验任务保存实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-25 16:32:34
 */
@Data
@ApiModel
public class ContentCheckTaskSaveBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 校验ID
     */
    @ApiModelProperty(value = "校验ID")
    private Long id;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long userId;

    /**
     * 原始文件路径
     */
    @ApiModelProperty(value = "原始文件路径")
    private String originalFile;

    /**
     * 解析出的文本内容
     */
    @ApiModelProperty(value = "解析出的文本内容")
    private String parsedText;

    /**
     * 校验结果（JSON格式）
     */
    @ApiModelProperty(value = "校验结果（JSON格式）")
    private String checkResult;

    /**
     * 格式json
     */
    @ApiModelProperty(value = "格式json")
    private String parsedJson;

    /**
     * 校验后文件路径
     */
    @ApiModelProperty(value = "校验后文件路径")
    private String checkedFile;

    /**
     * 爱校验消耗token
     */
    @ApiModelProperty(value = "爱校验消耗token")
    private Integer checkToken;

    /**
     * 关联的格式化任务ID
     */
    @ApiModelProperty(value = "关联的格式化任务ID")
    private Long formatTaskId;

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
    private String checkedUrl;

    private List<Map<String, String>> textList;
}
