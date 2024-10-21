package com.doc.format.parser;

import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ElementType;
import com.doc.format.util.entity.TableElement;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.Tbl;
import org.docx4j.wml.Tc;
import org.docx4j.wml.Tr;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/23 15:31
 */
public class TableParser implements DocumentElementParser {

    @Override
    public DocumentElement parse(Object element, WordprocessingMLPackage wordMLPackage) {
        if (element instanceof Tbl) {
            Tbl table = (Tbl) element;
            TableElement tableElement = new TableElement();
            tableElement.setType(ElementType.TABLE);
            tableElement.setTableContent(extractTableContent(table));
            return tableElement;
        }
        return null;
    }

    private List<List<String>> extractTableContent(Tbl table) {
        List<List<String>> tableContent = new ArrayList<>();
        List<Object> rows = table.getContent();

        for (Object rowObj : rows) {
            if (rowObj instanceof Tr) {
                Tr row = (Tr) rowObj;
                List<String> rowContent = new ArrayList<>();
                List<Object> cells = row.getContent();

                for (Object cellObj : cells) {
                    if (cellObj instanceof Tc) {
                        Tc cell = (Tc) cellObj;
                        String cellText = TextUtils.getText(cell);
                        rowContent.add(cellText);
                    }
                }
                tableContent.add(rowContent);
            }
        }
        return tableContent;
    }
}
