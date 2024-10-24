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

import lombok.Data;

import java.util.List;

/**
 * 教研是否符合规范的结果
 */
@Data
public class ValidationResult {
    /**
     * 文档元素名称，如标题、作者姓名等
     */
    private String type;
    /**
     * 文档元素类型名称，如标题、作者姓名等的类型名称
     */
    private String typeName;

    /**
     * 是否检测到内容
     */
    private boolean isContentDetected;
    /**
     * 处于第几节
     */
    private int sectionIndex;

    /**
     * 处于第几段落
     */

    private int paragraphIndex;
    /**
     * 文本信息列表
     */
    private List<TextElement> textElements;


    public ValidationResult(String type, String typeName, boolean isContentDetected, int elementIndex,int sectionIndex, List<TextElement> textElements) {
        this.type = type;
        this.typeName = typeName;
        this.isContentDetected = isContentDetected;
        this.paragraphIndex = elementIndex;
        this.sectionIndex = sectionIndex;
        this.textElements = textElements;

    }
}

