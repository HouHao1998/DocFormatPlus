package com.doc.format.util.excel;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/10/11 11:10
 */
public class DataSourceSQL {
    private String dataSourceName;
    private String sqlExpression;
    private String optionalField;

    public DataSourceSQL(String dataSourceName, String sqlExpression, String optionalField) {
        this.dataSourceName = dataSourceName;
        this.sqlExpression = sqlExpression;
        this.optionalField = optionalField;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public String getSqlExpression() {
        return sqlExpression;
    }

    public void setSqlExpression(String sqlExpression) {
        this.sqlExpression = sqlExpression;
    }

    public String getOptionalField() {
        return optionalField;
    }

    public void setOptionalField(String optionalField) {
        this.optionalField = optionalField;
    }

    @Override
    public String toString() {
        return "DataSourceSQL{" +
                "dataSourceName='" + dataSourceName + '\'' +
                ", sqlExpression='" + sqlExpression + '\'' +
                ", optionalField='" + optionalField + '\'' +
                '}';
    }
}
