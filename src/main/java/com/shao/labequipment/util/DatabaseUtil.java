package com.shao.labequipment.util;

import com.shao.labequipment.entity.LoginUser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

public final class DatabaseUtil {

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        Properties config = new Properties();

        try (InputStream input = DatabaseUtil.class
                .getResourceAsStream("/db.properties")) {

            if (input == null) {
                throw new SQLException(
                        "缺少数据库配置，请在 src/main/resources 中创建 db.properties"
                );
            }

            config.load(new InputStreamReader(
                    input, StandardCharsets.UTF_8
            ));
        } catch (java.io.IOException e) {
            throw new SQLException("读取数据库配置失败。", e);
        }

        Connection connection = DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.username"),
                config.getProperty("db.password")
        );

        // 向审计触发器传递当前系统用户
        try {
            LoginUser currentUser = Session.getCurrentUser();

            try (PreparedStatement statement = connection.prepareStatement(
                    "SET @app_user_id = ?"
            )) {
                if (currentUser == null) {
                    statement.setNull(1, Types.BIGINT);
                } else {
                    statement.setLong(1, currentUser.getId());
                }

                statement.execute();
            }

            return connection;
        } catch (SQLException e) {
            try {
                connection.close();
            } catch (SQLException closeError) {
                e.addSuppressed(closeError);
            }
            throw e;
        }
    }
}