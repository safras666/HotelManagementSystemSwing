package com.hotel.ui;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    public MainWindow() {
        // Настройка окна
        setTitle("Гостиничная система управления");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Центрирование окна

        // Создание меню
        createMenu();

        // Создание панели с вкладками
        createTabbedPane();

        // Показать окно
        setVisible(true);
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл"
        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // Меню "Справочники"
        JMenu refMenu = new JMenu("Справочники");
        JMenuItem guestsItem = new JMenuItem("Гости");
        JMenuItem roomsItem = new JMenuItem("Номера");
        JMenuItem servicesItem = new JMenuItem("Услуги");
        refMenu.add(guestsItem);
        refMenu.add(roomsItem);
        refMenu.add(servicesItem);

        // Меню "Операции"
        JMenu operationsMenu = new JMenu("Операции");
        JMenuItem bookingItem = new JMenuItem("Бронирование");
        JMenuItem checkinItem = new JMenuItem("Заселение");
        JMenuItem checkoutItem = new JMenuItem("Выселение");
        operationsMenu.add(bookingItem);
        operationsMenu.add(checkinItem);
        operationsMenu.add(checkoutItem);

        // Меню "Отчеты"
        JMenu reportsMenu = new JMenu("Отчеты");
        JMenuItem report1Item = new JMenuItem("Отчет по занятости");
        JMenuItem report2Item = new JMenuItem("Финансовый отчет");
        reportsMenu.add(report1Item);
        reportsMenu.add(report2Item);

        // Добавление меню в панель
        menuBar.add(fileMenu);
        menuBar.add(refMenu);
        menuBar.add(operationsMenu);
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    private void createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Вкладка "Дашборд"
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.add(new JLabel("Панель управления"));
        tabbedPane.addTab("Дашборд", dashboardPanel);

        // Вкладка "Гости"
        JPanel guestsPanel = createGuestsPanel();
        tabbedPane.addTab("Гости", guestsPanel);

        // Вкладка "Номера"
        JPanel roomsPanel = createRoomsPanel();
        tabbedPane.addTab("Номера", roomsPanel);

        // Вкладка "Бронирование"
        JPanel bookingPanel = new JPanel();
        bookingPanel.add(new JLabel("Бронирование номеров"));
        tabbedPane.addTab("Бронирование", bookingPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createGuestsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с кнопками
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Добавить гостя");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить");

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);

        // Панель поиска
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Поиск:"));
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Найти");
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel);

        // Таблица гостей
        String[] columns = {"ID", "Фамилия", "Имя", "Телефон", "Email", "Паспорт"};
        Object[][] data = {
                {1, "Иванов", "Иван", "+79161234567", "ivanov@mail.ru", "1234 567890"},
                {2, "Петрова", "Мария", "+79161234568", "petrova@mail.ru", "4321 987654"}
        };

        addButton.addActionListener(e -> {
            GuestDialog dialog = new GuestDialog(MainWindow.this);
            if (dialog.isSaved()) {
                // Здесь будет код сохранения гостя в БД
                JOptionPane.showMessageDialog(MainWindow.this,
                        "Гость " + dialog.getLastName() + " добавлен!",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        JTable guestsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(guestsTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Обновить");
        JComboBox<String> filterCombo = new JComboBox<>(new String[]{"Все", "Свободны", "Заняты"});

        topPanel.add(refreshButton);
        topPanel.add(new JLabel("Фильтр:"));
        topPanel.add(filterCombo);

        // Таблица номеров
        String[] columns = {"Номер", "Тип", "Этаж", "Статус", "Цена", "Вместимость"};
        Object[][] data = {
                {"101", "Стандарт", 1, "Свободен", "3500 ₽", 2},
                {"102", "Стандарт", 1, "Свободен", "3500 ₽", 2},
                {"201", "Люкс", 2, "Занят", "8000 ₽", 4}
        };

        JTable roomsTable = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(roomsTable);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        // Установка темы FlatLaf (опционально)
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            System.out.println("Не удалось установить тему: " + e.getMessage());
        }

        // Запуск приложения
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}