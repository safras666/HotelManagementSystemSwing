package com.hotel.dao;

import com.hotel.entity.Position;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PositionDAO {

    public List<Position> getAllPositions() {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM positions ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Position position = extractPositionFromResultSet(rs);
                positions.add(position);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка должностей: " + e.getMessage());
            e.printStackTrace();
        }

        return positions;
    }

    public Position getPositionById(int id) {
        String sql = "SELECT * FROM positions WHERE id = ?";
        Position position = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                position = extractPositionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении должности: " + e.getMessage());
            e.printStackTrace();
        }

        return position;
    }

    public Position getPositionByName(String name) {
        String sql = "SELECT * FROM positions WHERE name = ?";
        Position position = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                position = extractPositionFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении должности по имени: " + e.getMessage());
            e.printStackTrace();
        }

        return position;
    }

    private Position extractPositionFromResultSet(ResultSet rs) throws SQLException {
        Position position = new Position();
        position.setId(rs.getInt("id"));
        position.setName(rs.getString("name"));
        position.setDescription(rs.getString("description"));
        position.setCreatedAt(rs.getTimestamp("created_at"));
        return position;
    }
}