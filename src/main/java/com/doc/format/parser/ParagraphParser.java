package com.doc.format.parser;

import com.doc.format.util.entity.DocumentElement;
import com.doc.format.util.entity.ParagraphElement;
import com.doc.format.util.entity.TextElement;
import org.docx4j.TextUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.wml.*;

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
public class ParagraphParser implements DocumentElementParser {
    private String size = "";

    @Override
    public DocumentElement parse(Object element, WordprocessingMLPackage wordMLPackage) {
        if (element instanceof P) {
            P paragraph = (P) element;
            ParagraphElement paragraphElement = new ParagraphElement();

            // 提取段落对齐方式
            if (paragraph.getPPr() != null && paragraph.getPPr().getJc() != null) {
                paragraphElement.setAlign(paragraph.getPPr().getJc().getVal().value());
            } else {
                paragraphElement.setAlign("left");
            }

            // 检查是否有首行缩进
            if (paragraph.getPPr() != null && paragraph.getPPr().getInd() != null) {
                PPrBase.Ind ind = paragraph.getPPr().getInd();
                if (ind.getFirstLine() != null) {
                    paragraphElement.setFirstLineIndent(ind.getFirstLine().toString());
                } else if (ind.getHanging() != null) {
                    paragraphElement.setFirstLineIndent("hanging:" + ind.getHanging().toString());
                } else {
                    paragraphElement.setFirstLineIndent("none");
                }
            } else {
                paragraphElement.setFirstLineIndent("none");
            }

            // 提取段落内的文字元素
            List<TextElement> textElements = new ArrayList<>();
            for (Object runObj : paragraph.getContent()) {
                if (runObj instanceof R) {
                    setTextElement(textElements, (R) runObj);
                } else if (runObj instanceof RunDel) {
                    runDelToText(textElements, runObj);
                } else if (runObj instanceof RunIns) {
                    runInsToText(textElements, runObj);
                } else {
                    System.out.println("不是段落类型的元素，真实的元素类型是: " + runObj.getClass().getName());
                }
            }
            paragraphElement.setTextElements(textElements);

            return paragraphElement;
        }
        return null;
    }

    private void runDelToText(List<TextElement> textElements, Object runObj) {
        RunDel runDel = (RunDel) runObj;
        List<Object> customXmlOrSmartTagOrSdt = runDel.getCustomXmlOrSmartTagOrSdt();
        for (Object runD : customXmlOrSmartTagOrSdt) {
            if (runD instanceof R) {
                // setTextElement(textElements, (R) runD);
            } else {
                System.out.println("runD不是段落类型的元素，真实的元素类型是: " + runObj.getClass().getName());
            }
        }
    }

    private void runInsToText(List<TextElement> textElements, Object runObj) {
        List<Object> customXmlOrSmartTagOrSdt = ((RunIns) runObj).getCustomXmlOrSmartTagOrSdt();
        for (Object runO : customXmlOrSmartTagOrSdt) {
            if (runO instanceof R) {
                setTextElement(textElements, (R) runO);
            } else if (runO instanceof RunIns) {
                runInsToText(textElements, runO);
            } else if (runO instanceof RunDel) {
                runDelToText(textElements, runO);
            } else {
                System.out.println("runO不是段落类型的元素，真实的元素类型是: " + runO.getClass().getName());
            }
        }
    }

    private void setTextElement(List<TextElement> textElements, R runObj) {
        R run = runObj;
        TextElement textElement = new TextElement();
        textElement.setContent(TextUtils.getText(run));

        // 提取样式信息
        RPr rPr = run.getRPr();
        if (rPr != null) {
            textElement.setFont(getFontName(rPr));
            textElement.setFontSize(getFontSize(rPr));
            textElement.setBold(isBold(rPr));
            textElement.setItalic(isItalic(rPr));
        }

        textElements.add(textElement);
    }

    private String getFontName(RPr rPr) {
        if (rPr != null && rPr.getRFonts() != null) {
            return rPr.getRFonts().getAscii();
        }
        return "default";
    }

    private String getFontSize(RPr rPr) {
        if (rPr != null) {
            if (rPr.getSz() != null) {
                size = rPr.getSz().getVal().toString();
                return rPr.getSz().getVal().toString();
            } else if (rPr.getSzCs() != null) {
                size = rPr.getSzCs().getVal().toString();
                return rPr.getSzCs().getVal().toString();
            }
        }
        return size;
    }

    private boolean isBold(RPr rPr) {
        return rPr != null && rPr.getB() != null && rPr.getB().isVal();
    }

    private boolean isItalic(RPr rPr) {
        return rPr != null && rPr.getI() != null && rPr.getI().isVal();
    }
}