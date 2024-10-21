package com.doc.format.parser;

import com.doc.format.util.entity.DocumentElement;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

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
public interface DocumentElementParser {
    DocumentElement parse(Object element, WordprocessingMLPackage wordMLPackage) throws Exception;
}
