package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.EmployeeDAO;
import com.hotel.dao.PositionDAO;
import com.hotel.dao.RoomCleaningDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.entity.RoomCleaning;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class RoomStatisticsDialog extends JDialog {
    private Room room;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;
    private RoomCleaningDAO roomCleaningDAO;
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;

    // Таблицы
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;
    private JTable cleaningsTable;
    private DefaultTableModel cleaningsTableModel;

    // Список уборок для работы с ID
    private List<RoomCleaning> currentCleanings;

    // Метрики
    private JLabel lblRoomNumber;
    private JLabel lblRoomType;
    private JLabel lblRoomStatus;
    private JLabel lblFloor;
    private JLabel lblPrice;
    private JLabel lblCapacity;
    private JLabel lblTotalBookings;
    private JLabel lblTotalRevenue;
    private JLabel lblAvgStayDuration;
    private JLabel lblLastCleaning;
    private JLabel lblCleaningCount;
    private JLabel lblLastCleaningStatus;

    public RoomStatisticsDialog(JFrame parent, Room room,
                                RoomDAO roomDAO, BookingDAO bookingDAO,
                                RoomCleaningDAO roomCleaningDAO,
                                EmployeeDAO employeeDAO, PositionDAO positionDAO) {
        super(parent, "Статистика номера " + room.getRoomNumber(), true);
        this.room = room;
        this.roomDAO = roomDAO;
        this.bookingDAO = bookingDAO;
        this.roomCleaningDAO = roomCleaningDAO;
        this.employeeDAO = employeeDAO;
        this.positionDAO = positionDAO;

        initComponents();
        loadData();

        pack();
        setLocationRelativeTo(parent);
        setSize(1000, 800);
        setMinimumSize(new Dimension(1000, 700));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Основная панель с прокруткой
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя часть: информация о номере
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.add(createRoomInfoPanel(), BorderLayout.NORTH);
        topPanel.add(createRoomStatsPanel(), BorderLayout.CENTER);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Центральная часть с вкладками
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("История бронирований", createBookingsHistoryPanel());
        tabbedPane.addTab("История уборок", createCleaningsHistoryPanel());

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Нижняя часть с кнопками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JButton btnAssignCleaning = new JButton("Назначить уборку");
        btnAssignCleaning.setFont(new Font("Arial", Font.BOLD, 12));
        // Убрали иконку, которая вызывала ошибку
        btnAssignCleaning.addActionListener(e -> assignCleaning());

        JButton btnRefresh = new JButton("Обновить");
        btnRefresh.addActionListener(e -> refreshData());

        JButton btnClose = new JButton("Закрыть");
        btnClose.addActionListener(e -> dispose());

        buttonPanel.add(btnAssignCleaning);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnClose);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Добавляем все в скролл панель
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Основная информация о номере"));
        panel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);

        int row = 0;

        // Номер комнаты
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Номер комнаты:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        lblRoomNumber = new JLabel(room.getRoomNumber());
        lblRoomNumber.setFont(new Font("Arial", Font.BOLD, 16));
        lblRoomNumber.setForeground(new Color(0, 0, 139));
        panel.add(lblRoomNumber, gbc);
        row++;

        // Тип номера
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Тип номера:"), gbc);
        gbc.gridx = 1;
        lblRoomType = new JLabel(room.getRoomType());
        lblRoomType.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblRoomType, gbc);
        row++;

        // Статус
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        lblRoomStatus = new JLabel(room.getStatus());
        lblRoomStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblRoomStatus.setForeground(getStatusColor(room.getStatus()));
        panel.add(lblRoomStatus, gbc);
        row++;

        // Этаж
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Этаж:"), gbc);
        gbc.gridx = 1;
        lblFloor = new JLabel(String.valueOf(room.getFloor()));
        panel.add(lblFloor, gbc);
        row++;

        // Цена
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Цена за сутки:"), gbc);
        gbc.gridx = 1;
        lblPrice = new JLabel(String.format("%.2f руб.", room.getPrice()));
        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));
        lblPrice.setForeground(new Color(0, 100, 0));
        panel.add(lblPrice, gbc);
        row++;

        // Вместимость
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        panel.add(new JLabel("Вместимость:"), gbc);
        gbc.gridx = 1;
        lblCapacity = new JLabel(String.valueOf(room.getCapacity()) + " человек");
        panel.add(lblCapacity, gbc);

        // Вторая колонка - описание
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridheight = 6; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JTextArea descriptionArea = new JTextArea(room.getDescription() != null ? room.getDescription() : "Нет описания");
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(panel.getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setBorder(BorderFactory.createTitledBorder("Описание"));
        descScroll.setPreferredSize(new Dimension(250, 150));
        panel.add(descScroll, gbc);

        return panel;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "Свободен":
                return new Color(0, 128, 0); // зеленый
            case "Занят":
                return Color.RED;
            case "Забронирован":
                return new Color(0, 0, 255); // синий
            case "На ремонте":
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }

    private JPanel createRoomStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Статистика номера"));
        panel.setBackground(new Color(245, 245, 245));

        panel.add(createStatCard("Всего бронирований", lblTotalBookings = new JLabel("0"),
                new Color(70, 130, 180)));
        panel.add(createStatCard("Общий доход", lblTotalRevenue = new JLabel("0 руб."),
                new Color(34, 139, 34)));
        panel.add(createStatCard("Средняя продолжительность", lblAvgStayDuration = new JLabel("0 дней"),
                new Color(139, 0, 139)));
        panel.add(createStatCard("Количество уборок", lblCleaningCount = new JLabel("0"),
                new Color(255, 140, 0)));

        // Последняя уборка
        JPanel lastCleaningPanel = createStatCard("Последняя уборка", lblLastCleaning = new JLabel("Нет данных"),
                new Color(30, 144, 255));

        // Статус последней уборки
        JPanel lastCleaningStatusPanel = createStatCard("Статус уборки", lblLastCleaningStatus = new JLabel("Нет данных"),
                new Color(220, 20, 60));

        panel.add(lastCleaningPanel);
        panel.add(lastCleaningStatusPanel);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(Color.DARK_GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBookingsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Модель таблицы бронирований
        String[] columns = {"ID", "Гость", "Заезд", "Выезд", "Статус", "Стоимость", "Создано"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);
        bookingsTable.setRowHeight(25);

        // Настройка ширины колонок
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCleaningsHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Модель таблицы уборок
        String[] columns = {"ID", "Дата", "Уборщик", "Статус", "Примечания", "Создано"};
        cleaningsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cleaningsTable = new JTable(cleaningsTableModel);
        cleaningsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        cleaningsTable.getTableHeader().setReorderingAllowed(false);
        cleaningsTable.setRowHeight(25);

        // Скрываем колонку ID
        cleaningsTable.getColumnModel().getColumn(0).setMinWidth(0);
        cleaningsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        cleaningsTable.getColumnModel().getColumn(0).setWidth(0);

        // Настройка ширины остальных колонок
        cleaningsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        cleaningsTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        cleaningsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        cleaningsTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        cleaningsTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(cleaningsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками для управления уборками
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));

        JButton btnMarkCompleted = new JButton("Отметить как завершенную");
        btnMarkCompleted.addActionListener(e -> markCleaningAsCompleted());

        JButton btnRefreshCleanings = new JButton("Обновить");
        btnRefreshCleanings.addActionListener(e -> loadCleaningsHistory());

        buttonPanel.add(btnMarkCompleted);
        buttonPanel.add(btnRefreshCleanings);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Добавляем контекстное меню для таблицы
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem markCompletedItem = new JMenuItem("Отметить как завершенную");
        markCompletedItem.addActionListener(e -> markCleaningAsCompleted());
        popupMenu.add(markCompletedItem);

        cleaningsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int row = cleaningsTable.rowAtPoint(e.getPoint());
                    if (row >= 0 && row < cleaningsTable.getRowCount()) {
                        cleaningsTable.setRowSelectionInterval(row, row);
                        popupMenu.show(cleaningsTable, e.getX(), e.getY());
                    }
                }
            }
        });

        return panel;
    }

    private void loadData() {
        loadRoomStats();
        loadBookingsHistory();
        loadCleaningsHistory();
    }

    private void refreshData() {
        loadData();
        JOptionPane.showMessageDialog(this, "Данные обновлены", "Обновление",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadRoomStats() {
        try {
            // Получаем статистику бронирований для этого номера
            List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());

            int totalBookings = bookings.size();
            double totalRevenue = 0;
            int totalDays = 0;

            for (Booking booking : bookings) {
                if ("Выселен".equals(booking.getStatus())) {
                    totalRevenue += booking.getTotalPrice();
                }

                // Расчет количества дней
                if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                    long diff = booking.getCheckOutDate().getTime() - booking.getCheckInDate().getTime();
                    totalDays += (int) (diff / (1000 * 60 * 60 * 24));
                }
            }

            double avgStayDuration = totalBookings > 0 ? (double) totalDays / totalBookings : 0;

            // Обновляем метрики
            lblTotalBookings.setText(String.valueOf(totalBookings));
            lblTotalRevenue.setText(String.format("%.2f руб.", totalRevenue));
            lblAvgStayDuration.setText(String.format("%.1f дней", avgStayDuration));

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке статистики: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadBookingsHistory() {
        bookingsTableModel.setRowCount(0);

        try {
            List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            for (Booking booking : bookings) {
                Object[] row = {
                        booking.getId(),
                        booking.getGuestSurname() + " " + booking.getGuestName(),
                        booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                        booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                        formatStatus(booking.getStatus()),
                        String.format("%.2f руб.", booking.getTotalPrice()),
                        booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
                };
                bookingsTableModel.addRow(row);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке истории бронирований: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCleaningsHistory() {
        cleaningsTableModel.setRowCount(0);

        try {
            currentCleanings = roomCleaningDAO.getCleaningByRoomId(room.getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

            int cleaningCount = currentCleanings.size();
            lblCleaningCount.setText(String.valueOf(cleaningCount));

            if (cleaningCount > 0) {
                RoomCleaning lastCleaning = currentCleanings.get(0); // Самый свежий
                lblLastCleaning.setText(dateFormat.format(lastCleaning.getCleaningDate()) +
                        " (" + lastCleaning.getEmployeeName() + ")");
                lblLastCleaningStatus.setText(formatCleaningStatusText(lastCleaning.getStatus()));
                lblLastCleaningStatus.setForeground(getCleaningStatusColor(lastCleaning.getStatus()));
            } else {
                lblLastCleaning.setText("Нет данных");
                lblLastCleaningStatus.setText("Нет данных");
            }

            for (RoomCleaning cleaning : currentCleanings) {
                Object[] row = {
                        cleaning.getId(),
                        dateFormat.format(cleaning.getCleaningDate()),
                        cleaning.getEmployeeName(),
                        formatCleaningStatus(cleaning.getStatus()),
                        cleaning.getNotes() != null ? cleaning.getNotes() : "",
                        cleaning.getCreatedAt() != null ? datetimeFormat.format(cleaning.getCreatedAt()) : ""
                };
                cleaningsTableModel.addRow(row);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке истории уборок: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void markCleaningAsCompleted() {
        int selectedRow = cleaningsTable.getSelectedRow();
        if (selectedRow >= 0) {
            // Получаем ID уборки из скрытой колонки
            int cleaningId = (int) cleaningsTableModel.getValueAt(selectedRow, 0);

            // Находим уборку в списке
            RoomCleaning selectedCleaning = null;
            for (RoomCleaning cleaning : currentCleanings) {
                if (cleaning.getId() == cleaningId) {
                    selectedCleaning = cleaning;
                    break;
                }
            }

            if (selectedCleaning != null) {
                if ("Выполнена".equals(selectedCleaning.getStatus())) {
                    JOptionPane.showMessageDialog(this, "Эта уборка уже отмечена как выполненная",
                            "Информация", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Отметить уборку от " +
                                new SimpleDateFormat("dd.MM.yyyy").format(selectedCleaning.getCleaningDate()) +
                                " как завершенную?",
                        "Подтверждение",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        roomCleaningDAO.updateCleaningStatus(cleaningId, "Выполнена");
                        JOptionPane.showMessageDialog(this, "Статус уборки изменен на 'Выполнена'",
                                "Успех", JOptionPane.INFORMATION_MESSAGE);
                        // Обновляем данные
                        loadCleaningsHistory();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Ошибка при обновлении статуса: " + e.getMessage(),
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите уборку для изменения статуса",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void assignCleaning() {
        // Открываем диалог назначения уборки
        RoomCleaningDialog dialog = new RoomCleaningDialog(
                (JFrame) getParent(),
                room,
                roomCleaningDAO,
                employeeDAO,
                positionDAO
        );
        dialog.setVisible(true);

        // Обновляем данные после закрытия диалога
        loadCleaningsHistory();
    }

    private String formatStatus(String status) {
        if (status == null) return "";

        switch (status) {
            case "Забронирован":
                return "<html><font color='blue'><b>" + status + "</b></font></html>";
            case "Заселен":
                return "<html><font color='green'><b>" + status + "</b></font></html>";
            case "Выселен":
                return "<html><font color='gray'><b>" + status + "</b></font></html>";
            case "Отменен":
                return "<html><font color='red'><b>" + status + "</b></font></html>";
            default:
                return status;
        }
    }

    private String formatCleaningStatus(String status) {
        if (status == null) return "";

        switch (status) {
            case "Назначена":
                return "<html><font color='blue'><b>" + status + "</b></font></html>";
            case "Выполнена":
                return "<html><font color='green'><b>" + status + "</b></font></html>";
            case "Отменена":
                return "<html><font color='red'><b>" + status + "</b></font></html>";
            default:
                return status;
        }
    }

    private String formatCleaningStatusText(String status) {
        if (status == null) return "";

        switch (status) {
            case "Назначена":
                return "Назначена";
            case "Выполнена":
                return "Завершена ✓";
            case "Отменена":
                return "Отменена";
            default:
                return status;
        }
    }

    private Color getCleaningStatusColor(String status) {
        switch (status) {
            case "Назначена":
                return new Color(0, 0, 255); // синий
            case "Выполнена":
                return new Color(0, 128, 0); // зеленый
            case "Отменена":
                return Color.RED;
            default:
                return Color.BLACK;
        }
    }
}