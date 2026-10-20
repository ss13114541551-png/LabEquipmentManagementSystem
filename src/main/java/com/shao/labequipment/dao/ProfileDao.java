package com.shao.labequipment.dao;

import com.shao.labequipment.entity.UserProfile;
import com.shao.labequipment.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import com.shao.labequipment.util.Session;

public class ProfileDao {

    public UserProfile findByUserId(long userId) throws Exception {
        String sql = """
                SELECT u.id, u.username, u.real_name, u.phone, u.email, u.status,
                       COALESCE((
                           SELECT GROUP_CONCAT(r.role_name ORDER BY r.id SEPARATOR '、')
                           FROM sys_user_role ur
                           JOIN sys_role r ON ur.role_id = r.id
                           WHERE ur.user_id = u.id
                       ), '未分配') AS role_names
                FROM sys_user u
                WHERE u.id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new UserProfile(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("real_name"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getInt("status"),
                        resultSet.getString("role_names")
                );
            }
        }
    }

    public boolean updateContact(long userId, String username,
                                 String phone, String email) throws Exception {
        if (Session.requireActiveUser().getId() != userId) {
            throw new SecurityException("只能修改当前账号的资料。");
        }
        String sql = """
            UPDATE sys_user
            SET username = ?,
                phone = ?,
                email = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
              AND status = 1
            """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            setNullableString(statement, 2, phone);
            setNullableString(statement, 3, email);
            statement.setLong(4, userId);

            return statement.executeUpdate() == 1;
        }
    }

    public boolean changePassword(long userId, String oldPassword, String newPassword) throws Exception {
        if (Session.requireActiveUser().getId() != userId) {
            throw new SecurityException("只能修改当前账号的密码。");
        }
        if (oldPassword == null || newPassword == null || oldPassword.isBlank()
                || newPassword.isBlank() || newPassword.length() < 6 || newPassword.length() > 128) {
            throw new IllegalArgumentException("原密码不能为空，新密码长度须为6～128位。");
        }
        if (oldPassword.equals(newPassword)) throw new IllegalArgumentException("新密码不能与原密码相同。");
        String sql = """
                UPDATE sys_user SET password = SHA2(?, 256), updated_at = CURRENT_TIMESTAMP
                WHERE id = ? AND status = 1 AND password = SHA2(?, 256)
                """;
        try (Connection c = DatabaseUtil.getConnection(); PreparedStatement s = c.prepareStatement(sql)) {
            s.setString(1, newPassword);
            s.setLong(2, userId);
            s.setString(3, oldPassword);
            return s.executeUpdate() == 1;
        }
    }

    private void setNullableString(PreparedStatement statement, int index,
                                   String value) throws Exception {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value.trim());
        }
    }
}
