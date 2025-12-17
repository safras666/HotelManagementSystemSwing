package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

public class RoomHistoryDialog extends JDialog {
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private Room room;
    private BookingDAO bookingDAO;

    private JButton closeButton;
    private JButton refreshButton;
    private JButton calculateRevenueButton;
    private JLabel roomInfoLabel;
    private JLabel revenueLabel;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private SimpleDateFormat datetimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

    public RoomHistoryDialog(JFrame parent, Room room, BookingDAO bookingDAO) {
        super(parent, "История номера " + room.getRoomNumber(), true);
        this.room = room;
        this.bookingDAO = bookingDAO;
        initComponents();
        loadHistory();
        pack();
        setLocationRelativeTo(parent);
        setSize(900, 600);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Информация о номере (переименовал переменную)
        JPanel infoPanelTop = new JPanel(new BorderLayout());
        infoPanelTop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String roomInfoText = String.format("<html><b>Номер:</b> %s | <b>Тип:</b> %s | <b>Этаж:</b> %d | " +
                        "<b>Статус:</b> %s | <b>Цена:</b> %.2f руб. | <b>Вместимость:</b> %d чел.</html>",
                room.getRoomNumber(), room.getRoomType(), room.getFloor(),
                room.getStatus(), room.getPrice(), room.getCapacity());

        roomInfoLabel = new JLabel(roomInfoText);
        infoPanelTop.add(roomInfoLabel, BorderLayout.WEST);

        JPanel buttonPanelTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        calculateRevenueButton = new JButton("Рассчитать доход");
        refreshButton = new JButton("Обновить");
        closeButton = new JButton("Закрыть");

        calculateRevenueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRevenue();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadHistory();
            }
        });

        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonPanelTop.add(calculateRevenueButton);
        buttonPanelTop.add(refreshButton);
        buttonPanelTop.add(closeButton);
        infoPanelTop.add(buttonPanelTop, BorderLayout.EAST);

        add(infoPanelTop, BorderLayout.NORTH);

        // Таблица истории
        String[] columns = {"ID", "Дата заезда", "Дата выезда", "Гость", "Статус",
                "Стоимость (руб.)", "Создано", "Обновлено"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) { // Стоимость
                    return Double.class;
                }
                return String.class;
            }
        };

        historyTable = new JTable(tableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.getTableHeader().setReorderingAllowed(false);

        // Настройка ширины столбцов
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Дата заезда
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Дата выезда
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(200); // Гость
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Статус
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Стоимость
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Создано
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(120); // Обновлено

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(scrollPane, BorderLayout.CENTER);

        // Статистика внизу (переименовал переменную)
        JPanel statsPanelBottom = new JPanel(new BorderLayout());
        statsPanelBottom.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 10, 5));

        JLabel totalLabel = new JLabel("Всего записей: 0");
        totalLabel.setName("total_label");
        statsGrid.add(totalLabel);

        JLabel activeLabel = new JLabel("Активные: 0");
        activeLabel.setName("active_label");
        statsGrid.add(activeLabel);

        JLabel revenueTitleLabel = new JLabel("Общий доход: ");
        statsGrid.add(revenueTitleLabel);

        revenueLabel = new JLabel("0.00 руб.");
        revenueLabel.setName("revenue_label");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 14));
        revenueLabel.setForeground(new Color(0, 100, 0));
        statsGrid.add(revenueLabel);

        statsPanelBottom.add(statsGrid, BorderLayout.CENTER);

        add(statsPanelBottom, BorderLayout.SOUTH);

        // Настройка клавиш
        getRootPane().setDefaultButton(closeButton);
    }

    private void loadHistory() {
        tableModel.setRowCount(0);
        List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());

        for (Booking booking : bookings) {
            Object[] row = {
                    booking.getId(),
                    dateFormat.format(booking.getCheckInDate()),
                    dateFormat.format(booking.getCheckOutDate()),
                    booking.getGuestSurname() + " " + booking.getGuestName(),
                    getStatusWithColor(booking.getStatus()),
                    booking.getTotalPrice(),
                    booking.getCreatedAt() != null ? datetimeFormat.format(booking.getCreatedAt()) : "",
                    booking.getUpdatedAt() != null ? datetimeFormat.format(booking.getUpdatedAt()) : ""
            };
            tableModel.addRow(row);
        }

        // Обновляем статистику
        updateStatistics();
    }

    private String getStatusWithColor(String status) {
        String color;
        switch (status) {
            case "Забронирован":
                color = "blue";
                break;
            case "Заселен":
                color = "green";
                break;
            case "Выселен":
                color = "gray";
                break;
            case "Отменен":
                color = "red";
                break;
            default:
                color = "black";
        }
        return "<html><font color='" + color + "'>" + status + "</font></html>";
    }

    private void calculateRevenue() {
        List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());

        int totalBookings = bookings.size();
        int activeBookings = 0;
        int completedBookings = 0;
        int canceledBookings = 0;
        double totalRevenue = 0;

        for (Booking booking : bookings) {
            switch (booking.getStatus()) {
                case "Забронирован":
                case "Заселен":
                    activeBookings++;
                    break;
                case "Выселен":
                    completedBookings++;
                    totalRevenue += booking.getTotalPrice();
                    break;
                case "Отменен":
                    canceledBookings++;
                    break;
            }
        }

        // Обновляем статистику
        updateStatistics(totalBookings, activeBookings, completedBookings, canceledBookings, totalRevenue);

        // Показываем подробную информацию
        String message = String.format(
                "<html><b>Статистика номера %s:</b><br>" +
                        "• Всего бронирований: %d<br>" +
                        "• Активные: %d<br>" +
                        "• Завершены: %d<br>" +
                        "• Отменены: %d<br>" +
                        "• Общий доход: <font color='green'><b>%.2f руб.</b></font></html>",
                room.getRoomNumber(), totalBookings, activeBookings,
                completedBookings, canceledBookings, totalRevenue
        );

        JOptionPane.showMessageDialog(this, message, "Статистика дохода",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private int countByStatus(List<Booking> bookings, String status) {
        int count = 0;
        for (Booking booking : bookings) {
            if (status.equals(booking.getStatus())) {
                count++;
            }
        }
        return count;
    }

    private void updateStatistics() {
        List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());

        int totalBookings = bookings.size();
        int activeBookings = 0;
        int completedBookings = 0;
        int canceledBookings = 0;
        double totalRevenue = 0;

        for (Booking booking : bookings) {
            switch (booking.getStatus()) {
                case "Забронирован":
                case "Заселен":
                    activeBookings++;
                    break;
                case "Выселен":
                    completedBookings++;
                    totalRevenue += booking.getTotalPrice();
                    break;
                case "Отменен":
                    canceledBookings++;
                    break;
            }
        }

        updateStatistics(totalBookings, activeBookings, completedBookings, canceledBookings, totalRevenue);
    }

    private void updateStatistics(int total, int active, int completed, int canceled, double revenue) {
        // Находим компоненты статистики
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getComponentCount() > 0) {
                    Component[] innerComps = panel.getComponents();
                    for (Component innerComp : innerComps) {
                        if (innerComp instanceof JPanel) {
                            JPanel statsGrid = (JPanel) innerComp;
                            Component[] statsComps = statsGrid.getComponents();
                            for (Component statComp : statsComps) {
                                if (statComp instanceof JLabel) {
                                    JLabel label = (JLabel) statComp;
                                    String name = label.getName();
                                    if (name != null) {
                                        switch (name) {
                                            case "total_label":
                                                label.setText("Всего записей: " + total);
                                                break;
                                            case "active_label":
                                                label.setText("Активные: " + active);
                                                break;
                                            case "revenue_label":
                                                label.setText(String.format("%.2f руб.", revenue));
                                                break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}