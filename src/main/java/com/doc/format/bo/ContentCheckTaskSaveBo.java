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
 * @date 2025-03-19 15:03:18
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

    private List<Map<String, String>> textList;



}
