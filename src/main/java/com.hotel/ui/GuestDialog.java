package com.hotel.ui;

import com.hotel.dao.GuestDAO;
import com.hotel.entity.Guest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GuestDialog extends JDialog {
    private JTextField surnameField;
    private JTextField nameField;
    private JTextField patronymicField;
    private JTextField passportSeriesField;
    private JTextField passportNumberField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField birthDateField;
    private JTextField addressField;

    private JButton saveButton;
    private JButton cancelButton;

    private GuestDAO guestDAO;
    private boolean isEditMode = false;
    private Guest editingGuest;
    private MainWindow mainWindow;
    private boolean saved = false;

    public GuestDialog(MainWindow parent, GuestDAO guestDAO) {
        super(parent, "Добавление гостя", true);
        this.mainWindow = parent;
        this.guestDAO = guestDAO;
        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    public GuestDialog(MainWindow parent, GuestDAO guestDAO, Guest guest) {
        super(parent, "Редактирование гостя", true);
        this.mainWindow = parent;
        this.guestDAO = guestDAO;
        this.editingGuest = guest;
        this.isEditMode = true;
        initComponents();
        fillGuestData();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Основная панель с полями
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Фамилия
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel surnameLabel = new JLabel("Фамилия*:");
        surnameLabel.setPreferredSize(new Dimension(180, 25));
        surnameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(surnameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        surnameField = new JTextField(20);
        surnameField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(surnameField, gbc);
        row++;

        // Имя
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel nameLabel = new JLabel("Имя*:");
        nameLabel.setPreferredSize(new Dimension(180, 25));
        nameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(nameField, gbc);
        row++;

        // Отчество
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel patronymicLabel = new JLabel("Отчество:");
        patronymicLabel.setPreferredSize(new Dimension(180, 25));
        patronymicLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(patronymicLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        patronymicField = new JTextField(20);
        patronymicField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(patronymicField, gbc);
        row++;

        // Паспорт (серия и номер)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel passportLabel = new JLabel("Паспорт:");
        passportLabel.setPreferredSize(new Dimension(180, 25));
        passportLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(passportLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;

        JPanel passportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        passportSeriesField = new JTextField(4);
        passportSeriesField.setPreferredSize(new Dimension(60, 30));
        passportPanel.add(passportSeriesField);

        passportPanel.add(new JLabel("Серия"));

        passportNumberField = new JTextField(10);
        passportNumberField.setPreferredSize(new Dimension(120, 30));
        passportPanel.add(passportNumberField);

        passportPanel.add(new JLabel("Номер*"));
        fieldsPanel.add(passportPanel, gbc);
        row++;

        // Телефон
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel phoneLabel = new JLabel("Телефон:");
        phoneLabel.setPreferredSize(new Dimension(180, 25));
        phoneLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        phoneField = new JTextField(20);
        phoneField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(phoneField, gbc);
        row++;

        // Email
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setPreferredSize(new Dimension(180, 25));
        emailLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(emailLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        emailField = new JTextField(20);
        emailField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(emailField, gbc);
        row++;

        // Дата рождения
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel birthDateLabel = new JLabel("Дата рождения* (ДД.ММ.ГГГГ):");
        birthDateLabel.setPreferredSize(new Dimension(180, 25));
        birthDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(birthDateLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        birthDateField = new JTextField(20);
        birthDateField.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(birthDateField, gbc);
        row++;

        // Адрес
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        JLabel addressLabel = new JLabel("Адрес:");
        addressLabel.setPreferredSize(new Dimension(180, 25));
        addressLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        fieldsPanel.add(addressLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        addressField = new JTextField(20);
        addressField.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(addressField, gbc);

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
                saveGuest();
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
        setMinimumSize(new Dimension(600, 500));
    }

    private void fillGuestData() {
        if (editingGuest != null) {
            surnameField.setText(editingGuest.getMiddleName());
            nameField.setText(editingGuest.getName());
            patronymicField.setText(editingGuest.getLastName());
            passportSeriesField.setText(editingGuest.getPassportSeries());
            passportNumberField.setText(editingGuest.getPassportNumber());
            phoneField.setText(editingGuest.getPhoneNumber());
            emailField.setText(editingGuest.getEmail());

            if (editingGuest.getDateOfBirth() != null) {
                birthDateField.setText(new SimpleDateFormat("dd.MM.yyyy").format(editingGuest.getDateOfBirth()));
            }

            addressField.setText(editingGuest.getAddress());
        }
    }

    private void saveGuest() {
        // Проверка обязательных полей
        if (surnameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите фамилию", "Ошибка", JOptionPane.ERROR_MESSAGE);
            surnameField.requestFocus();
            return;
        }

        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return;
        }

        if (passportNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Пожалуйста, введите номер паспорта", "Ошибка", JOptionPane.ERROR_MESSAGE);
            passportNumberField.requestFocus();
            return;
        }

        // Проверка даты рождения
        Date birthDate = null;
        try {
            if (!birthDateField.getText().trim().isEmpty()) {
                birthDate = new SimpleDateFormat("dd.MM.yyyy").parse(birthDateField.getText().trim());
            } else {
                JOptionPane.showMessageDialog(this, "Пожалуйста, введите дату рождения", "Ошибка", JOptionPane.ERROR_MESSAGE);
                birthDateField.requestFocus();
                return;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте ДД.ММ.ГГГГ", "Ошибка", JOptionPane.ERROR_MESSAGE);
            birthDateField.requestFocus();
            return;
        }

        try {
            Guest guest = new Guest();
            guest.setMiddleName(surnameField.getText().trim());
            guest.setName(nameField.getText().trim());
            guest.setLastName(patronymicField.getText().trim());
            guest.setPassportSeries(passportSeriesField.getText().trim());
            guest.setPassportNumber(passportNumberField.getText().trim());
            guest.setPhoneNumber(phoneField.getText().trim());
            guest.setEmail(emailField.getText().trim());
            guest.setDateOfBirth(birthDate);
            guest.setAddress(addressField.getText().trim());

            if (isEditMode && editingGuest != null) {
                guest.setGuestId(editingGuest.getGuestId());
                guestDAO.updateGuest(guest); // Исправлено: updateGuest вместо update
                JOptionPane.showMessageDialog(this, "Гость успешно обновлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                guestDAO.addGuest(guest); // Исправлено: addGuest вместо save
                JOptionPane.showMessageDialog(this, "Гость успешно добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }

            saved = true;

            // Обновляем таблицу в главном окне
            if (mainWindow != null) {
                mainWindow.refreshGuestsTable();
            }

            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Геттеры для получения данных (если нужно в MainWindow)
    public String getSurname() {
        return surnameField.getText();
    }

    public String getName() {
        return nameField.getText();
    }

    public boolean isSaved() {
        return saved;
    }
}