package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.doc.format.vo.UserDetailVo;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 用户实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Data
@TableName("user")
public class UserEntity extends UserDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 登录账号
     */
    @TableField("username")
    private String username;
    /**
     * 加密密码
     */
    @TableField("password")
    private String password;
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
     * 删除标记（0-正常 1-删除）
     */
    @TableField("deleted")
    private Integer deleted;
    @TableField(exist = false)
    private String token;
}
