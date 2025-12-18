package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class GuestStatisticsDialog extends JDialog {
    private Guest guest;
    private GuestDAO guestDAO;
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;

    private JTable bookingsTable;
    private DefaultTableModel tableModel;

    // Метрики
    private JLabel lblTotalBookings;
    private JLabel lblActiveBookings;
    private JLabel lblCompletedBookings;
    private JLabel lblCancelledBookings;
    private JLabel lblTotalSpent;
    private JLabel lblAvgStayDuration;
    private JLabel lblFavoriteRoom;

    public GuestStatisticsDialog(JFrame parent, Guest guest, GuestDAO guestDAO,
                                 BookingDAO bookingDAO, RoomDAO roomDAO) {
        super(parent, "Статистика гостя", true);
        this.guest = guest;
        this.guestDAO = guestDAO;
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;

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

        // Панель с основной информацией о госте
        JPanel guestInfoPanel = createGuestInfoPanel();
        mainPanel.add(guestInfoPanel, BorderLayout.NORTH);

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

    private JPanel createGuestInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Основная информация о госте"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // ФИО
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("ФИО:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel lblFio = new JLabel(guest.getLastName() + " " + guest.getFirstName() + " " +
                (guest.getMiddleName() != null ? guest.getMiddleName() : ""));
        lblFio.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(lblFio, gbc);

        // Паспорт
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("Паспорт:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(guest.getPassportSeries() + " " + guest.getPassportNumber()), gbc);

        // Телефон
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        panel.add(new JLabel("Телефон:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(guest.getPhoneNumber()), gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(guest.getEmail() != null ? guest.getEmail() : "не указан"), gbc);

        // Дата рождения
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0;
        panel.add(new JLabel("Дата рождения:"), gbc);
        gbc.gridx = 1;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String birthDate = guest.getDateOfBirth() != null ?
                dateFormat.format(guest.getDateOfBirth()) : "не указана";
        panel.add(new JLabel(birthDate), gbc);

        // Адрес
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0;
        panel.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(guest.getAddress() != null ? guest.getAddress() : "не указан"), gbc);

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
        panel.add(createStatCard("Общая сумма потрачена", lblTotalSpent = new JLabel("0 руб."), Color.MAGENTA));
        panel.add(createStatCard("Средняя продолжительность", lblAvgStayDuration = new JLabel("0 дней"), new Color(0, 150, 200)));
        panel.add(createStatCard("Любимый номер", lblFavoriteRoom = new JLabel("Не определен"), new Color(139, 0, 139)));

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
        panel.setBorder(BorderFactory.createTitledBorder("История бронирований"));

        // Модель таблицы
        String[] columns = {"ID", "Номер", "Заезд", "Выезд", "Статус", "Стоимость", "Создано"};
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
        bookingsTable.getColumnModel().getColumn(1).setPreferredWidth(80);
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
        long totalBookings = bookingDAO.getTotalBookingsCount(guest.getGuestId());
        long activeBookings = bookingDAO.getActiveBookingsCount(guest.getGuestId());
        long completedBookings = bookingDAO.getCompletedBookingsCount(guest.getGuestId());
        long cancelledBookings = bookingDAO.getCancelledBookingsCount(guest.getGuestId());
        double totalSpent = bookingDAO.getTotalSpentByGuest(guest.getGuestId());
        double avgStay = bookingDAO.getAverageStayDuration(guest.getGuestId());
        String favoriteRoom = bookingDAO.getMostFrequentRoom(guest.getGuestId());

        // Обновляем метки
        lblTotalBookings.setText(String.valueOf(totalBookings));
        lblActiveBookings.setText(String.valueOf(activeBookings));
        lblCompletedBookings.setText(String.valueOf(completedBookings));
        lblCancelledBookings.setText(String.valueOf(cancelledBookings));
        lblTotalSpent.setText(String.format("%.2f руб.", totalSpent));
        lblAvgStayDuration.setText(String.format("%.1f дней", avgStay));
        lblFavoriteRoom.setText(favoriteRoom);

        // Загружаем историю бронирований
        loadBookingsHistory();
    }

    private void loadBookingsHistory() {
        tableModel.setRowCount(0);

        List<Booking> bookings = bookingDAO.getBookingsByGuestId(guest.getGuestId());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Booking booking : bookings) {
            Object[] row = {
                    booking.getId(),
                    booking.getRoomNumber(),
                    booking.getCheckInDate() != null ? dateFormat.format(booking.getCheckInDate()) : "",
                    booking.getCheckOutDate() != null ? dateFormat.format(booking.getCheckOutDate()) : "",
                    formatStatus(booking.getStatus()),
                    String.format("%.2f руб.", booking.getTotalPrice()),
                    booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : ""
            };
            tableModel.addRow(row);
        }
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
            default:
                return status;
        }
    }
}