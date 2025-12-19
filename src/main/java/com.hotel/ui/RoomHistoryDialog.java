package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomCleaningDAO;
import com.hotel.dao.EmployeeDAO;
import com.hotel.dao.PositionDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;
import com.hotel.entity.RoomCleaning;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class RoomHistoryDialog extends JDialog {
    private Room room;
    private BookingDAO bookingDAO;
    private RoomCleaningDAO roomCleaningDAO;
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;

    private JTabbedPane tabbedPane;
    private JTable bookingsTable;
    private DefaultTableModel bookingsTableModel;
    private JTable cleaningsTable;
    private DefaultTableModel cleaningsTableModel;

    public RoomHistoryDialog(JFrame parent, Room room, BookingDAO bookingDAO,
                             RoomCleaningDAO roomCleaningDAO,
                             EmployeeDAO employeeDAO,
                             PositionDAO positionDAO) {
        super(parent, "Информация о номере " + room.getRoomNumber(), true);
        this.room = room;
        this.bookingDAO = bookingDAO;
        this.roomCleaningDAO = roomCleaningDAO;
        this.employeeDAO = employeeDAO;
        this.positionDAO = positionDAO;

        initComponents();
        loadData();

        pack();
        setLocationRelativeTo(parent);
        setSize(800, 600);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Вкладка истории бронирований
        JPanel bookingsPanel = createBookingsPanel();
        tabbedPane.addTab("История бронирований", bookingsPanel);

        // Вкладка уборки
        JPanel cleaningsPanel = createCleaningsPanel();
        tabbedPane.addTab("Уборка", cleaningsPanel);

        // Вкладка информации о номере
        JPanel infoPanel = createRoomInfoPanel();
        tabbedPane.addTab("Информация", infoPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Закрыть");
        btnClose.addActionListener(e -> dispose());

        JButton btnAssignCleaning = new JButton("Назначить уборку");
        btnAssignCleaning.addActionListener(e -> {
            RoomCleaningDialog dialog = new RoomCleaningDialog(
                    (JFrame) getParent(), room, roomCleaningDAO, employeeDAO, positionDAO);
            dialog.setVisible(true);
            loadCleaningsData();
        });

        buttonPanel.add(btnAssignCleaning);
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Гость", "Заезд", "Выезд", "Статус", "Стоимость", "Администратор"};
        bookingsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingsTable = new JTable(bookingsTableModel);
        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCleaningsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"Дата", "Уборщик", "Статус", "Примечания"};
        cleaningsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cleaningsTable = new JTable(cleaningsTableModel);
        JScrollPane scrollPane = new JScrollPane(cleaningsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRoomInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Номер:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(new JLabel(room.getRoomNumber()), gbc);

        // ... остальная информация о номере

        return panel;
    }

    private void loadData() {
        loadBookingsData();
        loadCleaningsData();
    }

    private void loadBookingsData() {
        List<Booking> bookings = bookingDAO.getBookingsByRoomId(room.getId());
        bookingsTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        for (Booking booking : bookings) {
            Object[] row = {
                    booking.getGuestSurname() + " " + booking.getGuestName(),
                    dateFormat.format(booking.getCheckInDate()),
                    dateFormat.format(booking.getCheckOutDate()),
                    booking.getStatus(),
                    String.format("%.2f руб.", booking.getTotalPrice()),
                    booking.getAdministratorName() != null ? booking.getAdministratorName() : "Не указан"
            };
            bookingsTableModel.addRow(row);
        }
    }

    private void loadCleaningsData() {
        List<RoomCleaning> cleanings = roomCleaningDAO.getCleaningByRoomId(room.getId());
        cleaningsTableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        for (RoomCleaning cleaning : cleanings) {
            Object[] row = {
                    dateFormat.format(cleaning.getCleaningDate()),
                    cleaning.getEmployeeName(),
                    formatCleaningStatus(cleaning.getStatus()),
                    cleaning.getNotes()
            };
            cleaningsTableModel.addRow(row);
        }
    }

    private String formatCleaningStatus(String status) {
        if (status == null) return "";

        switch (status) {
            case "Назначена":
                return "<html><font color='blue'>" + status + "</font></html>";
            case "Выполнена":
                return "<html><font color='green'>" + status + "</font></html>";
            case "Отменена":
                return "<html><font color='red'>" + status + "</font></html>";
            default:
                return status;
        }
    }
}