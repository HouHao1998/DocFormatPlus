package com.doc.format.util.entity;

import lombok.Data;

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
 * @date 2024/12/2 14:45
 */
@Data
public class SimplePara {
    /**
     * 词列表 (words)
     */
    private List<SimpleWord> ws;

    /**
     * 内容 (content)
     */
    private String c;

    /**
     * 章节索引 (sectionIndex)
     */
    private int si;

    /**
     * 段落索引 (paraIndex)
     */
    private int pi;
    /**
     * 类型 (type)
     */
    private int t;

    public static List<SimplePara> getSimpleParaList(List<DocumentElement> documentElements) {
        List<SimplePara> simpleParas = new ArrayList<>();
        for (DocumentElement documentElement : documentElements) {

            if (documentElement instanceof ParagraphElement) {
                ParagraphElement paragraphElement = (ParagraphElement) documentElement;
                SimplePara simplePara = new SimplePara();
                simplePara.setC(paragraphElement.getContent());
                simplePara.setPi(paragraphElement.getParagraphIndex());
                simplePara.setSi(paragraphElement.getSectionIndex());
                List<SimpleWord> simpleWords = new ArrayList<>();
                for (TextElement textElement : paragraphElement.getTextElements()) {
                    SimpleWord simpleWord = new SimpleWord();
                    simpleWord.setC(textElement.getContent());
                    simpleWord.setWi(textElement.getChildObjectsIndex());
                    simpleWords.add(simpleWord);
                }
                simplePara.setWs(simpleWords);
                simpleParas.add(simplePara);
            }
        }
        return simpleParas;
    }

    public static List<SimplePara> getPageList(List<DocumentElement> documentElements) {
        List<SimplePara> simpleParas = new ArrayList<>();
        for (DocumentElement documentElement : documentElements) {

            if (documentElement instanceof ParagraphElement) {
                ParagraphElement paragraphElement = (ParagraphElement) documentElement;
                SimplePara simplePara = new SimplePara();
                simplePara.setC(paragraphElement.getContent());
                simplePara.setPi(paragraphElement.getParagraphIndex());
                simplePara.setSi(paragraphElement.getSectionIndex());
                simpleParas.add(simplePara);
            }
        }
        return simpleParas;
    }

}
