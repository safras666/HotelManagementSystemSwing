package com.hotel.ui;

import com.hotel.dao.RoomDAO;
import com.hotel.entity.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RoomDialog extends JDialog {
    private JTextField roomNumberField;
    private JComboBox<String> roomTypeCombo;
    private JSpinner floorSpinner;
    private JComboBox<String> statusCombo;
    private JTextField priceField;
    private JSpinner capacitySpinner;
    private JTextArea descriptionArea;

    private JButton saveButton;
    private JButton cancelButton;

    private RoomDAO roomDAO;
    private boolean isEditMode = false;
    private Room editingRoom;
    private MainWindow mainWindow;
    private boolean saved = false;

    public RoomDialog(MainWindow parent, RoomDAO roomDAO) {
        super(parent, "Добавление номера", true);
        this.mainWindow = parent;
        this.roomDAO = roomDAO;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 550);
    }

    public RoomDialog(MainWindow parent, RoomDAO roomDAO, Room room) {
        super(parent, "Редактирование номера", true);
        this.mainWindow = parent;
        this.roomDAO = roomDAO;
        this.editingRoom = room;
        this.isEditMode = true;
        initComponents();
        fillRoomData();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 550);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Основная панель с полями
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Номер комнаты
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel roomNumberLabel = new JLabel("Номер комнаты*:");
        roomNumberLabel.setPreferredSize(new Dimension(150, 25));
        roomNumberLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(roomNumberLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        roomNumberField = new JTextField(15);
        roomNumberField.setPreferredSize(new Dimension(200, 30));
        fieldsPanel.add(roomNumberField, gbc);
        row++;

        // Тип номера
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel roomTypeLabel = new JLabel("Тип номера*:");
        roomTypeLabel.setPreferredSize(new Dimension(150, 25));
        roomTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(roomTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] roomTypes = {"Стандарт", "Полулюкс", "Люкс", "Бизнес", "Семейный", "Президентский"};
        roomTypeCombo = new JComboBox<>(roomTypes);
        roomTypeCombo.setPreferredSize(new Dimension(200, 30));
        fieldsPanel.add(roomTypeCombo, gbc);
        row++;

        // Этаж
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel floorLabel = new JLabel("Этаж*:");
        floorLabel.setPreferredSize(new Dimension(150, 25));
        floorLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(floorLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel floorModel = new SpinnerNumberModel(1, 1, 20, 1);
        floorSpinner = new JSpinner(floorModel);
        floorSpinner.setPreferredSize(new Dimension(100, 30));
        fieldsPanel.add(floorSpinner, gbc);
        row++;

        // Статус
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel statusLabel = new JLabel("Статус*:");
        statusLabel.setPreferredSize(new Dimension(150, 25));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(statusLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        String[] statuses = {"Свободен", "Занят", "На ремонте", "Забронирован"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setPreferredSize(new Dimension(200, 30));
        fieldsPanel.add(statusCombo, gbc);
        row++;

        // Цена
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel priceLabel = new JLabel("Цена за сутки*:");
        priceLabel.setPreferredSize(new Dimension(150, 25));
        priceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        priceField = new JTextField(15);
        priceField.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(priceField, gbc);

        // Рублей
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("руб."), gbc);
        row++;

        // Вместимость
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel capacityLabel = new JLabel("Вместимость*:");
        capacityLabel.setPreferredSize(new Dimension(150, 25));
        capacityLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        SpinnerNumberModel capacityModel = new SpinnerNumberModel(1, 1, 10, 1);
        capacitySpinner = new JSpinner(capacityModel);
        capacitySpinner.setPreferredSize(new Dimension(100, 30));
        fieldsPanel.add(capacitySpinner, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0.0;
        fieldsPanel.add(new JLabel("человек"), gbc);
        row++;

        // Описание
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel descriptionLabel = new JLabel("Описание:");
        descriptionLabel.setPreferredSize(new Dimension(150, 25));
        descriptionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(descriptionLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(300, 80));
        fieldsPanel.add(descriptionScroll, gbc);

        // Добавляем отступ снизу
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        fieldsPanel.add(Box.createVerticalStrut(20), gbc);

        // Добавляем панель с полями в центр
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.add(fieldsPanel, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        saveButton = new JButton("Сохранить");
        cancelButton = new JButton("Отмена");

        saveButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.setPreferredSize(new Dimension(120, 35));

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveRoom();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Настройка клавиш
        getRootPane().setDefaultButton(saveButton);

        // Минимальный размер диалога
        setMinimumSize(new Dimension(500, 500));
    }

    private void fillRoomData() {
        if (editingRoom != null) {
            roomNumberField.setText(editingRoom.getRoomNumber());
            roomTypeCombo.setSelectedItem(editingRoom.getRoomType());
            floorSpinner.setValue(editingRoom.getFloor());
            statusCombo.setSelectedItem(editingRoom.getStatus());
            priceField.setText(String.valueOf(editingRoom.getPrice()));
            capacitySpinner.setValue(editingRoom.getCapacity());
            descriptionArea.setText(editingRoom.getDescription());
        }
    }

    private void saveRoom() {
        // Проверка обязательных полей
        if (roomNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите номер комнаты", "Ошибка", JOptionPane.ERROR_MESSAGE);
            roomNumberField.requestFocus();
            return;
        }

        if (priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите цену", "Ошибка", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }

        // Проверка цены
        double price;
        try {
            String priceText = priceField.getText().trim().replaceAll("[^\\d]", "");
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Цена должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                priceField.requestFocus();
                return;
            }
            // Округляем до целого
            price = Math.round(price);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат цены. Введите целое число", "Ошибка", JOptionPane.ERROR_MESSAGE);
            priceField.requestFocus();
            return;
        }
        priceField.setText(String.valueOf((int) editingRoom.getPrice()));
        try {
            Room room = new Room();
            room.setRoomNumber(roomNumberField.getText().trim());
            room.setRoomType((String) roomTypeCombo.getSelectedItem());
            room.setFloor((Integer) floorSpinner.getValue());
            room.setStatus((String) statusCombo.getSelectedItem());
            room.setPrice(price);
            room.setCapacity((Integer) capacitySpinner.getValue());
            room.setDescription(descriptionArea.getText().trim());

            // Проверяем, существует ли уже номер с таким номером (кроме редактируемого)
            Room existingRoom = roomDAO.getRoomByNumber(room.getRoomNumber());
            if (existingRoom != null) {
                if (isEditMode && editingRoom != null) {
                    // При редактировании проверяем, что это не тот же самый номер
                    if (existingRoom.getId() != editingRoom.getId()) {
                        JOptionPane.showMessageDialog(this,
                                "Номер с таким номером уже существует",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                        roomNumberField.requestFocus();
                        return;
                    }
                } else {
                    // При добавлении нового - номер должен быть уникальным
                    JOptionPane.showMessageDialog(this,
                            "Номер с таким номером уже существует",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    roomNumberField.requestFocus();
                    return;
                }
            }

            if (isEditMode && editingRoom != null) {
                room.setId(editingRoom.getId());
                roomDAO.updateRoom(room);
                JOptionPane.showMessageDialog(this, "Номер успешно обновлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                roomDAO.addRoom(room);
                JOptionPane.showMessageDialog(this, "Номер успешно добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }

            saved = true;

            // Обновляем таблицу в главном окне
            if (mainWindow != null) {
                mainWindow.refreshRoomsTable();
            }

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }
}