package com.shao.labequipment.dao;

import com.shao.labequipment.entity.BorrowRecord;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDao {

    public List<BorrowRecord> findAll() throws Exception {
        Session.requirePermission("BORROW_MANAGE");
        String sql = baseQuery() + """
                ORDER BY overdue DESC, due_soon DESC, b.borrow_time DESC
                """;

        return queryRecords(sql, null);
    }

    public List<BorrowRecord> findByUserId(long userId) throws Exception {
        var actor = Session.requireActiveUser();
        if (actor.getId() != userId && !actor.hasPermission("BORROW_MANAGE")) {
            throw new SecurityException("只能查看自己的借用记录。");
        }
        String sql = baseQuery() + """
                WHERE b.user_id = ?
                ORDER BY overdue DESC, due_soon DESC, b.borrow_time DESC
                """;

        return queryRecords(sql, userId);
    }

    private String baseQuery() {
        return """
                SELECT b.id, b.borrow_no, u.real_name, d.device_name,
                       b.borrow_time, b.expected_return_time, b.status,
                       CASE
                           WHEN b.status = 'BORROWED'
                            AND b.expected_return_time < NOW()
                           THEN 1 ELSE 0
                       END AS overdue,
                       CASE
                           WHEN b.status = 'BORROWED'
                            AND b.expected_return_time >= NOW()
                            AND b.expected_return_time <= DATE_ADD(NOW(), INTERVAL 24 HOUR)
                           THEN 1 ELSE 0
                       END AS due_soon
                FROM borrow_record b
                JOIN sys_user u ON b.user_id = u.id
                JOIN device d ON b.device_id = d.id
                """;
    }

    private List<BorrowRecord> queryRecords(String sql, Long userId) throws Exception {

        List<BorrowRecord> records = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (userId != null) {
                statement.setLong(1, userId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(new BorrowRecord(
                            resultSet.getLong("id"),
                            resultSet.getString("borrow_no"),
                            resultSet.getString("real_name"),
                            resultSet.getString("device_name"),
                            resultSet.getTimestamp("borrow_time").toLocalDateTime(),
                            resultSet.getTimestamp("expected_return_time").toLocalDateTime(),
                            resultSet.getString("status"),
                            resultSet.getBoolean("overdue"),
                            resultSet.getBoolean("due_soon")
                    ));
                }
            }
        }

        return records;
    }

    public List<String> findAvailableDeviceOptions() throws Exception {
        String sql = """
                SELECT id, device_no, device_name
                FROM device
                WHERE status = 'AVAILABLE'
                ORDER BY id
                """;

        List<String> options = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                options.add(
                        resultSet.getLong("id") + " - "
                                + resultSet.getString("device_no") + " - "
                                + resultSet.getString("device_name")
                );
            }
        }

        return options;
    }

    public void borrowDevice(long userId, long deviceId,
                             LocalDateTime expectedReturnTime) throws Exception {

        if (Session.requireActiveUser().getId() != userId) {
            throw new SecurityException("只能以当前登录用户借用设备。");
        }

        if (!expectedReturnTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("预计归还时间必须晚于当前时间。");
        }

        String lockSql = """
                SELECT status
                FROM device
                WHERE id = ?
                FOR UPDATE
                """;

        String insertSql = """
                INSERT INTO borrow_record
                (borrow_no, user_id, device_id,
                 borrow_time, expected_return_time, status)
                VALUES (?, ?, ?, NOW(), ?, 'BORROWED')
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                try (PreparedStatement lockStatement =
                             connection.prepareStatement(lockSql)) {

                    lockStatement.setLong(1, deviceId);

                    try (ResultSet resultSet = lockStatement.executeQuery()) {
                        if (!resultSet.next()
                                || !"AVAILABLE".equals(resultSet.getString("status"))) {
                            throw new IllegalStateException("该设备当前不可借用。");
                        }
                    }
                }

                String borrowNo = "BOR"
                        + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                        .format(LocalDateTime.now());

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, borrowNo);
                    insertStatement.setLong(2, userId);
                    insertStatement.setLong(3, deviceId);
                    insertStatement.setTimestamp(
                            4, Timestamp.valueOf(expectedReturnTime)
                    );

                    insertStatement.executeUpdate();
                }

                connection.commit();

            } catch (Exception e) {
                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }

    public void returnDevice(long borrowRecordId, long reporterUserId,
                             boolean needRepair,
                             String faultDescription) throws Exception {
        var actor = Session.requireActiveUser();
        if (actor.getId() != reporterUserId) throw new SecurityException("操作人必须为当前登录用户。");
        if (needRepair && (faultDescription == null || faultDescription.isBlank())) {
            throw new IllegalArgumentException("报修时必须填写故障说明。");
        }

        String lockSql = """
                SELECT device_id, user_id
                FROM borrow_record
                WHERE id = ?
                  AND status = 'BORROWED'
                FOR UPDATE
                """;
        String returnSql = """
                UPDATE borrow_record
                SET status = 'RETURNED',
                    actual_return_time = NOW()
                WHERE id = ?
                  AND status = 'BORROWED'
                """;
        String repairSql = """
                INSERT INTO maintenance_record
                (maintenance_no, device_id, reporter_user_id,
                 report_time, fault_description, status)
                VALUES (?, ?, ?, NOW(), ?, 'PENDING')
                """;
        String deviceSql = """
                UPDATE device
                SET status = 'MAINTENANCE'
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                long deviceId;
                try (PreparedStatement statement = connection.prepareStatement(lockSql)) {
                    statement.setLong(1, borrowRecordId);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new IllegalStateException("该记录不存在或设备已归还。");
                        }
                        deviceId = resultSet.getLong("device_id");
                        if (!actor.hasPermission("BORROW_MANAGE")
                                && resultSet.getLong("user_id") != actor.getId()) {
                            throw new SecurityException("只能归还自己借用的设备。");
                        }
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement(returnSql)) {
                    statement.setLong(1, borrowRecordId);
                    statement.executeUpdate();
                }

                if (needRepair) {
                    String maintenanceNo = "MNT"
                            + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                            .format(LocalDateTime.now());

                    try (PreparedStatement statement = connection.prepareStatement(repairSql)) {
                        statement.setString(1, maintenanceNo);
                        statement.setLong(2, deviceId);
                        statement.setLong(3, reporterUserId);
                        statement.setString(4, faultDescription.trim());
                        statement.executeUpdate();
                    }

                    try (PreparedStatement statement = connection.prepareStatement(deviceSql)) {
                        statement.setLong(1, deviceId);
                        statement.executeUpdate();
                    }
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(originalAutoCommit);
            }
        }
    }
}
