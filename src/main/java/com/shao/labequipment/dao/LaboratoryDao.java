package com.shao.labequipment.dao;

import com.shao.labequipment.entity.LabRecommendation;
import com.shao.labequipment.util.DatabaseUtil;
import com.shao.labequipment.util.Session;
import com.shao.labequipment.util.ReservationTimes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LaboratoryDao {

    public List<LabRecommendation> recommendAvailableLabs(
            LocalDateTime startTime,
            LocalDateTime endTime,
            int peopleCount
    ) throws Exception {

        Session.requireActiveUser();
        if (peopleCount <= 0) throw new IllegalArgumentException("人数必须为正整数。");

        String sql = """
                SELECT l.id, l.lab_name, l.location, l.capacity,
                       ABS(l.capacity - ?) AS match_score
                FROM laboratory l
                WHERE l.status = 'AVAILABLE'
                  AND l.capacity >= ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM reservation r
                      WHERE r.laboratory_id = l.id
                        AND r.status IN ('PENDING', 'APPROVED')
                        AND r.start_time < ?
                        AND r.end_time > ?
                  )
                ORDER BY match_score ASC, l.capacity ASC
                """;

        List<LabRecommendation> recommendations = new ArrayList<>();

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            ReservationTimes.validate(startTime, endTime, ReservationTimes.databaseNow(connection));

            statement.setInt(1, peopleCount);
            statement.setInt(2, peopleCount);
            statement.setTimestamp(3, Timestamp.valueOf(endTime));
            statement.setTimestamp(4, Timestamp.valueOf(startTime));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    recommendations.add(new LabRecommendation(
                            resultSet.getLong("id"),
                            resultSet.getString("lab_name"),
                            resultSet.getString("location"),
                            resultSet.getInt("capacity"),
                            resultSet.getInt("match_score")
                    ));
                }
            }
        }

        return recommendations;
    }
}
