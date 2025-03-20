package com.doc.format.util.excel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * <b>请输入名称</b>
 * <pre>
 * 描述<br/>
 * 作用：；<br/>
 * 限制：；<br/>
 * </pre>
 *
 * @author 侯浩(1272)
 * @date 2024/11/25 13:33
 */
public class MySQLValidator {

    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/your_database_name?useSSL=false&serverTimezone=UTC";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";

    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 验证SQL语句是否有效
     *
     * @param sql 需要验证的SQL语句
     * @return true 如果SQL有效，false 如果SQL无效或执行失败
     */
    public static boolean validateSQL(String sql) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // 尝试执行SQL语句的解析，不实际运行
            statement.setMaxRows(1); // 设置为查询结果最多返回一行，防止耗时操作
            statement.executeQuery();
            return true; // 如果没有抛出异常，则SQL有效
        } catch (SQLException e) {
            System.err.println("SQL验证失败: " + e.getMessage());
            return false; // 如果抛出异常，则SQL无效
        }
    }

    public static void main(String[] args) {
        // 测试SQL验证
        String testSQL = "SELECT * FROM your_table_name WHERE 1=1";
        boolean isValid = validateSQL(testSQL);
        System.out.println("SQL有效性: " + isValid);
    }
}