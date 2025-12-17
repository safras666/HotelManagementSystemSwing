package com.hotel;

import com.formdev.flatlaf.FlatLightLaf;
import com.hotel.ui.MainWindow;
import javax.swing.*;

public class HotelApp {
    public static void main(String[] args) {
        // Установка красивой темы
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.out.println("Не удалось установить тему: " + e.getMessage());
        }

        // Запуск приложения
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);

            // Проверка подключения к БД
            try {
                com.hotel.util.DatabaseConnection.getConnection();
                System.out.println("✅ База данных подключена успешно!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(window,
                        "Ошибка подключения к базе данных!\n" + e.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}