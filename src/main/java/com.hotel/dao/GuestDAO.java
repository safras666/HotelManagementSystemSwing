package com.hotel.dao;

import com.hotel.entity.Guest;
import com.hotel.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public List<Guest> getAllGuests() {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests ORDER BY surname, name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Guest guest = extractGuestFromResultSet(rs);
                guests.add(guest);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении списка гостей: " + e.getMessage());
            e.printStackTrace();
        }

        return guests;
    }

    public void addGuest(Guest guest) {
        String sql = "INSERT INTO guests (surname, name, patronymic, passport_series, passport_number, " +
                "phone, email, birth_date, address) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            setGuestParameters(pstmt, guest);
            pstmt.executeUpdate();

            // Получаем сгенерированный ID
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    guest.setGuestId(generatedKeys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении гостя: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось добавить гостя", e);
        }
    }

    public void updateGuest(Guest guest) {
        String sql = "UPDATE guests SET surname = ?, name = ?, patronymic = ?, passport_series = ?, " +
                "passport_number = ?, phone = ?, email = ?, birth_date = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setGuestParameters(pstmt, guest);
            pstmt.setInt(10, guest.getGuestId());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при обновлении гостя: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось обновить данные гостя", e);
        }
    }

    public void deleteGuest(int id) {
        String sql = "DELETE FROM guests WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Ошибка при удалении гостя: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Не удалось удалить гостя", e);
        }
    }

    public Guest getGuestById(int id) {
        String sql = "SELECT * FROM guests WHERE id = ?";
        Guest guest = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                guest = extractGuestFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при получении гостя: " + e.getMessage());
            e.printStackTrace();
        }

        return guest;
    }

    public List<Guest> searchGuests(String searchTerm) {
        List<Guest> guests = new ArrayList<>();
        String sql = "SELECT * FROM guests WHERE surname LIKE ? OR name LIKE ? OR phone LIKE ? OR passport_number LIKE ? ORDER BY surname";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeTerm = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                pstmt.setString(i, likeTerm);
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Guest guest = extractGuestFromResultSet(rs);
                guests.add(guest);
            }

        } catch (SQLException e) {
            System.err.println("Ошибка при поиске гостей: " + e.getMessage());
            e.printStackTrace();
        }

        return guests;
    }

    // Вспомогательные методы
    private Guest extractGuestFromResultSet(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("id"));
        guest.setMiddleName(rs.getString("surname"));
        guest.setFirstName(rs.getString("name"));
        guest.setLastName(rs.getString("patronymic"));
        guest.setPassportSeries(rs.getString("passport_series"));
        guest.setPassportNumber(rs.getString("passport_number"));
        guest.setPhoneNumber(rs.getString("phone"));
        guest.setEmail(rs.getString("email"));
        guest.setDateOfBirth(rs.getDate("birth_date"));
        guest.setAddress(rs.getString("address"));
        return guest;
    }

    private void setGuestParameters(PreparedStatement pstmt, Guest guest) throws SQLException {
        pstmt.setString(1, guest.getMiddleName());
        pstmt.setString(2, guest.getFirstName());
        pstmt.setString(3, guest.getLastName());
        pstmt.setString(4, guest.getPassportSeries());
        pstmt.setString(5, guest.getPassportNumber());
        pstmt.setString(6, guest.getPhoneNumber());
        pstmt.setString(7, guest.getEmail());
        pstmt.setDate(8, new java.sql.Date(guest.getDateOfBirth().getTime()));
        pstmt.setString(9, guest.getAddress());
    }
}