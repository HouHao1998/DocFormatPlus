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
 * @date 2024/9/20 09:35
 */

import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

/**
 * 文档元素的基类，包含通用属性
 */
@Data
@JSONType(seeAlso = {ParagraphElement.class, TableElement.class, TextElement.class, ImageElement.class}, typeKey = "type")
public abstract class DocumentElement {
    /**
     * 元素类型，如文本、图片、表格等
     */
    private ElementType type;

    /**
     * 元素在文档中的起始索引
     */
    private int startIndex;
    /**
     * 处于第几节
     */
    private int sectionIndex;

    /**
     * 处于第几段落
     */
    private int paragraphIndex;
    /**
     * 处于第几行
     */
    private int childObjectsIndex;

    /**
     * 元素在文档中的结束索引
     */
    private int endIndex;
}

