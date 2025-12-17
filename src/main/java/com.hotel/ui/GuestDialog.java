package com.hotel.ui;

import javax.swing.*;
import java.awt.*;

public class GuestDialog extends JDialog {
    private boolean saved = false;

    // Поля формы
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtMiddleName;
    private JTextField txtPassportSeries;
    private JTextField txtPassportNumber;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtBirthDate;
    private JTextArea txtAddress;

    public GuestDialog(JFrame parent) {
        super(parent, "Добавление гостя", true);

        // Настройка диалога
        setSize(500, 500);
        setLocationRelativeTo(parent);

        // Создание формы
        createForm();

        // Показать диалог
        setVisible(true);
    }

    private void createForm() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель формы
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Фамилия
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Фамилия*:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtLastName = new JTextField(20);
        formPanel.add(txtLastName, gbc);

        row++;

        // Имя
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Имя*:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtFirstName = new JTextField(20);
        formPanel.add(txtFirstName, gbc);

        row++;

        // Отчество
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Отчество:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 2;
        txtMiddleName = new JTextField(20);
        formPanel.add(txtMiddleName, gbc);

        row++;

        // Паспорт (серия)
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Паспорт (серия):"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 1;
        txtPassportSeries = new JTextField(4);
        formPanel.add(txtPassportSeries, gbc);

        // Паспорт (номер)
        gbc.gridx = 2;
        formPanel.add(new JLabel("Номер*:"), gbc);

        gbc.gridx = 3; gbc.gridwidth = 1;
        txtPassportNumber = new JTextField(10);
        formPanel.add(txtPassportNumber, gbc);

        row++;

        // Телефон
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        formPanel.add(new JLabel("Телефон:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);

        row++;

        // Email
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);

        row++;

        // Дата рождения
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Дата рождения* (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        txtBirthDate = new JTextField(10);
        formPanel.add(txtBirthDate, gbc);

        row++;

        // Адрес
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Адрес:"), gbc);

        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(txtAddress);
        formPanel.add(scrollPane, gbc);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            if (validateForm()) {
                saved = true;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            saved = false;
            dispose();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private boolean validateForm() {
        if (txtLastName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите фамилию", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtFirstName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtPassportNumber.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Введите номер паспорта", "Ошибка", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public boolean isSaved() {
        return saved;
    }

    // Геттеры для получения данных
    public String getLastName() { return txtLastName.getText().trim(); }
    public String getFirstName() { return txtFirstName.getText().trim(); }
    public String getMiddleName() { return txtMiddleName.getText().trim(); }
    public String getPassportSeries() { return txtPassportSeries.getText().trim(); }
    public String getPassportNumber() { return txtPassportNumber.getText().trim(); }
    public String getPhone() { return txtPhone.getText().trim(); }
    public String getEmail() { return txtEmail.getText().trim(); }
    public String getBirthDate() { return txtBirthDate.getText().trim(); }
    public String getAddress() { return txtAddress.getText().trim(); }
}