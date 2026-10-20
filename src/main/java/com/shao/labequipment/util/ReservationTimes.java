package com.shao.labequipment.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoUnit;

public final class ReservationTimes {
    public static final DateTimeFormatter FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd HH:mm").withResolverStyle(ResolverStyle.STRICT);
    private ReservationTimes() { }

    public static LocalDateTime databaseNow(Connection connection) throws Exception {
        try (Statement s = connection.createStatement(); ResultSet rows = s.executeQuery("SELECT NOW()")) {
            rows.next();
            return rows.getTimestamp(1).toLocalDateTime();
        }
    }

    public static LocalDateTime defaultStart() throws Exception {
        try (Connection c = DatabaseUtil.getConnection()) {
            return nextSlot(databaseNow(c));
        }
    }

    public static LocalDateTime nextSlot(LocalDateTime now) {
        return now.truncatedTo(ChronoUnit.HOURS).plusMinutes(now.getMinute() < 30 ? 30 : 60);
    }

    public static void validate(LocalDateTime start, LocalDateTime end, LocalDateTime now) {
        if (start == null || end == null) throw new IllegalArgumentException("请填写开始和结束时间。");
        if (!start.isAfter(now)) throw new IllegalArgumentException("开始时间必须晚于当前数据库时间，请重新选择。");
        if (!end.isAfter(start)) throw new IllegalArgumentException("结束时间必须晚于开始时间。");
    }
}
