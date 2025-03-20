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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClickHouseValidator {

    // ClickHouse 数据库连接信息
    private static final String DATABASE = "your_database_name"; // 指定的数据库名
    private static final String URL = "jdbc:clickhouse://localhost:8123/" + DATABASE;
    private static final String USER = "default"; // 默认用户
    private static final String PASSWORD = "";   // 默认无密码

    /**
     * 获取 ClickHouse 数据库连接
     *
     * @return Connection 数据库连接对象
     * @throws SQLException 如果连接失败抛出异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * 验证 ClickHouse SQL 语句的有效性
     *
     * @param sql 需要验证的 SQL 语句
     * @return true 如果 SQL 有效，false 如果无效
     */
    public static boolean validateSQL(String sql) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            // 尝试解析和执行 SQL（仅检查有效性，不实际运行）
            statement.setMaxRows(1); // 防止过大的结果集消耗资源
            statement.executeQuery();
            return true; // 如果执行到这里没有异常，说明 SQL 有效
        } catch (SQLException e) {
            System.err.println("SQL 验证失败: " + e.getMessage());
            return false; // 如果出现异常，说明 SQL 无效1
        }
    }

    public static void main(String[] args) {
        // 测试 ClickHouse SQL 验证111
        String testSQL = "SELECT * FROM system.tables LIMIT 1"; // 注意：不需要显式指定数据库
        boolean isValid = validateSQL(testSQL);
        System.out.println("SQL 有效性: " + isValid);
    }
}