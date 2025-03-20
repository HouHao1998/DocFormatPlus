package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * AI对话会话实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-16 14:15:24
 */
@Data
@TableName("ai_session")
public class AiSessionEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 会话名称（用户可编辑）
     */
    @TableField("session_name")
    private String sessionName;
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
     * 删除标记
     */
    @TableField("deleted")
    private Integer deleted;
}
