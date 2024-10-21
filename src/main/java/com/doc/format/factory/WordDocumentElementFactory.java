package com.doc.format.factory;

import com.doc.format.parser.DocumentElementParser;
import com.doc.format.parser.ImageParser;
import com.doc.format.parser.ParagraphParser;
import com.doc.format.parser.TableParser;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/23 15:25
 */
public class WordDocumentElementFactory implements DocumentElementFactory {

    @Override
    public DocumentElementParser createParagraphParser() {
        return new ParagraphParser();
    }

    @Override
    public DocumentElementParser createTableParser() {
        return new TableParser();
    }

    @Override
    public DocumentElementParser createImageParser() {
        return new ImageParser();
    }

}
