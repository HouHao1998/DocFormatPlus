package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 爱校对校验日志实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-30 20:57:05
 */
@Data
@TableName("check_log")
public class CheckLogEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件id
     */
    @TableField("file_id")
    private Long fileId;
    /**
     * 数量
     */
    @TableField("quantity")
    private Integer quantity;
    /**
     * 第几段
     */
    @TableField("position")
    private Integer position;
    /**
     * 总数
     */
    @TableField("total")
    private Integer total;
}
