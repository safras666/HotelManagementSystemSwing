package com.hotel.util;

import java.sql.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseConnection {
    // Настройки подключения к MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root"; // Ваш пользователь MySQL
    private static final String PASSWORD = "1234"; // Ваш пароль MySQL

    static {
        try {
            // Регистрируем драйвер MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL драйвер успешно зарегистрирован");
        } catch (ClassNotFoundException e) {
            System.err.println("Ошибка регистрации MySQL драйвера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        // Сначала создаем базу данных, если она не существует
        String createDbUrl = "jdbc:mysql://localhost:3306/?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";

        try (Connection conn = DriverManager.getConnection(createDbUrl, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Создаем базу данных, если она не существует
            stmt.execute("CREATE DATABASE IF NOT EXISTS hotel_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
            System.out.println("База данных 'hotel_db' создана или уже существует");

        } catch (SQLException e) {
            System.err.println("Ошибка при создании базы данных: " + e.getMessage());
            e.printStackTrace();
            return; // Если не можем создать БД, дальше не продолжаем
        }

        // Теперь создаем таблицы в базе данных hotel_db
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Создание таблицы гостей
            String createGuestsTable =
                    "CREATE TABLE IF NOT EXISTS guests (" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY," +
                            "    surname VARCHAR(50) NOT NULL," +
                            "    name VARCHAR(50) NOT NULL," +
                            "    patronymic VARCHAR(50)," +
                            "    passport_series VARCHAR(10)," +
                            "    passport_number VARCHAR(20) NOT NULL," +
                            "    phone VARCHAR(20)," +
                            "    email VARCHAR(100)," +
                            "    birth_date DATE NOT NULL," +
                            "    address VARCHAR(255)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.execute(createGuestsTable);
            System.out.println("Таблица 'guests' создана или уже существует");

            // Создание таблицы номеров
            String createRoomsTable =
                    "CREATE TABLE IF NOT EXISTS rooms (" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY," +
                            "    room_number VARCHAR(10) NOT NULL UNIQUE," +
                            "    room_type VARCHAR(20) NOT NULL," +
                            "    floor INT NOT NULL," +
                            "    status VARCHAR(20) NOT NULL DEFAULT 'Свободен'," +
                            "    price DECIMAL(10,2) NOT NULL," +
                            "    capacity INT NOT NULL," +
                            "    description VARCHAR(255)" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.execute(createRoomsTable);
            System.out.println("Таблица 'rooms' создана или уже существует");

            // Создание таблицы бронирований
            String createBookingsTable =
                    "CREATE TABLE IF NOT EXISTS bookings (" +
                            "    id INT AUTO_INCREMENT PRIMARY KEY," +
                            "    guest_id INT NOT NULL," +
                            "    room_id INT NOT NULL," +
                            "    check_in_date DATE NOT NULL," +
                            "    check_out_date DATE NOT NULL," +
                            "    status VARCHAR(20) NOT NULL DEFAULT 'Активно'," +
                            "    total_price DECIMAL(10,2)," +
                            "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                            "    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE," +
                            "    FOREIGN KEY (room_id) REFERENCES rooms(id) ON DELETE CASCADE" +
                            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";
            stmt.execute(createBookingsTable);
            System.out.println("Таблица 'bookings' создана или уже существует");

            // Проверяем и добавляем тестовые данные
            insertTestData(stmt);

            System.out.println("База данных MySQL успешно инициализирована");

        } catch (SQLException e) {
            System.err.println("Ошибка при инициализации базы данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void insertTestData(Statement stmt) throws SQLException {
        // Проверяем, есть ли уже данные в таблице гостей
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM guests");
        if (rs.next()) {
            int count = rs.getInt("count");
            if (count == 0) {
                // Добавляем тестовых гостей
                stmt.executeUpdate(
                        "INSERT INTO guests (surname, name, patronymic, passport_series, passport_number, phone, email, birth_date, address) VALUES " +
                                "('Иванов', 'Иван', 'Иванович', '1234', '567890', '+79161234567', 'ivanov@mail.ru', '1990-05-15', 'Москва, ул. Ленина, д.1')," +
                                "('Петрова', 'Мария', 'Сергеевна', '4321', '987654', '+79161234568', 'petrova@mail.ru', '1985-08-22', 'Санкт-Петербург, ул. Пушкина, д.10')"
                );
                System.out.println("Добавлены тестовые данные в таблицу 'guests'");
            }
        }
        rs.close();

        // Проверяем, есть ли уже данные в таблице номеров
        rs = stmt.executeQuery("SELECT COUNT(*) as count FROM rooms");
        if (rs.next()) {
            int count = rs.getInt("count");
            if (count == 0) {
                // Добавляем тестовые номера
                stmt.executeUpdate(
                        "INSERT INTO rooms (room_number, room_type, floor, status, price, capacity, description) VALUES " +
                                "('101', 'Стандарт', 1, 'Свободен', 3500.00, 2, 'Стандартный номер с двумя кроватями')," +
                                "('102', 'Стандарт', 1, 'Свободен', 3500.00, 2, 'Стандартный номер с двуспальной кроватью')," +
                                "('201', 'Люкс', 2, 'Свободен', 8000.00, 4, 'Номер люкс с гостиной зоной')," +
                                "('202', 'Бизнес', 2, 'Занят', 6000.00, 2, 'Бизнес-номер для деловых поездок')"
                );
                System.out.println("Добавлены тестовые данные в таблицу 'rooms'");
            }
        }
        rs.close();
    }

    // Метод для проверки подключения к БД
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Подключение к MySQL успешно!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к MySQL: " + e.getMessage());
            System.err.println("URL: " + URL);
            System.err.println("User: " + USER);
        }
        return false;
    }
}