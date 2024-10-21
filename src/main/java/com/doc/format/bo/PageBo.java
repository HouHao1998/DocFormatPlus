package com.doc.format.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author cw
 * @date 2021/9/29
 */
@Data
public class PageBo implements Serializable {

    private static final long serialVersionUID = 4931014535242549090L;

    @ApiModelProperty(value = "当前页，从1开始")
    private Long current;

    @ApiModelProperty(value = "页码")
    private Long size;
    /**
     * 字段排序名称
     */
    @ApiModelProperty(value = "字段排序名称")
    private String sortby;

    /**
     * 倒叙"orderby":"DESC"
     */
    @ApiModelProperty(value = "排序顺序,倒叙:DESC")
    private String orderby;

}
