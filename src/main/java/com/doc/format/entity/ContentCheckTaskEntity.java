package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 内容校验任务实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2025-03-19 15:03:18
 */
@Data
@TableName("content_check_task")
public class ContentCheckTaskEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 校验ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    /**
     * 原始文件路径
     */
    @TableField("original_file")
    private String originalFile;
    /**
     * 解析出的文本内容
     */
    @TableField("parsed_text")
    private String parsedText;
    /**
     * 校验结果（JSON格式）
     */
    @TableField("check_result")
    private String checkResult;
    /**
     * 格式json
     */
    @TableField("parsed_json")
    private String parsedJson;
    /**
     * 校验后文件路径
     */
    @TableField("checked_file")
    private String checkedFile;
    /**
     * 爱校验消耗token
     */
    @TableField("check_token")
    private Integer checkToken;
    /**
     * 关联的格式化任务ID
     */
    @TableField("format_task_id")
    private Long formatTaskId;
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
}
