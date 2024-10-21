package com.doc.format.util.entity;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/20 09:37
 */

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

/**
 * 图片元素
 */
@Data
@JSONType(typeName = "Image")
public class ImageElement extends DocumentElement {
    /**
     * 图片的Base64编码内容
     */
    private String base64Content;
    private String localPath;
    private String alignment;
    private String margin;
    private String size;
    private Long width;
    private Long height;
}
