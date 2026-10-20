package com.shao.labequipment.dao;

import com.shao.labequipment.entity.DashboardSummary;
import com.shao.labequipment.entity.DeviceUsage;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;
import com.shao.labequipment.entity.LaboratoryOverview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DashboardDao {

    public DashboardSummary getSummary() throws Exception {
        Session.requireManager();
        String sql = """
                SELECT
                    (SELECT COUNT(*) FROM laboratory) AS laboratory_count,
                    (SELECT COUNT(*) FROM device) AS device_count,
                    (SELECT COUNT(*) FROM reservation
                     WHERE status = 'PENDING') AS pending_reservation_count,
                    (SELECT COUNT(*) FROM borrow_record
                     WHERE status IN ('BORROWED', 'OVERDUE')) AS borrowing_count
                """;

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();

            return new DashboardSummary(
                    resultSet.getInt("laboratory_count"),
                    resultSet.getInt("device_count"),
                    resultSet.getInt("pending_reservation_count"),
                    resultSet.getInt("borrowing_count")
            );
        }
    }

    public List<DeviceUsage> findTopDevices() throws Exception {
        return findDeviceUsage().stream().limit(5).toList();
    }

    public List<DeviceUsage> findDeviceUsage() throws Exception {
        Session.requireManager();
        String sql = """
                SELECT device_no, device_name, category_name, lab_name, device_status,
                       borrow_count, total_borrow_minutes
                FROM vw_device_usage
                ORDER BY borrow_count DESC, total_borrow_minutes DESC, device_no
                """;

        List<DeviceUsage> devices = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                devices.add(new DeviceUsage(
                        resultSet.getString("device_no"),
                        resultSet.getString("device_name"),
                        resultSet.getString("category_name"),
                        resultSet.getString("lab_name"),
                        resultSet.getString("device_status"),
                        resultSet.getLong("borrow_count"),
                        resultSet.getLong("total_borrow_minutes")
                ));
            }
        }

        return devices;
    }

    public List<LaboratoryOverview> findLaboratoryOverview() throws Exception {
        Session.requireManager();
        List<LaboratoryOverview> labs = new ArrayList<>();
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement s = c.prepareStatement("SELECT * FROM vw_laboratory_overview ORDER BY lab_code");
             ResultSet r = s.executeQuery()) {
            while (r.next()) labs.add(new LaboratoryOverview(r.getLong("laboratory_id"),
                    r.getString("lab_code"), r.getString("lab_name"), r.getString("location"),
                    r.getInt("capacity"), r.getString("lab_status"), r.getLong("device_count"),
                    r.getLong("available_device_count"), r.getLong("borrowed_device_count"),
                    r.getLong("maintenance_device_count"), r.getLong("disabled_device_count"),
                    r.getLong("pending_reservation_count")));
        }
        return labs;
    }
}
