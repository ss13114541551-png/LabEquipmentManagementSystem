package com.shao.labequipment.dao;

import com.shao.labequipment.entity.Reservation;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;
import com.shao.labequipment.util.ReservationTimes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationDao {

    public List<Reservation> findAll() throws Exception {
        Session.requireActiveUser();
        String sql = """
                SELECT r.id, r.reservation_no, u.real_name,
                       l.lab_name, r.start_time, r.end_time,
                       r.purpose, r.status
                FROM reservation r
                JOIN sys_user u ON r.user_id = u.id
                JOIN laboratory l ON r.laboratory_id = l.id
                ORDER BY r.start_time DESC
                """;

        List<Reservation> reservations = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                reservations.add(new Reservation(
                        resultSet.getLong("id"),
                        resultSet.getString("reservation_no"),
                        resultSet.getString("real_name"),
                        resultSet.getString("lab_name"),
                        resultSet.getTimestamp("start_time").toLocalDateTime(),
                        resultSet.getTimestamp("end_time").toLocalDateTime(),
                        resultSet.getString("purpose"),
                        resultSet.getString("status")
                ));
            }
        }

        return reservations;
    }
    public void createReservation(long userId, long laboratoryId,
                                  LocalDateTime startTime,
                                  LocalDateTime endTime,
                                  String purpose) throws Exception {

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("结束时间必须晚于开始时间。");
        }

        if (Session.requirePermission("RESERVATION_CREATE").getId() != userId) {
            throw new SecurityException("只能以当前登录用户提交预约。");
        }
        if (purpose == null || purpose.isBlank() || purpose.length() > 255) {
            throw new IllegalArgumentException("请填写1～255字的使用用途。");
        }

        String lockLabSql = """
            SELECT id
            FROM laboratory
            WHERE id = ? AND status = 'AVAILABLE'
            FOR UPDATE
            """;

        String conflictSql = """
            SELECT COUNT(*) AS conflict_count
            FROM reservation
            WHERE laboratory_id = ?
              AND status IN ('PENDING', 'APPROVED')
              AND start_time < ?
              AND end_time > ?
            """;

        String insertSql = """
            INSERT INTO reservation
            (reservation_no, user_id, laboratory_id,
             start_time, end_time, purpose, status)
            VALUES (?, ?, ?, ?, ?, ?, 'PENDING')
            """;

        try (Connection connection = DatabaseUtil.getConnection()) {
            boolean originalAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try {
                // 1. 锁定实验室所在行，串行处理同一实验室的预约请求
                try (PreparedStatement lockStatement =
                             connection.prepareStatement(lockLabSql)) {

                    lockStatement.setLong(1, laboratoryId);

                    try (ResultSet resultSet = lockStatement.executeQuery()) {
                        if (!resultSet.next()) {
                            throw new IllegalStateException(
                                    "实验室不存在或当前不可预约。"
                            );
                        }
                    }
                }

                ReservationTimes.validate(startTime, endTime, ReservationTimes.databaseNow(connection));

                // 2. 检查时间冲突：已有开始 < 新结束 且 已有结束 > 新开始
                try (PreparedStatement conflictStatement =
                             connection.prepareStatement(conflictSql)) {

                    conflictStatement.setLong(1, laboratoryId);
                    conflictStatement.setTimestamp(2, Timestamp.valueOf(endTime));
                    conflictStatement.setTimestamp(3, Timestamp.valueOf(startTime));

                    try (ResultSet resultSet = conflictStatement.executeQuery()) {
                        resultSet.next();

                        if (resultSet.getInt("conflict_count") > 0) {
                            throw new IllegalStateException(
                                    "该实验室在所选时间段已被预约。"
                            );
                        }
                    }
                }

                // 3. 无冲突才写入预约记录
                String reservationNo = "RES"
                        + DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")
                        .format(LocalDateTime.now());

                try (PreparedStatement insertStatement =
                             connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, reservationNo);
                    insertStatement.setLong(2, userId);
                    insertStatement.setLong(3, laboratoryId);
                    insertStatement.setTimestamp(4, Timestamp.valueOf(startTime));
                    insertStatement.setTimestamp(5, Timestamp.valueOf(endTime));
                    insertStatement.setString(6, purpose);

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
    public List<String> findAvailableLabOptions() throws Exception {
        String sql = """
            SELECT id, lab_name
            FROM laboratory
            WHERE status = 'AVAILABLE'
            ORDER BY id
            """;

        List<String> options = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                options.add(
                        resultSet.getLong("id")
                                + " - "
                                + resultSet.getString("lab_name")
                );
            }
        }

        return options;
    }
    public void auditReservation(long reservationId, long auditorId,
                                 String newStatus) throws Exception {

        if (Session.requirePermission("RESERVATION_AUDIT").getId() != auditorId) {
            throw new SecurityException("审核人必须是当前登录用户。");
        }
        if (!"APPROVED".equals(newStatus) && !"REJECTED".equals(newStatus)) {
            throw new IllegalArgumentException("审核结果只能为通过或拒绝。");
        }
        String sql = """
            UPDATE reservation
            SET status = ?,
                audit_user_id = ?,
                audit_time = NOW()
            WHERE id = ?
              AND status = 'PENDING'
            """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, newStatus);
            statement.setLong(2, auditorId);
            statement.setLong(3, reservationId);

            int affectedRows = statement.executeUpdate();

            if (affectedRows == 0) {
                throw new IllegalStateException("该预约不存在或已经审核。");
            }
        }
    }
}
