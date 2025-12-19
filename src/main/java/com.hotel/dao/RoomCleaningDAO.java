package com.hotel.dao;

import com.hotel.entity.RoomCleaning;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomCleaningDAO {

    public List<RoomCleaning> getCleaningByRoomId(int roomId) {
        List<RoomCleaning> cleanings = new ArrayList<>();
        String sql = "SELECT rc.*, r.room_number, CONCAT(e.last_name, ' ', e.first_name) as employee_name " +
                "FROM room_cleaning rc " +
                "JOIN rooms r ON rc.room_id = r.id " +
                "JOIN employees e ON rc.employee_id = e.id " +
                "WHERE rc.room_id = ? " +
                "ORDER BY rc.cleaning_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomCleaning cleaning = extractRoomCleaningFromResultSet(rs);
                cleanings.add(cleaning);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении информации об уборке: " + e.getMessage());
            e.printStackTrace();
        }

        return cleanings;
    }

    public List<RoomCleaning> getCleaningByEmployeeId(int employeeId) {
        List<RoomCleaning> cleanings = new ArrayList<>();
        String sql = "SELECT rc.*, r.room_number, CONCAT(e.last_name, ' ', e.first_name) as employee_name " +
                "FROM room_cleaning rc " +
                "JOIN rooms r ON rc.room_id = r.id " +
                "JOIN employees e ON rc.employee_id = e.id " +
                "WHERE rc.employee_id = ? " +
                "ORDER BY rc.cleaning_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                RoomCleaning cleaning = extractRoomCleaningFromResultSet(rs);
                cleanings.add(cleaning);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении уборок сотрудника: " + e.getMessage());
            e.printStackTrace();
        }

        return cleanings;
    }

    public void addCleaning(RoomCleaning cleaning) {
        String sql = "INSERT INTO room_cleaning (room_id, employee_id, cleaning_date, status, notes) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, cleaning.getRoomId());
            pstmt.setInt(2, cleaning.getEmployeeId());
            pstmt.setDate(3, new java.sql.Date(cleaning.getCleaningDate().getTime()));
            pstmt.setString(4, cleaning.getStatus());
            pstmt.setString(5, cleaning.getNotes());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cleaning.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении уборки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateCleaningStatus(int id, String status) {
        String sql = "UPDATE room_cleaning SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении статуса уборки: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private RoomCleaning extractRoomCleaningFromResultSet(ResultSet rs) throws SQLException {
        RoomCleaning cleaning = new RoomCleaning();
        cleaning.setId(rs.getInt("id"));
        cleaning.setRoomId(rs.getInt("room_id"));
        cleaning.setEmployeeId(rs.getInt("employee_id"));
        cleaning.setCleaningDate(rs.getDate("cleaning_date"));
        cleaning.setStatus(rs.getString("status"));
        cleaning.setNotes(rs.getString("notes"));
        cleaning.setCreatedAt(rs.getTimestamp("created_at"));
        cleaning.setRoomNumber(rs.getString("room_number"));
        cleaning.setEmployeeName(rs.getString("employee_name"));
        return cleaning;
    }
}