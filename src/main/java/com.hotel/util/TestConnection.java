package com.hotel.util;

import java.sql.Connection;

public class TestConnection {
    public static void main(String[] args) {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                System.out.println("✅ Успешное подключение к базе данных!");
                DatabaseConnection.closeConnection();
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка подключения: " + e.getMessage());
        }
    }
}