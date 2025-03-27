package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 文档格式化任务实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-25 16:32:34
 */
@Data
@TableName("format_task")
public class FormatTaskEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 原始文件存储路径
     */
    @TableField("original_file")
    private String originalFile;
    /**
     * 解析JSON文件路径
     */
    @TableField("parsed_json")
    private String parsedJson;
    /**
     * 格式化文件路径
     */
    @TableField("formatted_file")
    private String formattedFile;
    /**
     * DeepSeek消耗token
     */
    @TableField("deepseek_token")
    private Integer deepseekToken;
    /**
     * 任务状态
     */
    @TableField("task_status")
    private String taskStatus;
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
    /**
     * 原始文件网址
     */
    @TableField("original_url")
    private String originalUrl;
    /**
     * 校验后文件网址
     */
    @TableField("formatted_url")
    private String formattedUrl;
}
