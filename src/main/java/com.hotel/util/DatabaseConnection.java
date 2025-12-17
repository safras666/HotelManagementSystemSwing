package com.hotel.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

public class DatabaseConnection {
    private static Connection connection = null;
    private static Properties properties = new Properties();

    static {
        try {
            // Загрузка настроек из файла
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream("database.properties");

            if (input != null) {
                properties.load(input);
                System.out.println("Настройки базы данных загружены");
            } else {
                System.out.println("Файл database.properties не найден");
                // Значения по умолчанию
                properties.setProperty("db.url", "jdbc:mysql://localhost:3306/hotel_management");
                properties.setProperty("db.username", "hotel_app");
                properties.setProperty("db.password", "password123");
            }

            // Регистрация драйвера
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Драйвер MySQL зарегистрирован");

        } catch (Exception e) {
            System.out.println("Ошибка загрузки настроек: " + e.getMessage());
        }
    }

    // Метод для получения соединения
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password")
            );
            System.out.println("Подключение к базе данных установлено");
        }
        return connection;
    }

    // Метод для закрытия соединения
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Подключение закрыто");
            } catch (SQLException e) {
                System.out.println("Ошибка при закрытии соединения: " + e.getMessage());
            }
        }
    }
}