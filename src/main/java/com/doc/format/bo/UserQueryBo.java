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
 * 用户查询实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Data
@ApiModel
public class UserQueryBo extends PageBo implements Serializable {
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
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private Long id;

    /**
     * 登录账号
     */
    @ApiModelProperty(value = "登录账号")
    private String username;

    /**
     * 加密密码
     */
    @ApiModelProperty(value = "加密密码")
    private String password;

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
     * 删除标记（0-正常 1-删除）
     */
    @ApiModelProperty(value = "删除标记（0-正常 1-删除）")
    private Integer deleted;

}
