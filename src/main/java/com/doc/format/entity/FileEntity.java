package com.doc.format.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;


/**
 * 文件总览实体
 *
 * @author HouHao
 * @version 1.0
 * @date 2024-10-29 21:35:49
 */
@Data
@TableName("file")
public class FileEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    @TableField("file_name")
    private String fileName;
    /**
     * 文件类型，字典FILE_TYPE
     */
    @TableField("file_type")
    private String fileType;
    /**
     * 文件在linux中的完整目录，
     */
    @TableField("file_path")
    private String filePath;
    /**
     * 文件互联网访问路径，
     */
    @TableField("file_url")
    private String fileUrl;
    /**
     * 结果JSON，
     */
    @TableField("result_json")
    private String resultJson;
    /**
     * 生成时间
     */
    @TableField("add_time")
    private Date addTime;
    /**
     * 删除状态
     */
    @TableField("deleted")
    private Integer deleted;
    /**
     * html地址
     */
    @TableField("html_path")
    private String htmlPath;
    /**
     * 格式化后的结果文件
     */
    @TableField("result_doc_path")
    private String resultDocPath;
    /**
     * 格式教研json
     */
    @TableField("validation_result_json")
    private String validationResultJson;
    /**
     * Join生成的html
     */
    @TableField("json_html_path")
    private String jsonHtmlPath;
    /**
     * 内容教研json
     */
    @TableField("content_verification_json")
    private String contentVerificationJson;
    /**
     * 内容教研结果html
     */
    @TableField("content_verification_html")
    private String contentVerificationHtml;
}
