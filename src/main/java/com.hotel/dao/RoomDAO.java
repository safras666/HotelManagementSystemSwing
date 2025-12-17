package com.hotel.dao;

import com.hotel.entity.Room;
import com.hotel.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // Получение всех номеров
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY floor, room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setTypeName(rs.getString("type_name"));
                room.setFloor(rs.getInt("floor"));
                room.setRoomStatus(rs.getString("room_status"));
                room.setPricePerDay(rs.getDouble("price_per_day"));
                room.setMaxGuests(rs.getInt("max_guests"));

                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении номеров: " + e.getMessage());
        }
        return rooms;
    }

    // Получение свободных номеров
    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_status = 'available' ORDER BY floor, room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = new Room();
                room.setRoomId(rs.getInt("room_id"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setTypeName(rs.getString("type_name"));
                room.setFloor(rs.getInt("floor"));
                room.setRoomStatus(rs.getString("room_status"));
                room.setPricePerDay(rs.getDouble("price_per_day"));
                room.setMaxGuests(rs.getInt("max_guests"));

                rooms.add(room);
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении свободных номеров: " + e.getMessage());
        }
        return rooms;
    }
}