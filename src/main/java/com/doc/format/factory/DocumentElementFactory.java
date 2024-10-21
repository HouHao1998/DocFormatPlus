package com.doc.format.factory;

import com.doc.format.parser.DocumentElementParser;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/23 15:24
 */
public interface DocumentElementFactory {
    DocumentElementParser createParagraphParser();

    DocumentElementParser createTableParser();

    DocumentElementParser createImageParser();

}
