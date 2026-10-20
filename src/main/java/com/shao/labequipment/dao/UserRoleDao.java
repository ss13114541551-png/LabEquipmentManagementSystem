package com.shao.labequipment.dao;

import com.shao.labequipment.entity.UserRoleInfo;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserRoleDao {

    public List<UserRoleInfo> findAllUserRoles() throws Exception {
        Session.requirePermission("ROLE_MANAGE");
        String sql = """
                SELECT u.id, u.username, u.real_name,
                       GROUP_CONCAT(r.role_name ORDER BY r.id SEPARATOR ', ') AS role_names
                FROM sys_user u
                LEFT JOIN sys_user_role ur ON u.id = ur.user_id
                LEFT JOIN sys_role r ON ur.role_id = r.id
                GROUP BY u.id, u.username, u.real_name
                ORDER BY u.id
                """;

        List<UserRoleInfo> users = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                users.add(new UserRoleInfo(
                        resultSet.getLong("id"),
                        resultSet.getString("username"),
                        resultSet.getString("real_name"),
                        resultSet.getString("role_names")
                ));
            }
        }

        return users;
    }

    public List<String> findRoleOptions() throws Exception {
        Session.requirePermission("ROLE_MANAGE");
        String sql = "SELECT id, role_name FROM sys_role WHERE status = 1 ORDER BY id";
        List<String> roles = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                roles.add(resultSet.getLong("id")
                        + " - " + resultSet.getString("role_name"));
            }
        }

        return roles;
    }

    public void replaceUserRole(long userId, long roleId) throws Exception {
        Session.requirePermission("ROLE_MANAGE");
        String deleteSql = "DELETE FROM sys_user_role WHERE user_id = ?";
        String insertSql = """
                INSERT INTO sys_user_role (user_id, role_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement deleteStatement =
                         connection.prepareStatement(deleteSql);
                 PreparedStatement insertStatement =
                         connection.prepareStatement(insertSql)) {

                deleteStatement.setLong(1, userId);
                deleteStatement.executeUpdate();

                insertStatement.setLong(1, userId);
                insertStatement.setLong(2, roleId);
                insertStatement.executeUpdate();

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }
}
