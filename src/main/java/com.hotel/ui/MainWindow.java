package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;
import com.hotel.util.BookingManager;
import com.hotel.entity.BookingStatusManager;
import com.hotel.util.StatusSynchronizer;
import com.hotel.dao.EmployeeDAO;
import com.hotel.entity.Employee;
import com.hotel.dao.PositionDAO;
import com.hotel.dao.RoomCleaningDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;


public class MainWindow extends JFrame {
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;
    private RoomCleaningDAO roomCleaningDAO;
    private BookingStatusManager bookingStatusManager;
    private DefaultTableModel guestsTableModel;
    private DefaultTableModel roomsTableModel;
    private DefaultTableModel bookingsTableModel;
    private JTable guestsTable;
    private JTable roomsTable;
    private JTable bookingsTable;
    private JTextField guestSearchField;
    private JTextField roomSearchField;
    private JTextField bookingSearchField;
    private BookingManager bookingManager;
    private StatusSynchronizer statusSynchronizer;
    private TableRowSorter<DefaultTableModel> bookingsTableSorter;
    private JComboBox<String> statusFilterCombo;
    private JComboBox<String> sortCombo;
    private JCheckBox showActiveOnlyCheckbox;
    private JComboBox<String> roomStatusFilterCombo;
    private DefaultTableModel employeesTableModel;
    private JTable employeesTable;
    private JTextField employeeSearchField;


    public MainWindow() {
        // Инициализация DAO
        this.guestDAO = new GuestDAO();
        this.roomDAO = new RoomDAO();
        this.bookingDAO = new BookingDAO();
        this.bookingStatusManager = new BookingStatusManager(bookingDAO, roomDAO);
        this.employeeDAO = new EmployeeDAO();
        this.positionDAO = new PositionDAO();
        this.roomCleaningDAO = new RoomCleaningDAO();

        // Инициализация менеджера бронирований
        this.bookingManager = new BookingManager(bookingDAO, roomDAO);
        bookingManager.startAutoCheck();

        // Инициализация синхронизатора
        this.statusSynchronizer = new StatusSynchronizer(bookingDAO, roomDAO);
        statusSynchronizer.startSync();

        // Настройка окна
        setTitle("Гостиничная система управления");
        setSize(1000, 700);
        setMinimumSize(new Dimension(1000, 700)); // Минимальный размер окна
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
            BookingDialog dialog = new BookingDialog(
                    MainWindow.this,
                    bookingDAO,
                    guestDAO,
                    roomDAO,
                    employeeDAO // Передаем DAO сотрудников
            );
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

        // Вкладка "Сотрудники"
        JPanel employeesPanel = createEmployeesPanel();
        tabbedPane.addTab("Сотрудники", employeesPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        // Карточки статистики
        JPanel guestCard = createStatCard("Гости", "☺", Color.BLUE);
        JPanel roomCard = createStatCard("Номера", "№", Color.GREEN);
        JPanel bookingCard = createStatCard("Бронирования", "√", Color.ORANGE);
        JPanel revenueCard = createStatCard("Доход", "₽", Color.MAGENTA);
        JPanel occupiedCard = createStatCard("Занято", "●", Color.RED);
        JPanel freeCard = createStatCard("Свободно", "○", new Color(34, 139, 34));

        // Новые карточки для сотрудников
        JPanel employeeCard = createStatCard("Сотрудники", "👨‍💼", new Color(70, 130, 180));
        employeeCard.getComponent(1).setName("value_сотрудники"); // JLabel с значением

        JPanel salaryCard = createStatCard("Зарплаты", "💰", new Color(255, 140, 0));
        salaryCard.getComponent(1).setName("value_зарплата");

        JPanel activeEmployeeCard = createStatCard("Работают", "✓", new Color(50, 205, 50));
        activeEmployeeCard.getComponent(1).setName("value_активные_сотрудники");

        statsPanel.add(guestCard);
        statsPanel.add(roomCard);
        statsPanel.add(bookingCard);
        statsPanel.add(revenueCard);
        statsPanel.add(occupiedCard);
        statsPanel.add(freeCard);
        statsPanel.add(employeeCard);
        statsPanel.add(salaryCard);
        statsPanel.add(activeEmployeeCard);

        panel.add(statsPanel, BorderLayout.CENTER);

        // Обновление статистики
        refreshDashboardStats(guestCard, roomCard, bookingCard, revenueCard,
                occupiedCard, freeCard, employeeCard, salaryCard, activeEmployeeCard);

        return panel;
    }

    private JPanel createEmployeesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с кнопками
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Кнопки действий
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JButton addButton = new JButton("Добавить сотрудника");
        topPanel.add(addButton, gbc);

        gbc.gridx = 1;
        JButton editButton = new JButton("Редактировать");
        topPanel.add(editButton, gbc);

        gbc.gridx = 2;
        JButton deleteButton = new JButton("Удалить");
        topPanel.add(deleteButton, gbc);

        gbc.gridx = 3;
        JButton refreshButton = new JButton("Обновить");
        topPanel.add(refreshButton, gbc);

        // Поиск
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.weightx = 1.0;
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Поиск:"));
        employeeSearchField = new JTextField(20); // Используем поле класса
        searchPanel.add(employeeSearchField);
        JButton searchButton = new JButton("Найти");
        searchPanel.add(searchButton);
        topPanel.add(searchPanel, gbc);

        // Модель таблицы сотрудников - используем поле класса
        String[] columns = {"ID", "Фамилия", "Имя", "Отчество", "Должность", "Телефон", "Зарплата", "Статус", "Дата приема"};
        employeesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 6) return Double.class; // Зарплата
                return String.class;
            }
        };

        // Таблица сотрудников - используем поле класса
        employeesTable = new JTable(employeesTableModel);
        employeesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeesTable.getTableHeader().setReorderingAllowed(false);

        // Настройка ширины колонок
        employeesTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        employeesTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        employeesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        employeesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        employeesTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        employeesTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        employeesTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        employeesTable.getColumnModel().getColumn(7).setPreferredWidth(80);
        employeesTable.getColumnModel().getColumn(8).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(employeesTable);

        // Заполнение таблицы
        refreshEmployeesTable();

        // Добавьте кастомный рендерер для колонки зарплаты
        employeesTable.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                if (value instanceof Double) {
                    value = String.format("%.2f руб.", (Double) value);
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        // Обработчики кнопок
        addButton.addActionListener(e -> {
            EmployeeDialog dialog = new EmployeeDialog(MainWindow.this, employeeDAO, positionDAO);
            dialog.setVisible(true);
            refreshEmployeesTable();
        });

        editButton.addActionListener(e -> {
            int selectedRow = employeesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int employeeId = (int) employeesTableModel.getValueAt(selectedRow, 0);
                Employee employee = employeeDAO.getEmployeeById(employeeId);
                if (employee != null) {
                    EmployeeDialog dialog = new EmployeeDialog(MainWindow.this, employeeDAO, positionDAO, employee);
                    dialog.setVisible(true);
                    refreshEmployeesTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите сотрудника для редактирования",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = employeesTable.getSelectedRow();
            if (selectedRow >= 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Вы уверены, что хотите удалить выбранного сотрудника?",
                        "Подтверждение удаления",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int employeeId = (int) employeesTableModel.getValueAt(selectedRow, 0);
                    employeeDAO.deleteEmployee(employeeId);
                    refreshEmployeesTable();
                    JOptionPane.showMessageDialog(this, "Сотрудник успешно удален",
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите сотрудника для удаления",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> refreshEmployeesTable());

        searchButton.addActionListener(e -> {
            String searchTerm = employeeSearchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                List<Employee> searchResults = employeeDAO.searchEmployees(searchTerm);
                updateEmployeesTable(searchResults); // Без параметра model
            } else {
                refreshEmployeesTable(); // Без параметра
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Метод обновления таблицы сотрудников (без параметра)
    private void refreshEmployeesTable() {
        List<Employee> employees = employeeDAO.getAllEmployees();
        updateEmployeesTable(employees);
    }

    // НА этот метод:
    private void updateEmployeesTable(List<Employee> employees) {
        employeesTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        for (Employee employee : employees) {
            Object[] row = {
                    employee.getId(),
                    employee.getLastName(),
                    employee.getFirstName(),
                    employee.getMiddleName() != null ? employee.getMiddleName() : "",
                    employee.getPositionName(),
                    employee.getPhone(),
                    employee.getSalary(),
                    formatEmployeeStatus(employee.getStatus()),
                    dateFormat.format(employee.getHireDate())
            };
            employeesTableModel.addRow(row);
        }
    }

    // Метод форматирования статуса сотрудника
    private String formatEmployeeStatus(String status) {
        if (status == null) return "";

        switch (status) {
            case "Работает":
                return "<html><font color='green'>" + status + "</font></html>";
            case "Уволен":
                return "<html><font color='red'>" + status + "</font></html>";
            case "Отпуск":
                return "<html><font color='orange'>" + status + "</font></html>";
            default:
                return status;
        }
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

        // Добавьте эти переменные
        int employeeCount = employeeDAO.getAllEmployees().size();
        double totalSalary = employeeDAO.getTotalSalaryExpenses();
        int activeEmployees = employeeDAO.getEmployeesByStatus("Работает").size();

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

        // Обновление карточек (добавьте в цикл):
        for (JPanel card : cards) {
            Component[] components = card.getComponents();
            for (Component comp : components) {
                if (comp instanceof JLabel) {
                    JLabel label = (JLabel) comp;
                    String name = label.getName();
                    if (name != null) {
                        switch (name) {
                            // ... существующие кейсы ...
                            case "value_сотрудники": // Создайте такую карточку
                                label.setText(String.valueOf(employeeCount));
                                break;
                            case "value_зарплата": // Создайте такую карточку
                                label.setText(String.format("%.2f руб.", totalSalary));
                                break;
                            case "value_активные_сотрудники": // Создайте такую карточку
                                label.setText(String.valueOf(activeEmployees));
                                break;
                        }
                    }
                }
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

        // Верхняя панель с кнопками - компактное расположение
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Первый ряд: кнопки действий
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JButton addButton = new JButton("Добавить гостя");
        topPanel.add(addButton, gbc);

        gbc.gridx = 1;
        JButton editButton = new JButton("Редактировать");
        topPanel.add(editButton, gbc);

        gbc.gridx = 2;
        JButton deleteButton = new JButton("Удалить");
        topPanel.add(deleteButton, gbc);

        gbc.gridx = 3;
        JButton refreshButton = new JButton("Обновить");
        topPanel.add(refreshButton, gbc);

        // Второй ряд: поиск
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4; // занимает 4 колонки
        gbc.weightx = 1.0;

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.add(new JLabel("Поиск:"));
        guestSearchField = new JTextField(20);
        searchPanel.add(guestSearchField);

        JButton searchButton = new JButton("Найти");
        searchPanel.add(searchButton);

        topPanel.add(searchPanel, gbc);

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

        // В методе createGuestsPanel() класса MainWindow, после создания таблицы guestsTable:

// Добавляем обработчик двойного щелчка для открытия статистики гостя
        guestsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = guestsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int guestId = (int) guestsTableModel.getValueAt(selectedRow, 0);
                        Guest guest = guestDAO.getGuestById(guestId);
                        if (guest != null) {
                            GuestStatisticsDialog dialog = new GuestStatisticsDialog(
                                    MainWindow.this,
                                    guest,
                                    guestDAO,
                                    bookingDAO,
                                    roomDAO
                            );
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с кнопками - компактное расположение
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Первый ряд: кнопки действий
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JButton addButton = new JButton("Добавить номер");
        topPanel.add(addButton, gbc);

        gbc.gridx = 1;
        JButton editButton = new JButton("Редактировать");
        topPanel.add(editButton, gbc);

        gbc.gridx = 2;
        JButton deleteButton = new JButton("Удалить");
        topPanel.add(deleteButton, gbc);

        gbc.gridx = 3;
        JButton refreshButton = new JButton("Обновить");
        topPanel.add(refreshButton, gbc);

        gbc.gridx = 4;
        JButton historyButton = new JButton("История");
        topPanel.add(historyButton, gbc);

        // Второй ряд: фильтры и поиск
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 5; // занимает 5 колонок
        gbc.weightx = 1.0;

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Фильтры"));

        // Фильтр по статусу
        filterPanel.add(new JLabel("Статус:"));
        roomStatusFilterCombo = new JComboBox<>(new String[]{"Все", "Свободен", "Занят", "На ремонте", "Забронирован"});
        filterPanel.add(roomStatusFilterCombo);

        // Поиск
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Поиск:"));
        roomSearchField = new JTextField(15);
        filterPanel.add(roomSearchField);

        JButton searchButton = new JButton("Найти");
        filterPanel.add(searchButton);

        JButton clearFilterButton = new JButton("Сбросить");
        filterPanel.add(clearFilterButton);

        topPanel.add(filterPanel, gbc);

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
                    RoomHistoryDialog dialog = new RoomHistoryDialog(
                            MainWindow.this,
                            room,
                            bookingDAO,
                            roomCleaningDAO,
                            employeeDAO,
                            positionDAO
                    );
                    dialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите номер для просмотра истории",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        // В методе createRoomsPanel() найдите существующий обработчик и замените его:

        // В методе createRoomsPanel() обновите обработчик двойного щелчка:
        roomsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = roomsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        int roomId = (int) roomsTableModel.getValueAt(selectedRow, 0);
                        Room room = roomDAO.getRoomById(roomId);
                        if (room != null) {
                            // Открываем новую статистику номера
                            RoomStatisticsDialog dialog = new RoomStatisticsDialog(
                                    MainWindow.this,
                                    room,
                                    roomDAO,
                                    bookingDAO,
                                    roomCleaningDAO,
                                    employeeDAO,
                                    positionDAO
                            );
                            dialog.setVisible(true);
                        }
                    }
                }
            }
        });

        // В обработчике кнопки "Редактировать" тоже передайте employeeDAO:
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
                    int bookingId = (int) bookingsTableModel.getValueAt(modelRow, 0);
                    Booking booking = bookingDAO.getBookingById(bookingId);
                    if (booking != null) {
                        BookingDialog dialog = new BookingDialog(
                                MainWindow.this,
                                bookingDAO,
                                guestDAO,
                                roomDAO,
                                employeeDAO, // Передаем DAO сотрудников
                                booking
                        );
                        dialog.setVisible(true);
                        refreshBookingsTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Выберите бронирование для редактирования",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        roomStatusFilterCombo.addActionListener(e -> {
            String selectedStatus = (String) roomStatusFilterCombo.getSelectedItem();
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

        clearFilterButton.addActionListener(e -> {
            roomStatusFilterCombo.setSelectedIndex(0);
            roomSearchField.setText("");
            refreshRoomsTable();
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Верхняя панель с кнопками действий - компактное расположение
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Первый ряд: кнопки действий
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        JButton addButton = new JButton("Новое бронирование");
        topPanel.add(addButton, gbc);

        gbc.gridx = 1;
        JButton editButton = new JButton("Редактировать");
        topPanel.add(editButton, gbc);

        gbc.gridx = 2;
        JButton cancelButton = new JButton("Отменить бронь");
        topPanel.add(cancelButton, gbc);

        gbc.gridx = 3;
        JButton checkinButton = new JButton("Заселить");
        topPanel.add(checkinButton, gbc);

        gbc.gridx = 4;
        JButton checkoutButton = new JButton("Выселить");
        topPanel.add(checkoutButton, gbc);

        gbc.gridx = 5;
        JButton refreshButton = new JButton("Обновить");
        topPanel.add(refreshButton, gbc);

        // Второй ряд: фильтры и поиск
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 6; // занимает все 6 колонок
        gbc.weightx = 1.0;

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Фильтры и сортировка"));

        // Фильтр по статусу
        filterPanel.add(new JLabel("Статус:"));
        statusFilterCombo = new JComboBox<>(new String[]{"Все статусы", "Забронирован", "Заселен", "Выселен", "Отменен"});
        filterPanel.add(statusFilterCombo);

        // Сортировка
        filterPanel.add(new JLabel("Сортировка:"));
        sortCombo = new JComboBox<>(new String[]{"По дате заезда (новые)", "По дате заезда (старые)",
                "По дате выезда", "По стоимости (убыв.)",
                "По стоимости (возр.)", "По ID"});
        filterPanel.add(sortCombo);

        // Чекбокс "Только активные"
        showActiveOnlyCheckbox = new JCheckBox("Только активные");
        filterPanel.add(showActiveOnlyCheckbox);

        // Поиск
        filterPanel.add(new JLabel("Поиск:"));
        bookingSearchField = new JTextField(15);
        filterPanel.add(bookingSearchField);

        JButton searchButton = new JButton("Найти");
        filterPanel.add(searchButton);

        JButton clearFiltersButton = new JButton("Сбросить");
        filterPanel.add(clearFiltersButton);

        topPanel.add(filterPanel, gbc);

        // Основная панель с таблицей
        panel.add(topPanel, BorderLayout.NORTH);

        // Модель таблицы бронирований с правильными типами данных
        String[] columns = {"ID", "Гость", "Номер", "Заезд", "Выезд", "Статус", "Стоимость", "Сотрудник", "Создано"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:  // ID
                        return Integer.class;
                    case 6:  // Стоимость - оставляем Number.class или Double.class
                        return Number.class;
                    default: // Все остальные колонки
                        return String.class;
                }
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);

        // Настройка сортировки таблицы с кастомными компараторами
        bookingsTableSorter = new TableRowSorter<>(bookingsTableModel);

        // Установка компараторов для разных типов данных
        bookingsTableSorter.setComparator(0, Comparator.comparingInt(o -> (Integer) o)); // ID
        bookingsTableSorter.setComparator(6, Comparator.comparingDouble(o -> (Double) o)); // Стоимость

        // Компараторы для дат (строки в формате dd.MM.yyyy)
        bookingsTableSorter.setComparator(3, new Comparator<String>() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public int compare(String s1, String s2) {
                try {
                    Date d1 = dateFormat.parse(s1);
                    Date d2 = dateFormat.parse(s2);
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return s1.compareTo(s2);
                }
            }
        });

        bookingsTableSorter.setComparator(4, new Comparator<String>() {
            private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public int compare(String s1, String s2) {
                try {
                    Date d1 = dateFormat.parse(s1);
                    Date d2 = dateFormat.parse(s2);
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return s1.compareTo(s2);
                }
            }
        });

        // Кастомный рендерер для колонки стоимости
        bookingsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                // Форматируем стоимость как целое число с "руб."
                if (value instanceof Number) {
                    Number number = (Number) value;
                    value = String.format("%d руб.", number.intValue());
                } else if (value == null) {
                    value = "0 руб.";
                }
                return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            }
        });

        bookingsTableSorter.setComparator(7, new Comparator<String>() {
            private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            @Override
            public int compare(String s1, String s2) {
                try {
                    Date d1 = datetimeFormat.parse(s1);
                    Date d2 = datetimeFormat.parse(s2);
                    return d1.compareTo(d2);
                } catch (ParseException e) {
                    return s1.compareTo(s2);
                }
            }
        });

        bookingsTable.setRowSorter(bookingsTableSorter);

        // Настройка ширины колонок
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(7).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Заполняем таблицу данными
        refreshBookingsTable();

        // В обработчике кнопки "Новое бронирование" передайте employeeDAO:
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // ОТКРЫТЬ ДИАЛОГ БРОНИРОВАНИЯ
                BookingDialog dialog = new BookingDialog(
                        MainWindow.this,
                        bookingDAO,
                        guestDAO,
                        roomDAO,
                        employeeDAO // Передаем DAO сотрудников
                );
                dialog.setVisible(true);
                refreshBookingsTable();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = bookingsTable.getSelectedRow();
                if (selectedRow >= 0) {
                    int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
                    int bookingId = (int) bookingsTableModel.getValueAt(modelRow, 0);
                    Booking booking = bookingDAO.getBookingById(bookingId);
                    if (booking != null) {
                        BookingDialog dialog = new BookingDialog(
                                MainWindow.this,
                                bookingDAO,
                                guestDAO,
                                roomDAO,
                                employeeDAO,
                                booking
                        );
                        dialog.setVisible(true);
                        refreshBookingsTable();
                    }
                } else {
                    JOptionPane.showMessageDialog(MainWindow.this,
                            "Выберите бронирование для редактирования",
                            "Ошибка", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(e -> handleCancelBooking());
        checkinButton.addActionListener(e -> handleCheckIn());
        checkoutButton.addActionListener(e -> handleCheckOut());

        // Обработчики фильтров и сортировки
        statusFilterCombo.addActionListener(e -> applyFilters());
        sortCombo.addActionListener(e -> applySorting());
        showActiveOnlyCheckbox.addActionListener(e -> applyFilters());

        searchButton.addActionListener(e -> applySearchFilter());
        clearFiltersButton.addActionListener(e -> clearFilters());
        bookingSearchField.addActionListener(e -> applySearchFilter());

        return panel;
    }

    // Метод применения фильтров
    private void applyFilters() {
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        boolean activeOnly = showActiveOnlyCheckbox.isSelected();

        List<Booking> allBookings = bookingDAO.getAllBookingsWithDetails();
        List<Booking> filteredBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            boolean statusMatch = "Все статусы".equals(statusFilter) ||
                    booking.getStatus().equals(statusFilter);

            boolean activeMatch = !activeOnly ||
                    ("Забронирован".equals(booking.getStatus()) ||
                            "Заселен".equals(booking.getStatus()));

            if (statusMatch && activeMatch) {
                filteredBookings.add(booking);
            }
        }

        updateBookingsTable(filteredBookings);
    }

    // Метод применения сортировки
    private void applySorting() {
        String sortOption = (String) sortCombo.getSelectedItem();

        switch (sortOption) {
            case "По дате заезда (новые)":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(3, SortOrder.DESCENDING)
                ));
                break;
            case "По дате заезда (старые)":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(3, SortOrder.ASCENDING)
                ));
                break;
            case "По дате выезда":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(4, SortOrder.ASCENDING)
                ));
                break;
            case "По стоимости (убыв.)":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(6, SortOrder.DESCENDING)
                ));
                break;
            case "По стоимости (возр.)":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(6, SortOrder.ASCENDING)
                ));
                break;
            case "По ID":
                bookingsTableSorter.setSortKeys(java.util.Arrays.asList(
                        new RowSorter.SortKey(0, SortOrder.ASCENDING)
                ));
                break;
        }
    }

    // Метод применения поискового фильтра
    private void applySearchFilter() {
        String searchTerm = bookingSearchField.getText().trim().toLowerCase();

        if (searchTerm.isEmpty()) {
            applyFilters();
            return;
        }

        List<Booking> allBookings = bookingDAO.getAllBookingsWithDetails();
        List<Booking> filteredBookings = new ArrayList<>();

        for (Booking booking : allBookings) {
            // Проверяем совпадение по различным полям
            boolean matches = (booking.getGuestSurname() != null &&
                    booking.getGuestSurname().toLowerCase().contains(searchTerm)) ||
                    (booking.getGuestName() != null &&
                            booking.getGuestName().toLowerCase().contains(searchTerm)) ||
                    (booking.getRoomNumber() != null &&
                            booking.getRoomNumber().toLowerCase().contains(searchTerm)) ||
                    (String.valueOf(booking.getId()).contains(searchTerm)) ||
                    (booking.getStatus() != null &&
                            booking.getStatus().toLowerCase().contains(searchTerm));

            if (matches) {
                filteredBookings.add(booking);
            }
        }

        updateBookingsTable(filteredBookings);
    }

    // Метод сброса всех фильтров
    private void clearFilters() {
        statusFilterCombo.setSelectedIndex(0);
        sortCombo.setSelectedIndex(0);
        showActiveOnlyCheckbox.setSelected(false);
        bookingSearchField.setText("");
        refreshBookingsTable();
    }

    // Обработчик отмены бронирования
    private void handleCancelBooking() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
            int bookingId = (int) bookingsTableModel.getValueAt(modelRow, 0);
            String guestName = (String) bookingsTableModel.getValueAt(modelRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Вы уверены, что хотите отменить бронирование ID: " + bookingId +
                            " для гостя " + guestName + "?",
                    "Подтверждение отмены",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookingStatusManager.cancelBooking(bookingId);

                if (success) {
                    showSuccess("Бронирование успешно отменено");
                    refreshAllTables();
                } else {
                    showError("Не удалось отменить бронирование. Проверьте статус.");
                }
            }
        } else {
            showWarning("Выберите бронирование для отмены");
        }
    }

    // Обработчик заселения
    private void handleCheckIn() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
            int bookingId = (int) bookingsTableModel.getValueAt(modelRow, 0);
            String guestName = (String) bookingsTableModel.getValueAt(modelRow, 1);
            String roomNumber = (String) bookingsTableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Подтвердить заселение гостя " + guestName +
                            " в номер " + roomNumber + "?",
                    "Подтверждение заселения",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookingStatusManager.checkInBooking(bookingId);

                if (success) {
                    showSuccess("Гость успешно заселен");
                    refreshAllTables();
                } else {
                    showError("Не удалось заселить гостя. Проверьте статус бронирования.");
                }
            }
        } else {
            showWarning("Выберите бронирование для заселения");
        }
    }



    // Обработчик выселения
    private void handleCheckOut() {
        int selectedRow = bookingsTable.getSelectedRow();
        if (selectedRow >= 0) {
            int modelRow = bookingsTable.convertRowIndexToModel(selectedRow);
            int bookingId = (int) bookingsTableModel.getValueAt(modelRow, 0);
            String guestName = (String) bookingsTableModel.getValueAt(modelRow, 1);
            String roomNumber = (String) bookingsTableModel.getValueAt(modelRow, 2);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Подтвердить выселение гостя " + guestName +
                            " из номера " + roomNumber + "?",
                    "Подтверждение выселения",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bookingStatusManager.checkOutBooking(bookingId);

                if (success) {
                    showSuccess("Гость успешно выселен");
                    refreshAllTables();
                } else {
                    showError("Не удалось выселить гостя. Проверьте статус бронирования.");
                }
            }
        } else {
            showWarning("Выберите бронирование для выселения");
        }
    }

    // Методы для обновления всех таблиц
    public void refreshAllTables() {
        refreshBookingsTable();
        refreshRoomsTable();
        refreshGuestsTable();
        refreshEmployeesTable();
    }

    public void refreshBookingsTable() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<Booking> bookings = bookingDAO.getAllBookingsWithDetails();
                updateBookingsTable(bookings);

                // Сбрасываем фильтры к начальному состоянию
                statusFilterCombo.setSelectedIndex(0);
                sortCombo.setSelectedIndex(0);
                showActiveOnlyCheckbox.setSelected(false);
                bookingSearchField.setText("");

            } catch (Exception e) {
                System.err.println("Ошибка при обновлении таблицы бронирований: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Новый метод для обновления таблицы заданным списком бронирований
    private void updateBookingsTable(List<Booking> bookings) {
        SwingUtilities.invokeLater(() -> {
            try {
                bookingsTableModel.setRowCount(0);

                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                for (Booking booking : bookings) {
                    Object[] row = {
                            booking.getId(),
                            formatGuestName(booking.getGuestSurname(), booking.getGuestName()),
                            booking.getRoomNumber(),
                            booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                            booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                            formatStatus(booking.getStatus()),
                            booking.getTotalPrice(), // Передаем Double без форматирования
                            formatEmployeeName(booking.getEmployeeLastName(), booking.getEmployeeFirstName()),
                            booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
                    };
                    bookingsTableModel.addRow(row);
                }

                System.out.println("Таблица бронирований обновлена. Записей: " + bookings.size());

                // Применяем текущую сортировку
                applySorting();

            } catch (Exception e) {
                System.err.println("Ошибка при обновлении таблицы бронирований: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Добавьте метод для форматирования имени сотрудника:
    private String formatEmployeeName(String lastName, String firstName) {
        if (lastName == null && firstName == null) return "Не указан";
        if (lastName == null) return firstName;
        if (firstName == null) return lastName;
        return lastName + " " + firstName;
    }

    public void refreshRoomsTable() {
        SwingUtilities.invokeLater(() -> {
            try {
                roomsTableModel.setRowCount(0);
                List<Room> rooms = roomDAO.getAllRooms();

                for (Room room : rooms) {
                    Object[] row = {
                            room.getId(),
                            room.getRoomNumber(),
                            room.getRoomType(),
                            room.getFloor(),
                            formatStatus(room.getStatus()),
                            String.format("%.2f руб.", room.getPrice()),
                            room.getCapacity(),
                            room.getDescription()
                    };
                    roomsTableModel.addRow(row);
                }

                System.out.println("Таблица номеров обновлена. Записей: " + rooms.size());

            } catch (Exception e) {
                System.err.println("Ошибка при обновлении таблицы номеров: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    // Вспомогательные методы для форматирования
    private String formatGuestName(String surname, String name) {
        if (surname == null && name == null) return "Неизвестно";
        if (surname == null) return name;
        if (name == null) return surname;
        return surname + " " + name;
    }

    private String formatStatus(String status) {
        if (status == null) return "";

        switch (status) {
            case "Забронирован":
                return "<html><font color='blue'>" + status + "</font></html>";
            case "Заселен":
                return "<html><font color='green'>" + status + "</font></html>";
            case "Выселен":
                return "<html><font color='gray'>" + status + "</font></html>";
            case "Отменен":
                return "<html><font color='red'>" + status + "</font></html>";
            case "Свободен":
                return "<html><font color='green'>" + status + "</font></html>";
            case "Занят":
                return "<html><font color='red'>" + status + "</font></html>";
            case "На ремонте":
                return "<html><font color='orange'>" + status + "</font></html>";
            default:
                return status;
        }
    }

    // Методы для показа сообщений
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Успех", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Внимание", JOptionPane.WARNING_MESSAGE);
    }

    public void refreshGuestsTable() {
        guestsTableModel.setRowCount(0);
        List<Guest> guests = guestDAO.getAllGuests();
        for (Guest guest : guests) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getLastName(),
                    guest.getName(),
                    guest.getMiddleName(),
                    guest.getPhoneNumber(),
                    guest.getEmail(),
                    guest.getPassportSeries() + " " + guest.getPassportNumber()
            };
            guestsTableModel.addRow(row);
        }
    }

    private void updateGuestsTable(List<Guest> guests) {
        guestsTableModel.setRowCount(0);
        for (Guest guest : guests) {
            Object[] row = {
                    guest.getGuestId(),
                    guest.getLastName(),
                    guest.getName(),
                    guest.getMiddleName(),
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
        // Останавливаем синхронизатор при закрытии
        if (statusSynchronizer != null) {
            statusSynchronizer.stopSync();
        }
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