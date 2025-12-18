package com.hotel.dao;

import com.hotel.entity.Booking;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class BookingDAO {

    public List<Booking> getBookingsByRoomId(int roomId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, r.room_number " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.room_id = ? " +
                "ORDER BY b.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении истории бронирований: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, r.room_number " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "ORDER BY b.check_in_date DESC";

        System.out.println("Выполняем запрос: " + sql);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("Запрос выполнен успешно");

            int count = 0;
            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
                count++;

                System.out.println("Загружено бронирование #" + count + ":");
                System.out.println("  ID: " + booking.getId());
                System.out.println("  Гость: " + booking.getGuestSurname() + " " + booking.getGuestName());
                System.out.println("  Номер: " + booking.getRoomNumber());
                System.out.println("  Статус: " + booking.getStatus());
            }

            System.out.println("Всего загружено бронирований: " + count);

        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех бронирований: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    public void addBooking(Booking booking) {
        String sql = "INSERT INTO bookings (guest_id, room_id, check_in_date, check_out_date, " +
                "status, total_price) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("Добавление бронирования в БД:");
            System.out.println("Гость ID: " + booking.getGuestId());
            System.out.println("Номер ID: " + booking.getRoomId());
            System.out.println("Статус: " + booking.getStatus());
            System.out.println("Стоимость: " + booking.getTotalPrice());

            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomId());
            pstmt.setDate(3, new java.sql.Date(booking.getCheckInDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(booking.getCheckOutDate().getTime()));
            pstmt.setString(5, booking.getStatus());
            pstmt.setDouble(6, booking.getTotalPrice());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Строк добавлено: " + rowsAffected);

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    booking.setId(generatedKeys.getInt(1));
                    System.out.println("ID нового бронирования: " + booking.getId());
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении бронирования: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось добавить бронирование", e);
        }
    }

    public void updateBooking(Booking booking) {
        String sql = "UPDATE bookings SET guest_id = ?, room_id = ?, check_in_date = ?, " +
                "check_out_date = ?, status = ?, total_price = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("=== ДЕБАГ: Обновление бронирования ===");
            System.out.println("ID: " + booking.getId());
            System.out.println("Guest ID: " + booking.getGuestId());
            System.out.println("Room ID: " + booking.getRoomId());
            System.out.println("Status: " + booking.getStatus());
            System.out.println("Total Price: " + booking.getTotalPrice());
            System.out.println("Check-in: " + booking.getCheckInDate());
            System.out.println("Check-out: " + booking.getCheckOutDate());

            pstmt.setInt(1, booking.getGuestId());
            pstmt.setInt(2, booking.getRoomId());
            pstmt.setDate(3, new java.sql.Date(booking.getCheckInDate().getTime()));
            pstmt.setDate(4, new java.sql.Date(booking.getCheckOutDate().getTime()));
            pstmt.setString(5, booking.getStatus());
            pstmt.setDouble(6, booking.getTotalPrice());
            pstmt.setInt(7, booking.getId());

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Строк обновлено: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("Бронирование обновлено: ID " + booking.getId());
            } else {
                System.err.println("НЕ НАЙДЕНО бронирование с ID: " + booking.getId());
                throw new RuntimeException("Бронирование с ID " + booking.getId() + " не найдено");
            }

        } catch (SQLException e) {
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Ошибка при обновлении бронирования: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось обновить бронирование", e);
        }
    }

    // Улучшенный метод обновления статуса с возвратом результата
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, bookingId);

            int rowsUpdated = pstmt.executeUpdate();
            System.out.println("Статус бронирования ID " + bookingId + " изменен на: " + newStatus +
                    " (обновлено строк: " + rowsUpdated + ")");

            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении статуса бронирования: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Метод для получения бронирования по ID с полными данными
    public Booking getBookingByIdWithDetails(int id) {
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, " +
                "r.room_number, r.status as room_status " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.id = ?";
        Booking booking = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                booking = extractBookingFromResultSet(rs);
                // Добавляем статус комнаты
                booking.setRoomNumber(rs.getString("room_number"));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении бронирования: " + e.getMessage());
            e.printStackTrace();
        }

        return booking;
    }

    // Метод для получения всех бронирований с деталями
    public List<Booking> getAllBookingsWithDetails() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, " +
                "r.room_number, r.status as room_status " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "ORDER BY b.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                booking.setRoomNumber(rs.getString("room_number"));
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении всех бронирований: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    public void deleteBooking(int id) {
        String sql = "DELETE FROM bookings WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

            System.out.println("Бронирование удалено: ID " + id);

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении бронирования: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Booking getBookingById(int id) {
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, r.room_number " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.id = ?";
        Booking booking = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                booking = extractBookingFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении бронирования: " + e.getMessage());
            e.printStackTrace();
        }

        return booking;
    }

    public List<Booking> getActiveBookings() {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, r.room_number " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.status IN ('Забронирован', 'Заселен') " +
                "ORDER BY b.check_in_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении активных бронирований: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    public boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut) {
        String sql = "SELECT COUNT(*) as count FROM bookings " +
                "WHERE room_id = ? AND status IN ('Забронирован', 'Заселен') " +
                "AND ((check_in_date <= ? AND check_out_date >= ?) " +
                "OR (check_in_date <= ? AND check_out_date >= ?) " +
                "OR (check_in_date >= ? AND check_out_date <= ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            pstmt.setDate(2, new java.sql.Date(checkOut.getTime()));
            pstmt.setDate(3, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(4, new java.sql.Date(checkOut.getTime()));
            pstmt.setDate(5, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(6, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(7, new java.sql.Date(checkOut.getTime()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Найдено пересечений для комнаты " + roomId + ": " + count);
                return count == 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке доступности номера: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Метод проверки доступности номера с исключением текущего бронирования
    public boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut, int excludeBookingId) {
        String sql = "SELECT COUNT(*) as count FROM bookings " +
                "WHERE room_id = ? AND status IN ('Забронирован', 'Заселен') " +
                "AND id != ? " +  // Исключаем текущее бронирование при редактировании
                "AND ((check_in_date <= ? AND check_out_date >= ?) " +
                "OR (check_in_date <= ? AND check_out_date >= ?) " +
                "OR (check_in_date >= ? AND check_out_date <= ?))";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            pstmt.setInt(2, excludeBookingId);
            pstmt.setDate(3, new java.sql.Date(checkOut.getTime()));
            pstmt.setDate(4, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(5, new java.sql.Date(checkOut.getTime()));
            pstmt.setDate(6, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(7, new java.sql.Date(checkIn.getTime()));
            pstmt.setDate(8, new java.sql.Date(checkOut.getTime()));

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Найдено пересечений для комнаты " + roomId + " (исключая ID " + excludeBookingId + "): " + count);
                return count == 0;
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при проверке доступности номера: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Вспомогательные методы
    private Booking extractBookingFromResultSet(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setId(rs.getInt("id"));
        booking.setGuestId(rs.getInt("guest_id"));
        booking.setRoomId(rs.getInt("room_id"));
        booking.setCheckInDate(rs.getDate("check_in_date"));
        booking.setCheckOutDate(rs.getDate("check_out_date"));
        booking.setStatus(rs.getString("status"));
        booking.setTotalPrice(rs.getDouble("total_price"));

        // Проверяем, есть ли поле created_at в таблице
        try {
            booking.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            // Если поля нет, игнорируем ошибку
            System.out.println("Поле created_at не найдено в таблице bookings");
        }

        // Проверяем, есть ли поля из JOIN
        try {
            booking.setGuestSurname(rs.getString("guest_surname"));
            booking.setGuestName(rs.getString("guest_name"));
            booking.setRoomNumber(rs.getString("room_number"));

            System.out.println("Извлечение данных: гостя=" + booking.getGuestSurname() +
                    ", номера=" + booking.getRoomNumber());
        } catch (SQLException e) {
            System.err.println("Ошибка при извлечении полей из JOIN: " + e.getMessage());
            // Устанавливаем значения по умолчанию
            booking.setGuestSurname("Неизвестно");
            booking.setGuestName("");
            booking.setRoomNumber("?");
        }

        return booking;
    }

    // В BookingDAO.java добавьте эти методы:

    // Методы для статистики номера
    public long getTotalBookingsCountForRoom(int roomId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE room_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении количества бронирований для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getActiveBookingsCountForRoom(int roomId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE room_id = ? AND status IN ('Забронирован', 'Заселен')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении активных бронирований для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getCompletedBookingsCountForRoom(int roomId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE room_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении завершенных бронирований для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getCancelledBookingsCountForRoom(int roomId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE room_id = ? AND status = 'Отменен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении отмененных бронирований для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalRevenueForRoom(int roomId) {
        String sql = "SELECT SUM(total_price) as total FROM bookings WHERE room_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении общего дохода для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getAverageStayDurationForRoom(int roomId) {
        String sql = "SELECT AVG(DATEDIFF(check_out_date, check_in_date)) as avg_days " +
                "FROM bookings WHERE room_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_days");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении средней продолжительности пребывания для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getMostFrequentGuestForRoom(int roomId) {
        String sql = "SELECT CONCAT(g.surname, ' ', g.name) as guest_name, COUNT(*) as booking_count " +
                "FROM bookings b " +
                "JOIN guests g ON b.guest_id = g.id " +
                "WHERE b.room_id = ? " +
                "GROUP BY b.guest_id, g.surname, g.name " +
                "ORDER BY booking_count DESC " +
                "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("guest_name");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении самого частого гостя для номера: " + e.getMessage());
            e.printStackTrace();
        }
        return "Не определен";
    }

    public double getOccupancyRateForRoom(int roomId) {
        String sql = "SELECT " +
                "SUM(DATEDIFF(LEAST(check_out_date, CURDATE()), GREATEST(check_in_date, CURDATE() - INTERVAL 30 DAY))) as occupied_days " +
                "FROM bookings " +
                "WHERE room_id = ? " +
                "AND status = 'Выселен' " +
                "AND check_out_date >= CURDATE() - INTERVAL 30 DAY " +
                "AND check_in_date <= CURDATE()";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double occupiedDays = rs.getDouble("occupied_days");
                return (occupiedDays / 30) * 100; // процент занятости за последние 30 дней
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при расчете загруженности номера: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    // В файл BookingDAO.java добавьте эти методы в конец класса (перед последней фигурной скобкой):

    // Новые методы для статистики гостя
    public List<Booking> getBookingsByGuestId(int guestId) {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.*, g.surname as guest_surname, g.name as guest_name, r.room_number " +
                "FROM bookings b " +
                "LEFT JOIN guests g ON b.guest_id = g.id " +
                "LEFT JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.guest_id = ? " +
                "ORDER BY b.check_in_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Booking booking = extractBookingFromResultSet(rs);
                bookings.add(booking);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении бронирований гостя: " + e.getMessage());
            e.printStackTrace();
        }

        return bookings;
    }

    public long getTotalBookingsCount(int guestId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE guest_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении количества бронирований: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getActiveBookingsCount(int guestId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE guest_id = ? AND status IN ('Забронирован', 'Заселен')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении активных бронирований: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getCompletedBookingsCount(int guestId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE guest_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении завершенных бронирований: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public long getCancelledBookingsCount(int guestId) {
        String sql = "SELECT COUNT(*) as count FROM bookings WHERE guest_id = ? AND status = 'Отменен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getLong("count");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении отмененных бронирований: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public double getTotalSpentByGuest(int guestId) {
        String sql = "SELECT SUM(total_price) as total FROM bookings WHERE guest_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении общей суммы потраченных средств: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getAverageStayDuration(int guestId) {
        String sql = "SELECT AVG(DATEDIFF(check_out_date, check_in_date)) as avg_days " +
                "FROM bookings WHERE guest_id = ? AND status = 'Выселен'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("avg_days");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении средней продолжительности пребывания: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    public String getMostFrequentRoom(int guestId) {
        String sql = "SELECT r.room_number, COUNT(*) as booking_count " +
                "FROM bookings b " +
                "JOIN rooms r ON b.room_id = r.id " +
                "WHERE b.guest_id = ? " +
                "GROUP BY b.room_id, r.room_number " +
                "ORDER BY booking_count DESC " +
                "LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, guestId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("room_number");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении любимого номера: " + e.getMessage());
            e.printStackTrace();
        }
        return "Не определен";
    }

}