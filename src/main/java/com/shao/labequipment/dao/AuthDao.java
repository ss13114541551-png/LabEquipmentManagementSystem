package com.shao.labequipment.dao;

import com.shao.labequipment.entity.LoginUser;
import com.shao.labequipment.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class AuthDao {

    public LoginUser login(String username, String password) throws Exception {
        String sql = userQuery() + """
                WHERE u.username = ?
                  AND u.password = SHA2(?, 256)
                  AND u.status = 1
                ORDER BY r.id, p.id
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                return readUser(resultSet);
            }
        }
    }

    public boolean changePassword(String username, String oldPassword,
                                  String newPassword) throws Exception {
        String sql = """
                UPDATE sys_user
                SET password = SHA2(?, 256),
                    updated_at = CURRENT_TIMESTAMP
                WHERE username = ?
                  AND password = SHA2(?, 256)
                  AND status = 1
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newPassword);
            statement.setString(2, username);
            statement.setString(3, oldPassword);

            return statement.executeUpdate() == 1;
        }
    }

    public LoginUser findById(long userId) throws Exception {
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     userQuery() + " WHERE u.id = ? AND u.status = 1 ORDER BY r.id, p.id")) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return readUser(resultSet);
            }
        }
    }

    private String userQuery() {
        return """
                SELECT u.id, u.username, u.real_name, r.role_name, p.permission_code
                FROM sys_user u
                LEFT JOIN sys_user_role ur ON ur.user_id = u.id
                LEFT JOIN sys_role r ON r.id = ur.role_id AND r.status = 1
                LEFT JOIN sys_role_permission rp ON rp.role_id = r.id
                LEFT JOIN sys_permission p ON p.id = rp.permission_id
                """;
    }

    private LoginUser readUser(ResultSet rows) throws Exception {
        if (!rows.next()) return null;
        long id = rows.getLong("id");
        String username = rows.getString("username");
        String realName = rows.getString("real_name");
        Set<String> roles = new LinkedHashSet<>();
        Set<String> permissions = new LinkedHashSet<>();
        do {
            if (rows.getString("role_name") != null) roles.add(rows.getString("role_name"));
            if (rows.getString("permission_code") != null) permissions.add(rows.getString("permission_code"));
        } while (rows.next());
        return new LoginUser(id, username, realName,
                roles.isEmpty() ? "未分配角色" : String.join("、", roles), permissions);
    }
}
