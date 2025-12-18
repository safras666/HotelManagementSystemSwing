package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class RoomStatisticsDialog extends JDialog {
    private Room room;
    private RoomDAO roomDAO;
    private BookingDAO bookingDAO;

    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    // Метрики
    private JLabel lblTotalBookings;
    private JLabel lblActiveBookings;
    private JLabel lblCompletedBookings;
    private JLabel lblCancelledBookings;
    private JLabel lblTotalRevenue;
    private JLabel lblAvgStayDuration;
    private JLabel lblMostFrequentGuest;
    private JLabel lblOccupancyRate;

    public RoomStatisticsDialog(JFrame parent, Room room, RoomDAO roomDAO, BookingDAO bookingDAO) {
        super(parent, "Статистика номера", true);
        this.room = room;
        this.roomDAO = roomDAO;
        this.bookingDAO = bookingDAO;

        initComponents();
        loadData();

        pack();
        setLocationRelativeTo(parent);
        setSize(900, 700);
        setMinimumSize(new Dimension(900, 600));
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Основная панель с прокруткой
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель с основной информацией о номере
        JPanel roomInfoPanel = createRoomInfoPanel();
        mainPanel.add(roomInfoPanel, BorderLayout.NORTH);

        // Панель со статистикой
        JPanel statsPanel = createStatisticsPanel();
        mainPanel.add(statsPanel, BorderLayout.CENTER);

        // Панель с таблицей истории бронирований
        JPanel historyPanel = createHistoryPanel();
        mainPanel.add(historyPanel, BorderLayout.SOUTH);

        // Добавляем все в скролл панель
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scrollPane, BorderLayout.CENTER);

        // Кнопка закрытия
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Закрыть");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Основная информация о номере"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Номер
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("Номер:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel lblNumber = new JLabel(room.getRoomNumber());
        lblNumber.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblNumber, gbc);

        // Тип
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Тип:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(room.getRoomType()), gbc);

        // Этаж
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Этаж:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(room.getFloor())), gbc);

        // Статус
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(formatStatus(room.getStatus())), gbc);

        // Цена
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Цена за ночь:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.format("%.2f руб.", room.getPrice())), gbc);

        // Вместимость
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Вместимость:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(String.valueOf(room.getCapacity()) + " чел."), gbc);

        // Описание
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0;
        panel.add(new JLabel("Описание:"), gbc);
        gbc.gridx = 1;
        JTextArea descriptionArea = new JTextArea(room.getDescription() != null ? room.getDescription() : "нет описания");
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(panel.getBackground());
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.add(descriptionArea, gbc);

        return panel;
    }

    private JPanel createStatisticsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Статистика бронирований"));

        // Карточки статистики
        panel.add(createStatCard("Всего бронирований", lblTotalBookings = new JLabel("0"), Color.BLUE));
        panel.add(createStatCard("Активные бронирования", lblActiveBookings = new JLabel("0"), Color.GREEN));
        panel.add(createStatCard("Завершенные бронирования", lblCompletedBookings = new JLabel("0"), Color.ORANGE));
        panel.add(createStatCard("Отмененные бронирования", lblCancelledBookings = new JLabel("0"), Color.RED));
        panel.add(createStatCard("Общий доход", lblTotalRevenue = new JLabel("0 руб."), Color.MAGENTA));
        panel.add(createStatCard("Средняя продолжительность", lblAvgStayDuration = new JLabel("0 дней"), new Color(0, 150, 200)));
        panel.add(createStatCard("Самый частый гость", lblMostFrequentGuest = new JLabel("Не определен"), new Color(139, 0, 139)));
        panel.add(createStatCard("Загруженность (30 дн.)", lblOccupancyRate = new JLabel("0%"), new Color(0, 100, 0)));

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(color);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        valueLabel.setForeground(Color.DARK_GRAY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("История бронирований номера"));

        // Модель таблицы
        String[] columns = {"ID", "Гость", "Заезд", "Выезд", "Статус", "Стоимость", "Создано"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(tableModel);
        bookingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookingsTable.getTableHeader().setReorderingAllowed(false);

        // Настройка ширины колонок
        bookingsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        bookingsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        bookingsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        bookingsTable.getColumnModel().getColumn(6).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setPreferredSize(new Dimension(800, 200));

        panel.add(scrollPane, BorderLayout.CENTER);

        // Панель с кнопками для таблицы
        JPanel tableButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> loadData());
        tableButtonsPanel.add(refreshButton);

        panel.add(tableButtonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadData() {
        // Загружаем статистику
        long totalBookings = bookingDAO.getTotalBookingsCountForRoom(room.getId());
        long activeBookings = bookingDAO.getActiveBookingsCountForRoom(room.getId());
        long completedBookings = bookingDAO.getCompletedBookingsCountForRoom(room.getId());
        long cancelledBookings = bookingDAO.getCancelledBookingsCountForRoom(room.getId());
        double totalRevenue = bookingDAO.getTotalRevenueForRoom(room.getId());
        double avgStay = bookingDAO.getAverageStayDurationForRoom(room.getId());
        String mostFrequentGuest = bookingDAO.getMostFrequentGuestForRoom(room.getId());
        double occupancyRate = bookingDAO.getOccupancyRateForRoom(room.getId());

        // Обновляем метки
        lblTotalBookings.setText(String.valueOf(totalBookings));
        lblActiveBookings.setText(String.valueOf(activeBookings));
        lblCompletedBookings.setText(String.valueOf(completedBookings));
        lblCancelledBookings.setText(String.valueOf(cancelledBookings));
        lblTotalRevenue.setText(String.format("%.2f руб.", totalRevenue));
        lblAvgStayDuration.setText(String.format("%.1f дней", avgStay));
        lblMostFrequentGuest.setText(mostFrequentGuest);
        lblOccupancyRate.setText(String.format("%.1f%%", occupancyRate));

        // Загружаем историю бронирований
        loadBookingsHistory();
    }

    private void loadBookingsHistory() {
        tableModel.setRowCount(0);

        List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Booking booking : bookings) {
            Object[] row = {
                    booking.getId(),
                    formatGuestName(booking.getGuestSurname(), booking.getGuestName()),
                    booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                    booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                    formatStatus(booking.getStatus()),
                    String.format("%.2f руб.", booking.getTotalPrice()),
                    booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
            };
            tableModel.addRow(row);
        }
    }

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
}