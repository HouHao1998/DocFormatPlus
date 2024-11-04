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
 * 爱校对校验日志详情实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
@Data
@ApiModel
public class CheckLogDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ApiModelProperty(value = "主键ID")
    private Long id;

    /**
     * 文件id
     */
    @ApiModelProperty(value = "文件id")
    private Long fileId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    /**
     * 第几段
     */
    @ApiModelProperty(value = "第几段")
    private Integer position;
    /**
     * 总数
     */
    @ApiModelProperty(value = "总数")
    private Integer total;
}
