package com.hotel.ui;

import javax.swing.*;
import java.awt.*;

public class RoomDialog extends JDialog {
    private JTextField txtRoomNumber;
    private JTextField txtType;
    private JTextField txtFloor;
    private JTextField txtPrice;
    private JComboBox<String> cmbStatus;

    public RoomDialog(JFrame parent) {
        super(parent, "Добавить номер", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        createForm();
    }

    private void createForm() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("Номер комнаты:"));
        txtRoomNumber = new JTextField();
        panel.add(txtRoomNumber);

        panel.add(new JLabel("Тип:"));
        txtType = new JTextField();
        panel.add(txtType);

        panel.add(new JLabel("Этаж:"));
        txtFloor = new JTextField();
        panel.add(txtFloor);

        panel.add(new JLabel("Цена за день:"));
        txtPrice = new JTextField();
        panel.add(txtPrice);

        panel.add(new JLabel("Статус:"));
        cmbStatus = new JComboBox<>(new String[]{"available", "occupied", "maintenance"});
        panel.add(cmbStatus);

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}