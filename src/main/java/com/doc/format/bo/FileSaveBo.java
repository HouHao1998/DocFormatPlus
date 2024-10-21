package com.doc.format.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;


/**
 * 文件总览保存实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-21 20:00:06
 */
@Data
@ApiModel
public class FileSaveBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件类型，字典FILE_TYPE
     */
    @ApiModelProperty(value = "文件类型，字典FILE_TYPE")
    private String fileType;

    /**
     * 文件在linux中的完整目录，
     */
    @ApiModelProperty(value = "文件在linux中的完整目录，")
    private String filePath;

    /**
     * 文件互联网访问路径，
     */
    @ApiModelProperty(value = "文件互联网访问路径，")
    private String fileUrl;

    /**
     * 结果JSON，
     */
    @ApiModelProperty(value = "结果JSON，")
    private String resultJson;

    /**
     * 生成时间
     */
    @ApiModelProperty(value = "生成时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date addTime;

    /**
     * 删除状态
     */
    @ApiModelProperty(value = "删除状态")
    private Integer deleted;

    /**
     * html地址
     */
    @ApiModelProperty(value = "html地址")
    private String htmlPath;

    /**
     * 格式化后的结果文件
     */
    @ApiModelProperty(value = "格式化后的结果文件")
    private String resultDocPath;

    /**
     * 格式教研json
     */
    @ApiModelProperty(value = "格式教研json")
    private String validationResultJson;

    /**
     * Join生成的html
     */
    @ApiModelProperty(value = "Join生成的html")
    private String jsonHtmlPath;

    /**
     * 内容教研json
     */
    @ApiModelProperty(value = "内容教研json")
    private String contentVerificationJson;
    private String uuid;


}
