package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;
import com.hotel.util.BookingManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainWindow extends JFrame {
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private DefaultTableModel guestsTableModel;
    private DefaultTableModel roomsTableModel;
    private DefaultTableModel bookingsTableModel;
    private JTable guestsTable;
    private JTable roomsTable;
    private JTable bookingsTable;
    private JTextField guestSearchField;
    private JTextField roomSearchField;
    private BookingManager bookingManager;

    public MainWindow() {
        // Инициализация DAO
        this.guestDAO = new GuestDAO();
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();

        // Инициализация менеджера бронирований
        this.bookingManager = new BookingManager(bookingDAO, roomDAO);
        bookingManager.startAutoCheck();

        // Настройка окна
        setTitle("Гостиничная система управления");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

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
        JMenuItem bookingItem = new JMenuItem("Новое бронирование");

        bookingItem.addActionListener(e -> {
            BookingDialog dialog = new BookingDialog(this, bookingDAO, guestDAO, roomDAO);
            dialog.setVisible(true);
            refreshBookingsTable();
        });

        operationsMenu.add(bookingItem);

        // Меню "Отчеты"
        JMenu reportsMenu = new JMenu("Отчеты");
        JMenuItem report1Item = new JMenuItem("Отчет по занятости");
        JMenuItem report2Item = new JMenuItem("Финансовый отчет");
        reportsMenu.add(report1Item);
        reportsMenu.add(report2Item);

        menuBar.add(fileMenu);
        menuBar.add(refMenu);
        menuBar.add(operationsMenu);
        menuBar.add(reportsMenu);

        setJMenuBar(menuBar);
    }

    private void createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Вкладка "Дашборд"
        JPanel dashboardPanel = createDashboardPanel();
        tabbedPane.addTab("Дашборд", dashboardPanel);

        // Вкладка "Гости"
        JPanel guestsPanel = createGuestsPanel();
        tabbedPane.addTab("Гости", guestsPanel);

        // Вкладка "Номера"
        JPanel roomsPanel = createRoomsPanel();
        tabbedPane.addTab("Номера", roomsPanel);

        // Вкладка "Бронирование"
        JPanel bookingPanel = createBookingsPanel();
        tabbedPane.addTab("Бронирования", bookingPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Карточки статистики
        JPanel guestCard = createStatCard("Гости", "👤", Color.BLUE);
        JPanel roomCard = createStatCard("Номера", "🏨", Color.GREEN);
        JPanel bookingCard = createStatCard("Бронирования", "📅", Color.ORANGE);
        JPanel revenueCard = createStatCard("Доход", "💰", Color.MAGENTA);
        JPanel occupiedCard = createStatCard("Занято", "🔴", Color.RED);
        JPanel freeCard = createStatCard("Свободно", "🟢", new Color(34, 139, 34));

        statsPanel.add(guestCard);
        statsPanel.add(roomCard);
        statsPanel.add(bookingCard);
        statsPanel.add(revenueCard);
        statsPanel.add(occupiedCard);
        statsPanel.add(freeCard);

        panel.add(statsPanel, BorderLayout.CENTER);

        // Обновление статистики
        refreshDashboardStats(guestCard, roomCard, bookingCard, revenueCard, occupiedCard, freeCard);

        return panel;
    }

    private JPanel createStatCard(String title, String icon, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title + " " + icon);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel("0", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(Color.DARK_GRAY);
        valueLabel.setName("value_" + title.toLowerCase());

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void refreshDashboardStats(JPanel... cards) {
        // Получаем данные
        int guestCount = guestDAO.getAllGuests().size();
        int roomCount = roomDAO.getAllRooms().size();
        int bookingCount = bookingDAO.getAllBookings().size();

        List<Room> rooms = roomDAO.getAllRooms();
        int occupiedCount = 0;
        int freeCount = 0;
        double totalRevenue = 0;

        for (Room room : rooms) {
            if ("Занят".equals(room.getStatus()) || "Забронирован".equals(room.getStatus())) {
                occupiedCount++;
            } else {
                freeCount++;
            }
        }

        // Рассчитываем доход из завершенных бронирований
        List<Booking> bookings = bookingDAO.getAllBookings();
        for (Booking booking : bookings) {
            if ("Выселен".equals(booking.getStatus())) {
                totalRevenue += booking.getTotalPrice();
            }
        }

        // Обновляем значения на карточках
        for (JPanel card : cards) {
            Component[] components = card.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String name = label.getName();
                    if (name != null) {
                        switch (name) {
                            case "value_гости":
                                label.setText(String.valueOf(guestCount));
                                break;
                            case "value_номера":
                                label.setText(String.valueOf(roomCount));
                                break;
                            case "value_бронирования":
                                label.setText(String.valueOf(bookingCount));
                                break;
                            case "value_доход":
                                label.setText(String.format("%.2f руб.", totalRevenue));
                                break;
                            case "value_занято":
                                label.setText(occupiedCount + " (" +
                                        (roomCount > 0 ? (occupiedCount * 100 / roomCount) : 0) + "%)");
                                break;
                            case "value_свободно":
                                label.setText(freeCount + " (" +
                                        (roomCount > 0 ? (freeCount * 100 / roomCount) : 0) + "%)");
                                break;
                        }
                    }
                }
            }
        }
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
        guestSearchField = new JTextField(20);
        JButton searchButton = new JButton("Найти");
        searchPanel.add(guestSearchField);
        searchPanel.add(searchButton);

        topPanel.add(searchPanel);

        // Модель таблицы гостей
        String[] columns = {"ID", "Фамилия", "Имя", "Отчество", "Телефон", "Email", "Паспорт"};
        guestsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        guestsTable = new JTable(guestsTableModel);
        guestsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestsTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane scrollPane = new JScrollPane(guestsTable);

        // Заполняем таблицу данными
        refreshGuestsTable();

        // Обработчики кнопок
        addButton.addActionListener(e -> {
            GuestDialog dialog = new GuestDialog(MainWindow.this, guestDAO);
            dialog.setVisible(true);
        });

        refreshButton.addActionListener(e -> refreshGuestsTable());

        editButton.addActionListener(e -> {
            int selectedRow = guestsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int guestId = (int) guestsTableModel.getValueAt(selectedRow, 0);
                Guest guest = guestDAO.getGuestById(guestId);
                if (guest != null) {
                    GuestDialog dialog = new GuestDialog(MainWindow.this, guestDAO, guest);
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите гостя для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = guestsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить выбранного гостя?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int guestId = (int) guestsTableModel.getValueAt(selectedRow, 0);
                    guestDAO.deleteGuest(guestId);
                    refreshGuestsTable();
                    JOptionPane.showMessageDialog(this, "Гость успешно удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите гостя для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = guestSearchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Guest> searchResults = guestDAO.searchGuests(searchTerm);
                updateGuestsTable(searchResults);
            } else {
                refreshGuestsTable();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с кнопками
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Добавить номер");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton refreshButton = new JButton("Обновить");
        JButton historyButton = new JButton("История");

        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        topPanel.add(historyButton);

        // Панель фильтрации и поиска
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Статус:"));
        JComboBox<String> statusFilterCombo = new JComboBox<>(new String[]{"Все", "Свободен", "Занят", "На ремонте", "Забронирован"});
        filterPanel.add(statusFilterCombo);

        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Поиск:"));
        roomSearchField = new JTextField(15);
        JButton searchButton = new JButton("Найти");
        filterPanel.add(roomSearchField);
        filterPanel.add(searchButton);

        topPanel.add(filterPanel);

        // Модель таблицы номеров
        String[] columns = {"ID", "Номер", "Тип", "Этаж", "Статус", "Цена", "Вместимость", "Описание"};
        roomsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomsTable = new JTable(roomsTableModel);
        roomsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomsTable.getTableHeader().setReorderingAllowed(false);

        roomsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        roomsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        roomsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        roomsTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        roomsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        roomsTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        roomsTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        roomsTable.getColumnModel().getColumn(7).setPreferredWidth(200);

        JScrollPane scrollPane = new JScrollPane(roomsTable);

        // Заполняем таблицу данными
        refreshRoomsTable();

        // Обработчики кнопок
        addButton.addActionListener(e -> {
            RoomDialog dialog = new RoomDialog(MainWindow.this, roomDAO);
            dialog.setVisible(true);
        });

        refreshButton.addActionListener(e -> refreshRoomsTable());

        editButton.addActionListener(e -> {
            int selectedRow = roomsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int roomId = (int) roomsTableModel.getValueAt(selectedRow, 0);
                Room room = roomDAO.getRoomById(roomId);
                if (room != null) {
                    RoomDialog dialog = new RoomDialog(MainWindow.this, roomDAO, room);
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите номер для редактирования", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = roomsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить выбранный номер?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int roomId = (int) roomsTableModel.getValueAt(selectedRow, 0);
                    roomDAO.deleteRoom(roomId);
                    refreshRoomsTable();
                    JOptionPane.showMessageDialog(this, "Номер успешно удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите номер для удаления", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        historyButton.addActionListener(e -> {
            int selectedRow = roomsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int roomId = (int) roomsTableModel.getValueAt(selectedRow, 0);
                Room room = roomDAO.getRoomById(roomId);
                if (room != null) {
                    RoomHistoryDialog dialog = new RoomHistoryDialog(MainWindow.this, room, bookingDAO);
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите номер для просмотра истории",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Добавляем обработчик двойного щелчка
        roomsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = roomsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int roomId = (int) roomsTableModel.getValueAt(selectedRow, 0);
                        Room room = roomDAO.getRoomById(roomId);
                        if (room != null) {
                            RoomHistoryDialog dialog = new RoomHistoryDialog(MainWindow.this, room, bookingDAO);
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });

        statusFilterCombo.addActionListener(e -> {
            String selectedStatus = (String) statusFilterCombo.getSelectedItem();
            if ("Все".equals(selectedStatus)) {
                refreshRoomsTable();
            } else {
                List<Room> filteredRooms = roomDAO.getAllRooms();
                filteredRooms.removeIf(room -> !room.getStatus().equals(selectedStatus));
                updateRoomsTable(filteredRooms);
            }
        });

        searchButton.addActionListener(e -> {
            String searchTerm = roomSearchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Room> searchResults = roomDAO.searchRooms(searchTerm);
                updateRoomsTable(searchResults);
            } else {
                refreshRoomsTable();
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // Кнопки
        JButton addButton = new JButton("Новое бронирование");
        JButton editButton = new JButton("Редактировать");
        JButton cancelButton = new JButton("Отменить бронь");
        JButton checkinButton = new JButton("Заселить");
        JButton checkoutButton = new JButton("Выселить");
        JButton refreshButton = new JButton("Обновить");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(checkinButton);
        buttonPanel.add(checkoutButton);
        buttonPanel.add(refreshButton);

        // Фильтр
        filterPanel.add(new JLabel("Статус:"));
        JComboBox<String> statusFilterCombo = new JComboBox<>(
                new String[]{"Все", "Забронирован", "Заселен", "Выселен", "Отменен"}
        );
        filterPanel.add(statusFilterCombo);

        topPanel.add(buttonPanel, BorderLayout.WEST);
        topPanel.add(filterPanel, BorderLayout.EAST);

        // Модель таблицы бронирований
        String[] columns = {"ID", "Гость", "Номер", "Заезд", "Выезд", "Статус", "Стоимость", "Создано"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);

        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);

        // Заполняем таблицу данными
        refreshBookingsTable();

        // **КОНТЕКСТНОЕ МЕНЮ ДЛЯ ТАБЛИЦЫ БРОНИРОВАНИЙ**
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem editMenuItem = new JMenuItem("Редактировать");
        JMenuItem cancelMenuItem = new JMenuItem("Отменить бронь");
        JMenuItem checkinMenuItem = new JMenuItem("Заселить");
        JMenuItem checkoutMenuItem = new JMenuItem("Выселить");
        JMenuItem viewHistoryMenuItem = new JMenuItem("История номера");

        // Обработчики для контекстного меню
        editMenuItem.addActionListener(e -> {
            // Делегируем действие кнопке "Редактировать"
            editButton.doClick();
        });

        cancelMenuItem.addActionListener(e -> {
            // Делегируем действие кнопке "Отменить бронь"
            cancelButton.doClick();
        });

        checkinMenuItem.addActionListener(e -> {
            // Делегируем действие кнопке "Заселить"
            checkinButton.doClick();
        });

        checkoutMenuItem.addActionListener(e -> {
            // Делегируем действие кнопке "Выселить"
            checkoutButton.doClick();
        });

        viewHistoryMenuItem.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                try {
                    int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
                    Booking booking = bookingDAO.getBookingById(bookingId);
                    if (booking != null && booking.getRoomId() > 0) {
                        Room room = roomDAO.getRoomById(booking.getRoomId());
                        if (room != null) {
                            RoomHistoryDialog dialog = new RoomHistoryDialog(MainWindow.this, room, bookingDAO);
                            dialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(MainWindow.this,
                                    "Не удалось найти информацию о номере",
                                    "Ошибка", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Ошибка при открытии истории: " + ex.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(MainWindow.this,
                        "Выберите бронирование для просмотра истории номера",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Добавляем пункты в контекстное меню
        popupMenu.add(editMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(cancelMenuItem);
        popupMenu.add(checkinMenuItem);
        popupMenu.add(checkoutMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(viewHistoryMenuItem);

        // Устанавливаем контекстное меню на таблицу
        bookingsTable.setComponentPopupMenu(popupMenu);

        // Добавляем слушатель для фильтра
        statusFilterCombo.addActionListener(e -> {
            String selectedStatus = (String) statusFilterCombo.getSelectedItem();
            if ("Все".equals(selectedStatus)) {
                refreshBookingsTable();
            } else {
                filterBookingsByStatus(selectedStatus);
            }
        });

        // Обработчики кнопок (основные)
        addButton.addActionListener(e -> {
            BookingDialog dialog = new BookingDialog(this, bookingDAO, guestDAO, roomDAO);
            dialog.setVisible(true);
            refreshBookingsTable();
        });

        refreshButton.addActionListener(e -> refreshBookingsTable());

        editButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
                Booking booking = bookingDAO.getBookingById(bookingId);
                if (booking != null) {
                    BookingDialog dialog = new BookingDialog(this, bookingDAO,
                            guestDAO, roomDAO);
                    dialog.setVisible(true);
                    refreshBookingsTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите бронирование для редактирования",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
                Booking booking = bookingDAO.getBookingById(bookingId);

                if (booking != null) {
                    // Проверяем, можно ли отменить это бронирование
                    if (!"Забронирован".equals(booking.getStatus())) {
                        JOptionPane.showMessageDialog(this,
                                "Можно отменить только бронирования со статусом 'Забронирован'",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Вы уверены, что хотите отменить бронирование ID: " + bookingId + "?",
                            "Подтверждение отмены",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            // Обновляем статус бронирования
                            bookingDAO.updateBookingStatus(bookingId, "Отменен");

                            // Освобождаем номер (если он был забронирован)
                            Room room = roomDAO.getRoomById(booking.getRoomId());
                            if (room != null && "Забронирован".equals(room.getStatus())) {
                                room.setStatus("Свободен");
                                roomDAO.updateRoom(room);
                            }

                            refreshBookingsTable();
                            refreshRoomsTable();
                            JOptionPane.showMessageDialog(this,
                                    "Бронирование отменено",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Ошибка при отмене бронирования: " + ex.getMessage(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите бронирование для отмены",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        checkinButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
                Booking booking = bookingDAO.getBookingById(bookingId);

                if (booking != null) {
                    if (!"Забронирован".equals(booking.getStatus())) {
                        JOptionPane.showMessageDialog(this,
                                "Можно заселить только бронирования со статусом 'Забронирован'",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Подтвердить заселение гостя " +
                                    (booking.getGuestSurname() != null ? booking.getGuestSurname() : "") + " " +
                                    (booking.getGuestName() != null ? booking.getGuestName() : "") + "?",
                            "Подтверждение заселения",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            bookingDAO.updateBookingStatus(bookingId, "Заселен");

                            // Меняем статус номера на "Занят"
                            Room room = roomDAO.getRoomById(booking.getRoomId());
                            if (room != null) {
                                room.setStatus("Занят");
                                roomDAO.updateRoom(room);
                            }

                            refreshBookingsTable();
                            refreshRoomsTable();
                            JOptionPane.showMessageDialog(this,
                                    "Заселение подтверждено",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Ошибка при заселении: " + ex.getMessage(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите бронирование для заселения",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        checkoutButton.addActionListener(e -> {
            int selectedRow = bookingsTable.getSelectedRow();
            if (selectedRow >= 0) {
                int bookingId = (int) bookingsTableModel.getValueAt(selectedRow, 0);
                Booking booking = bookingDAO.getBookingById(bookingId);

                if (booking != null) {
                    if (!"Заселен".equals(booking.getStatus())) {
                        JOptionPane.showMessageDialog(this,
                                "Можно выселить только бронирования со статусом 'Заселен'",
                                "Ошибка", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(this,
                            "Подтвердить выселение гостя " +
                                    (booking.getGuestSurname() != null ? booking.getGuestSurname() : "") + " " +
                                    (booking.getGuestName() != null ? booking.getGuestName() : "") + "?",
                            "Подтверждение выселения",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        try {
                            bookingDAO.updateBookingStatus(bookingId, "Выселен");

                            // Освобождаем номер
                            Room room = roomDAO.getRoomById(booking.getRoomId());
                            if (room != null) {
                                room.setStatus("Свободен");
                                roomDAO.updateRoom(room);
                            }

                            refreshBookingsTable();
                            refreshRoomsTable();
                            JOptionPane.showMessageDialog(this,
                                    "Выселение подтверждено",
                                    "Успех", JOptionPane.INFORMATION_MESSAGE);

                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                    "Ошибка при выселении: " + ex.getMessage(),
                                    "Ошибка", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите бронирование для выселения",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }



    public void refreshGuestsTable() {
        guestsTableModel.setRowCount(0);
        List<Guest> guests = guestDAO.getAllGuests();
        for (Guest guest : guests) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getMiddleName(),
                    guest.getFirstName(),
                    guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail(),
                    guest.getPassportSeries() + " " + guest.getPassportNumber()
            };
            guestsTableModel.addRow(row);
        }
    }

    // Метод для фильтрации бронирований по статусу
    private void filterBookingsByStatus(String status) {
        try {
            bookingsTableModel.setRowCount(0);
            List<Booking> allBookings = bookingDAO.getAllBookings();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            for (Booking booking : allBookings) {
                if (status.equals(booking.getStatus())) {
                    Object[] row = {
                            booking.getId(),
                            (booking.getGuestSurname() != null ? booking.getGuestSurname() : "") + " " +
                                    (booking.getGuestName() != null ? booking.getGuestName() : ""),
                            booking.getRoomNumber() != null ? booking.getRoomNumber() : "",
                            booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                            booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                            booking.getStatus(),
                            booking.getTotalPrice(),
                            booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
                    };
                    bookingsTableModel.addRow(row);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при фильтрации бронирований: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void refreshRoomsTable() {
        roomsTableModel.setRowCount(0);
        List<Room> rooms = roomDAO.getAllRooms();
        for (Room room : rooms) {
            Object[] row = {
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getFloor(),
                    room.getStatus(),
                    String.format("%.2f руб.", room.getPrice()),
                    room.getCapacity(),
                    room.getDescription()
            };
            roomsTableModel.addRow(row);
        }
    }

    public void refreshBookingsTable() {
        try {
            bookingsTableModel.setRowCount(0);
            List<Booking> bookings = bookingDAO.getAllBookings();

            System.out.println("Загружено бронирований: " + bookings.size());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            for (Booking booking : bookings) {
                Object[] row = {
                        booking.getId(),
                        (booking.getGuestSurname() != null ? booking.getGuestSurname() : "") + " " +
                                (booking.getGuestName() != null ? booking.getGuestName() : ""),
                        booking.getRoomNumber() != null ? booking.getRoomNumber() : "",
                        booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                        booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                        booking.getStatus(),
                        booking.getTotalPrice(),
                        booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
                };
                bookingsTableModel.addRow(row);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при обновлении таблицы бронирований: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateGuestsTable(List<Guest> guests) {
        guestsTableModel.setRowCount(0);
        for (Guest guest : guests) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getMiddleName(),
                    guest.getFirstName(),
                    guest.getLastName(),
                    guest.getPhoneNumber(),
                    guest.getEmail(),
                    guest.getPassportSeries() + " " + guest.getPassportNumber()
            };
            guestsTableModel.addRow(row);
        }
    }

    private void updateRoomsTable(List<Room> rooms) {
        roomsTableModel.setRowCount(0);
        for (Room room : rooms) {
            Object[] row = {
                    room.getId(),
                    room.getRoomNumber(),
                    room.getRoomType(),
                    room.getFloor(),
                    room.getStatus(),
                    String.format("%.2f руб.", room.getPrice()),
                    room.getCapacity(),
                    room.getDescription()
            };
            roomsTableModel.addRow(row);
        }
    }
    @Override
    public void dispose() {
        bookingManager.stopAutoCheck();
        super.dispose();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception e) {
            System.out.println("Не удалось установить тему: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}