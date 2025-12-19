package com.hotel.ui;

import com.hotel.dao.EmployeeDAO;
import com.hotel.dao.PositionDAO;
import com.hotel.dao.RoomCleaningDAO;
import com.hotel.entity.Employee;
import com.hotel.entity.Room;
import com.hotel.entity.RoomCleaning;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RoomCleaningDialog extends JDialog {
    private Room room;
    private RoomCleaningDAO roomCleaningDAO;
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;

    private JTable cleaningsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Employee> cbCleaner;
    private JDateChooser dcCleaningDate;
    private JTextArea txtNotes;

    public RoomCleaningDialog(JFrame parent, Room room,
                              RoomCleaningDAO roomCleaningDAO,
                              EmployeeDAO employeeDAO,
                              PositionDAO positionDAO) {
        super(parent, "Назначение уборки номера " + room.getRoomNumber(), true);
        this.room = room;
        this.roomCleaningDAO = roomCleaningDAO;
        this.employeeDAO = employeeDAO;
        this.positionDAO = positionDAO;

        initComponents();
        loadData();

        pack();
        setLocationRelativeTo(parent);
        setSize(600, 500);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель добавления новой уборки
        JPanel addPanel = new JPanel(new GridBagLayout());
        addPanel.setBorder(BorderFactory.createTitledBorder("Назначить новую уборку"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        addPanel.add(new JLabel("Уборщик:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;

        // Загружаем уборщиков
        List<Employee> cleaners = employeeDAO.getEmployeesByPosition("Cleaner");
        cbCleaner = new JComboBox<>(cleaners.toArray(new Employee[0]));
        cbCleaner.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Employee) {
                    Employee emp = (Employee) value;
                    setText(emp.getLastName() + " " + emp.getFirstName());
                }
                return this;
            }
        });
        addPanel.add(cbCleaner, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        addPanel.add(new JLabel("Дата уборки:"), gbc);
        gbc.gridx = 1;
        dcCleaningDate = new JDateChooser();
        dcCleaningDate.setDate(new Date());
        addPanel.add(dcCleaningDate, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        addPanel.add(new JLabel("Примечания:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 0.5;
        txtNotes = new JTextArea(3, 20);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(txtNotes);
        addPanel.add(notesScroll, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        JButton btnAssign = new JButton("Назначить уборку");
        btnAssign.addActionListener(e -> assignCleaning());
        addPanel.add(btnAssign, gbc);

        mainPanel.add(addPanel, BorderLayout.NORTH);

        // Панель с таблицей истории уборок
        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("История уборок"));

        String[] columns = {"Дата", "Уборщик", "Статус", "Примечания"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        cleaningsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(cleaningsTable);

        historyPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(historyPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnClose = new JButton("Закрыть");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        List<RoomCleaning> cleanings = roomCleaningDAO.getCleaningByRoomId(room.getId());
        updateCleaningsTable(cleanings);
    }

    private void updateCleaningsTable(List<RoomCleaning> cleanings) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

        for (RoomCleaning cleaning : cleanings) {
            Object[] row = {
                    dateFormat.format(cleaning.getCleaningDate()),
                    cleaning.getEmployeeName(),
                    formatCleaningStatus(cleaning.getStatus()),
                    cleaning.getNotes()
            };
            tableModel.addRow(row);
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

    private void assignCleaning() {
        try {
            Employee selectedCleaner = (Employee) cbCleaner.getSelectedItem();
            if (selectedCleaner == null) {
                JOptionPane.showMessageDialog(this, "Выберите уборщика", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dcCleaningDate.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Выберите дату уборки", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RoomCleaning cleaning = new RoomCleaning();
            cleaning.setRoomId(room.getId());
            cleaning.setEmployeeId(selectedCleaner.getId());
            cleaning.setCleaningDate(dcCleaningDate.getDate());
            cleaning.setStatus("Назначена");
            cleaning.setNotes(txtNotes.getText().trim());

            roomCleaningDAO.addCleaning(cleaning);

            JOptionPane.showMessageDialog(this, "Уборка успешно назначена", "Успех", JOptionPane.INFORMATION_MESSAGE);

            // Очистка полей и обновление таблицы
            txtNotes.setText("");
            loadData();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при назначении уборки: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}