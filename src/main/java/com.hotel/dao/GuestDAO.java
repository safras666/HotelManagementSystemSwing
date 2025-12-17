package com.hotel.dao;

import com.hotel.entity.Guest;
import com.hotel.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public void testAddGuest() {
        Guest guest = new Guest("Тестов", "Тест", "Тестович");
        guest.setPhoneNumber("+79990001122");
        guest.setEmail("test@test.ru");

        // УСТАНОВИТЕ ДАТУ РОЖДЕНИЯ!
        guest.setDateOfBirth(new java.util.Date()); // Текущая дата

        // Установите паспортные данные (обязательные поля)
        guest.setPassportSeries("1234");
        guest.setPassportNumber("567890");

        if (addGuest(guest)) {
            System.out.println("Тестовый гость добавлен успешно!");
        }
    }
    // Добавление гостя
    public boolean addGuest(Guest guest) {
        String sql = "INSERT INTO guests (last_name, first_name, middle_name, " +
                "passport_series, passport_number, date_of_birth, phone_number, " +
                "email, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, guest.getLastName());
            stmt.setString(2, guest.getFirstName());
            stmt.setString(3, guest.getMiddleName());
            stmt.setString(4, guest.getPassportSeries());
            stmt.setString(5, guest.getPassportNumber());

            // ПРОВЕРКА НА NULL для даты рождения
            java.util.Date dateOfBirth = guest.getDateOfBirth();
            if (dateOfBirth != null) {
                stmt.setDate(6, new java.sql.Date(dateOfBirth.getTime()));
            } else {
                // Если дата рождения не указана, устанавливаем текущую дату
                stmt.setDate(6, new java.sql.Date(new java.util.Date().getTime()));
                System.out.println("Внимание: дата рождения не указана, установлена текущая дата");
            }

            stmt.setString(7, guest.getPhoneNumber());
            stmt.setString(8, guest.getEmail());
            stmt.setString(9, guest.getAddress());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        guest.setGuestId(generatedKeys.getInt(1));
                    }
                }
                System.out.println("Гость добавлен: " + guest.getLastName());
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при добавлении гостя: " + e.getMessage());
        }
        return false;
    }
    // Получение всех гостей
    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY last_name, first_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Guest guest = new Guest();
                guest.setGuestId(rs.getInt("guest_id"));
                guest.setLastName(rs.getString("last_name"));
                guest.setFirstName(rs.getString("first_name"));
                guest.setMiddleName(rs.getString("middle_name"));
                guest.setPassportSeries(rs.getString("passport_series"));
                guest.setPassportNumber(rs.getString("passport_number"));
                guest.setDateOfBirth(rs.getDate("date_of_birth"));
                guest.setPhoneNumber(rs.getString("phone_number"));
                guest.setEmail(rs.getString("email"));
                guest.setAddress(rs.getString("address"));
                guest.setCreatedAt(rs.getTimestamp("created_at"));

                guests.add(guest);
            }
            System.out.println("Загружено гостей: " + guests.size());
        } catch (SQLException e) {
            System.out.println("Ошибка при получении гостей: " + e.getMessage());
        }
        return guests;
    }

    // Поиск гостей по фамилии
    public List<Guest> searchGuests(String searchText) {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE last_name LIKE ? OR phone_number LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchText + "%");
            stmt.setString(2, "%" + searchText + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Guest guest = new Guest();
                    guest.setGuestId(rs.getInt("guest_id"));
                    guest.setLastName(rs.getString("last_name"));
                    guest.setFirstName(rs.getString("first_name"));
                    guest.setMiddleName(rs.getString("middle_name"));
                    guest.setPhoneNumber(rs.getString("phone_number"));
                    guest.setEmail(rs.getString("email"));

                    guests.add(guest);
                }
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при поиске гостей: " + e.getMessage());
        }
        return guests;
    }

    // Удаление гостя
    public boolean deleteGuest(int guestId) {
        String sql = "DELETE FROM guests WHERE guest_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, guestId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Гость удален, ID: " + guestId);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при удалении гостя: " + e.getMessage());
        }
        return false;
    }
}