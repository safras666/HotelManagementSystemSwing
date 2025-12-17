package com.hotel.dao;

import com.hotel.util.DatabaseConnection;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class ReportDAO {

    // Отчет по занятости номеров
    public DefaultTableModel getOccupancyReport() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Номер");
        model.addColumn("Тип");
        model.addColumn("Этаж");
        model.addColumn("Статус");
        model.addColumn("Цена");

        String sql = "SELECT room_number, type_name, floor, room_status, price_per_day " +
                "FROM rooms ORDER BY floor, room_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String status = rs.getString("room_status");
                String statusDisplay = status.equals("available") ? "Свободен" :
                        status.equals("occupied") ? "Занят" : "На обслуживании";

                model.addRow(new Object[]{
                        rs.getString("room_number"),
                        rs.getString("type_name"),
                        rs.getInt("floor"),
                        statusDisplay,
                        rs.getDouble("price_per_day") + " ₽"
                });
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении отчета: " + e.getMessage());
        }

        return model;
    }

    // Отчет по гостям
    public DefaultTableModel getGuestsReport() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Фамилия");
        model.addColumn("Имя");
        model.addColumn("Телефон");
        model.addColumn("Email");
        model.addColumn("Паспорт");

        String sql = "SELECT last_name, first_name, phone_number, email, " +
                "CONCAT(passport_series, ' ', passport_number) as passport " +
                "FROM guests ORDER BY last_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("last_name"),
                        rs.getString("first_name"),
                        rs.getString("phone_number"),
                        rs.getString("email"),
                        rs.getString("passport")
                });
            }
        } catch (SQLException e) {
            System.out.println("Ошибка при получении отчета по гостям: " + e.getMessage());
        }

        return model;
    }
}