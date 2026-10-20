package com.shao.labequipment.dao;

import com.shao.labequipment.entity.Device;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DeviceDao {

    public List<Device> search (String deviceNo, String deviceName,
                                String categoryName, String status)
            throws Exception {

        StringBuilder sql = new StringBuilder ("""
                SELECT d.id, d.device_no, d.device_name,
                       c.category_name, l.lab_name, d.status
                FROM device d
                JOIN device_category c ON d.category_id = c.id
                JOIN laboratory l ON d.laboratory_id = l.id
                WHERE 1 = 1
                """);

        List<Object> parameters = new ArrayList<> ();

        if (!deviceNo.isBlank ()) {
            sql.append (" AND d.device_no = ?");
            parameters.add (deviceNo);
        }

        if (!deviceName.isBlank ()) {
            sql.append (" AND d.device_name LIKE ?");
            parameters.add ("%" + deviceName + "%");
        }

        if (!"全部".equals (categoryName)) {
            sql.append (" AND c.category_name = ?");
            parameters.add (categoryName);
        }

        if (!"全部".equals (status)) {
            sql.append (" AND d.status = ?");
            parameters.add (status);
        }

        sql.append (" ORDER BY d.id");

        List<Device> devices = new ArrayList<> ();

        try (Connection connection = DatabaseUtil.getConnection ();
             PreparedStatement statement =
                     connection.prepareStatement (sql.toString ())) {

            for (int i = 0; i < parameters.size (); i++) {
                statement.setObject (i + 1, parameters.get (i));
            }

            try (ResultSet resultSet = statement.executeQuery ()) {
                while (resultSet.next ()) {
                    devices.add (new Device (
                            resultSet.getLong ("id"),
                            resultSet.getString ("device_no"),
                            resultSet.getString ("device_name"),
                            resultSet.getString ("category_name"),
                            resultSet.getString ("lab_name"),
                            resultSet.getString ("status")
                    ));
                }
            }
        }

        return devices;
    }

    public List<String> findAllCategoryNames() throws Exception {
        String sql = """
                SELECT category_name
                FROM device_category
                ORDER BY id
                """;

        List<String> categoryNames = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categoryNames.add(resultSet.getString("category_name"));
            }
        }

        return categoryNames;
    }

    public void insert (String deviceNo, String deviceName,
                        long categoryId, long laboratoryId,
                        String brand, String model, String status)
            throws Exception {

        Session.requirePermission("DEVICE_ADD");
        String sql = """
                INSERT INTO device
                (device_no, device_name, category_id, laboratory_id, brand, model, status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = DatabaseUtil.getConnection ();
             PreparedStatement statement = connection.prepareStatement (sql)) {

            statement.setString (1, deviceNo);
            statement.setString (2, deviceName);
            statement.setLong (3, categoryId);
            statement.setLong (4, laboratoryId);
            statement.setString (5, brand);
            statement.setString (6, model);
            statement.setString (7, status);

            statement.executeUpdate ();
        }
    }

    public void updateBasic (long id, String deviceName, String status)
            throws Exception {

        Session.requirePermission("DEVICE_EDIT");
        String sql = """
                UPDATE device
                SET device_name = ?, status = ?
                WHERE id = ?
                """;

        try (Connection connection = DatabaseUtil.getConnection ();
             PreparedStatement statement = connection.prepareStatement (sql)) {

            statement.setString (1, deviceName);
            statement.setString (2, status);
            statement.setLong (3, id);

            statement.executeUpdate ();
        }
    }

    public void deleteById (long id) throws Exception {
        Session.requirePermission("DEVICE_DELETE");
        String sql = "DELETE FROM device WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection ();
             PreparedStatement statement = connection.prepareStatement (sql)) {

            statement.setLong (1, id);
            statement.executeUpdate ();
        }
    }
}
