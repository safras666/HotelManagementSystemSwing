package com.hotel.dao;

import com.hotel.entity.Room;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms ORDER BY CAST(room_number AS UNSIGNED), room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка номеров: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    public void addRoom(Room room) {
        String sql = "INSERT INTO rooms (room_number, room_type, floor, status, price, capacity, description) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setRoomParameters(pstmt, room);
            pstmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    room.setId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении номера: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось добавить номер", e);
        }
    }

    public void updateRoom(Room room) {
        String sql = "UPDATE rooms SET room_number = ?, room_type = ?, floor = ?, status = ?, " +
                "price = ?, capacity = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setRoomParameters(pstmt, room);
            pstmt.setInt(8, room.getId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении номера: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось обновить данные номера", e);
        }
    }

    // Метод для обновления только статуса комнаты
    public boolean updateRoomStatus(int roomId, String status) {
        String sql = "UPDATE rooms SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, roomId);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Статус комнаты ID " + roomId + " изменен на: " + status);

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении статуса комнаты: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для синхронизации статуса комнаты на основе бронирований
    public void syncRoomStatusFromBookings(int roomId) {
        try {
            // Получаем активные бронирования для этой комнаты
            String sql = "SELECT status FROM bookings WHERE room_id = ? AND status IN ('Забронирован', 'Заселен') " +
                    "ORDER BY check_in_date DESC LIMIT 1";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setInt(1, roomId);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String bookingStatus = rs.getString("status");
                    String roomStatus = bookingStatus.equals("Заселен") ? "Занят" : "Забронирован";
                    updateRoomStatus(roomId, roomStatus);
                    System.out.println("Синхронизирован статус комнаты " + roomId + " на: " + roomStatus);
                } else {
                    // Нет активных бронирований - комната свободна
                    updateRoomStatus(roomId, "Свободен");
                    System.out.println("Комната " + roomId + " освобождена");
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при синхронизации статуса комнаты: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteRoom(int id) {
        String sql = "DELETE FROM rooms WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении номера: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось удалить номер", e);
        }
    }

    public Room getRoomById(int id) {
        String sql = "SELECT * FROM rooms WHERE id = ?";
        Room room = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                room = extractRoomFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении номера: " + e.getMessage());
            e.printStackTrace();
        }

        return room;
    }

    public Room getRoomByNumber(String roomNumber) {
        String sql = "SELECT * FROM rooms WHERE room_number = ?";
        Room room = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, roomNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                room = extractRoomFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении номера по номеру: " + e.getMessage());
            e.printStackTrace();
        }

        return room;
    }

    public List<Room> searchRooms(String searchTerm) {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE room_number LIKE ? OR room_type LIKE ? OR status LIKE ? ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при поиске номеров: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM rooms WHERE status = 'Свободен' ORDER BY room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Room room = extractRoomFromResultSet(rs);
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении свободных номеров: " + e.getMessage());
            e.printStackTrace();
        }

        return rooms;
    }

    // Вспомогательные методы
    private Room extractRoomFromResultSet(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("id"));
        room.setRoomNumber(rs.getString("room_number"));
        room.setRoomType(rs.getString("room_type"));
        room.setFloor(rs.getInt("floor"));
        room.setStatus(rs.getString("status"));
        room.setPrice(rs.getDouble("price"));
        room.setCapacity(rs.getInt("capacity"));
        room.setDescription(rs.getString("description"));
        return room;
    }

    private void setRoomParameters(PreparedStatement pstmt, Room room) throws SQLException {
        pstmt.setString(1, room.getRoomNumber());
        pstmt.setString(2, room.getRoomType());
        pstmt.setInt(3, room.getFloor());
        pstmt.setString(4, room.getStatus());
        pstmt.setDouble(5, room.getPrice());
        pstmt.setInt(6, room.getCapacity());
        pstmt.setString(7, room.getDescription());
    }
}