package com.hotel;

import com.hotel.ui.MainWindow;
import com.hotel.util.DatabaseConnection;

public class HotelApp {
    public static void main(String[] args) {
        // Инициализация базы данных MySQL
        DatabaseConnection.initializeDatabase();

        // Запуск главного окна
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow();
        });
    }
}