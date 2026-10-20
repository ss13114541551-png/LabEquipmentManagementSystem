package com.shao.labequipment.dao;

import com.shao.labequipment.entity.MaintenanceRecord;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRecordDao {

    public List<MaintenanceRecord> findAll() throws Exception {
        Session.requirePermission("MAINTENANCE_MANAGE");
        String sql = """
                SELECT m.id, m.maintenance_no, d.device_no, d.device_name,
                       u.real_name AS reporter_name, m.report_time,
                       m.maintenance_start_time, m.maintenance_end_time,
                       m.fault_description, m.cost, m.status, m.remark
                FROM maintenance_record m
                JOIN device d ON m.device_id = d.id
                JOIN sys_user u ON m.reporter_user_id = u.id
                ORDER BY FIELD(m.status, 'PENDING', 'PROCESSING', 'COMPLETED'),
                         m.report_time DESC
                """;

        List<MaintenanceRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                records.add(new MaintenanceRecord(
                        resultSet.getLong("id"),
                        resultSet.getString("maintenance_no"),
                        resultSet.getString("device_no"),
                        resultSet.getString("device_name"),
                        resultSet.getString("reporter_name"),
                        resultSet.getTimestamp("report_time").toLocalDateTime(),
                        resultSet.getTimestamp("maintenance_start_time") == null
                                ? null : resultSet.getTimestamp("maintenance_start_time").toLocalDateTime(),
                        resultSet.getTimestamp("maintenance_end_time") == null
                                ? null : resultSet.getTimestamp("maintenance_end_time").toLocalDateTime(),
                        resultSet.getString("fault_description"),
                        resultSet.getBigDecimal("cost"),
                        resultSet.getString("status"),
                        resultSet.getString("remark")
                ));
            }
        }

        return records;
    }

    public void startProcessing(long maintenanceId) throws Exception {
        Session.requirePermission("MAINTENANCE_MANAGE");
        String sql = """
                UPDATE maintenance_record
                SET status = 'PROCESSING',
                    maintenance_start_time = NOW()
                WHERE id = ?
                  AND status = 'PENDING'
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, maintenanceId);
            if (statement.executeUpdate() == 0) {
                throw new IllegalStateException("该报修记录不存在或已经开始处理。");
            }
        }
    }

    public void completeMaintenance(long maintenanceId, BigDecimal cost,
                                    String result) throws Exception {
        Session.requirePermission("MAINTENANCE_MANAGE");
        String lockSql = """
                SELECT device_id
                FROM maintenance_record
                WHERE id = ?
                  AND status IN ('PENDING', 'PROCESSING')
                FOR UPDATE
                """;
        String completeSql = """
                UPDATE maintenance_record
                SET status = 'COMPLETED',
                    maintenance_start_time = COALESCE(maintenance_start_time, NOW()),
                    maintenance_end_time = NOW(),
                    cost = ?,
                    remark = ?
                WHERE id = ?
                """;
        String deviceSql = """
                UPDATE device d
                SET d.status = 'AVAILABLE'
                WHERE d.id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM maintenance_record m
                      WHERE m.device_id = d.id
                        AND m.status IN ('PENDING', 'PROCESSING')
                  )
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            connection.setAutoCommit(false);
            try {
                long deviceId;
                try (PreparedStatement statement = connection.prepareStatement(lockSql)) {
                    statement.setLong(1, maintenanceId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new IllegalStateException("该记录不存在或维修已经完成。");
                        }
                        deviceId = resultSet.getLong("device_id");
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(completeSql)) {
                    statement.setBigDecimal(1, cost);
                    statement.setString(2, result);
                    statement.setLong(3, maintenanceId);
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(deviceSql)) {
                    statement.setLong(1, deviceId);
                    statement.executeUpdate();
                }

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
