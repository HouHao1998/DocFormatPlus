package com.doc.format.util.deep;

import com.doc.format.enums.DocElementTypeEnum;
import lombok.Data;

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
 * @date 2025/3/20 15:06
 */
@Data
public class Detail {
    DocElementTypeEnum type;
    String content;

    Detail(DocElementTypeEnum type, String content) {
        this.type = type;
        this.content = content;
    }
}
