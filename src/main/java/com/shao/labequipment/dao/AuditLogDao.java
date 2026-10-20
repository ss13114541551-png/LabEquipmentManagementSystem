package com.shao.labequipment.dao;

import com.shao.labequipment.entity.AuditLog;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDao {

    public List<AuditLog> findLatestLogs() throws Exception {
        Session.requireManager();
        String sql = """
                SELECT l.id, u.real_name, l.module_name,
                       l.action_name, l.target_type,
                       l.detail, l.operation_time
                FROM operation_log l
                LEFT JOIN sys_user u ON l.user_id = u.id
                ORDER BY l.operation_time DESC, l.id DESC
                LIMIT 100
                """;

        List<AuditLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                logs.add(new AuditLog(
                        resultSet.getLong("id"),
                        resultSet.getString("real_name"),
                        resultSet.getString("module_name"),
                        resultSet.getString("action_name"),
                        resultSet.getString("target_type"),
                        resultSet.getString("detail"),
                        resultSet.getTimestamp("operation_time").toLocalDateTime()
                ));
            }
        }

        return logs;
    }
}
