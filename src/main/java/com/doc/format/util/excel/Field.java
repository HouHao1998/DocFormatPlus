package com.doc.format.util.excel;

import lombok.Data;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/9/13 15:23
 */
@Data
public class Field {

    private String fieldName;       // 字段名称（英文占位符）
    private String tableName;       // 表头名称（中文表头）
    private String attributeName;   // 属性名
    private String fieldType = "String";       // 字段类型
    private String width;           // 宽度
    private String type;            // 类型
    private Boolean taskDispatch;   // 是否任务派发
    private Boolean auditField;     // 是否是审计信息字段
    private Boolean exportable;     // 是否可导出
    private Boolean dataGenerationField; // 是否为数据生成字段
    private String displayMode;     // 显示方式
    private String drillOptionalParam; // 钻取选填参数配置
    private String drillType;       // 钻取类型
    private Boolean supportDrill;//是否支持钻取;
    private Boolean supportComplete;//支持补全;
    private String completeType;//补全类型;
    private String completeConfig;//补全配置


    @Override
    public String toString() {
        return "Field{" +
                "fieldName='" + fieldName + '\'' +
                ", tableName='" + tableName + '\'' +
                ", attributeName='" + attributeName + '\'' +
                ", fieldType='" + fieldType + '\'' +
                ", width='" + width + '\'' +
                ", type='" + type + '\'' +
                ", taskDispatch=" + taskDispatch +
                ", auditField=" + auditField +
                ", exportable=" + exportable +
                ", dataGenerationField=" + dataGenerationField +
                '}';
    }
}
