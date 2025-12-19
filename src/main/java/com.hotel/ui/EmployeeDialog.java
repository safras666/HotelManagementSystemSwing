package com.hotel.ui;

import com.hotel.dao.EmployeeDAO;
import com.hotel.dao.PositionDAO;
import com.hotel.entity.Employee;
import com.hotel.entity.Position;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EmployeeDialog extends JDialog {
    private EmployeeDAO employeeDAO;
    private PositionDAO positionDAO;
    private Employee employee;
    private boolean isEditMode;

    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtMiddleName;
    private JComboBox<Position> cbPosition;
    private JTextField txtPhone;
    private JTextField txtEmail;
    private JTextField txtPassportSeries;
    private JTextField txtPassportNumber;
    private JDateChooser dcHireDate;
    private JTextField txtSalary;
    private JComboBox<String> cbStatus;
    private JTextArea txtAddress;
    private JDateChooser dcBirthDate;

    public EmployeeDialog(JFrame parent, EmployeeDAO employeeDAO, PositionDAO positionDAO) {
        this(parent, employeeDAO, positionDAO, null);
    }

    public EmployeeDialog(JFrame parent, EmployeeDAO employeeDAO, PositionDAO positionDAO, Employee employee) {
        super(parent, true);
        this.employeeDAO = employeeDAO;
        this.positionDAO = positionDAO;
        this.employee = employee;
        this.isEditMode = employee != null;

        initComponents(); // ДОЛЖЕН БЫТЬ ПЕРВЫМ!
        setTitle(isEditMode ? "Редактирование сотрудника" : "Добавление нового сотрудника");
        setSize(500, 650);
        setLocationRelativeTo(parent);

        if (isEditMode) {
            loadEmployeeData();
        }
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Панель с полями ввода
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Фамилия
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Фамилия*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtLastName = new JTextField(20);
        formPanel.add(txtLastName, gbc);

        // Имя
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Имя*:"), gbc);
        gbc.gridx = 1;
        txtFirstName = new JTextField(20);
        formPanel.add(txtFirstName, gbc);

        // Отчество
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Отчество:"), gbc);
        gbc.gridx = 1;
        txtMiddleName = new JTextField(20);
        formPanel.add(txtMiddleName, gbc);

        // Должность
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Должность*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;

        // Загружаем должности из базы данных
        List<Position> positions = positionDAO.getAllPositions();
        cbPosition = new JComboBox<>(positions.toArray(new Position[0]));

        cbPosition.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Position) {
                    setText(((Position) value).getName());
                }
                return this;
            }
        });

        // Добавляем кастомный рендерер для правильного отображения
        cbPosition.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Position) {
                    setText(((Position) value).getName());
                } else if (value == null) {
                    setText("Выберите должность");
                }
                return this;
            }
        });

        cbPosition.setFont(new Font("Dialog", Font.PLAIN, 12));

        formPanel.add(cbPosition, gbc);


        // Телефон
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Телефон*:"), gbc);
        gbc.gridx = 1;
        txtPhone = new JTextField(20);
        formPanel.add(txtPhone, gbc);

        // Email
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        formPanel.add(txtEmail, gbc);

        // Паспорт (серия и номер)
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Паспорт (серия):"), gbc);
        gbc.gridx = 1;
        txtPassportSeries = new JTextField(5);
        formPanel.add(txtPassportSeries, gbc);

        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Паспорт (номер)*:"), gbc);
        gbc.gridx = 1;
        txtPassportNumber = new JTextField(15);
        formPanel.add(txtPassportNumber, gbc);

        // Дата приема
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата приема*:"), gbc);
        gbc.gridx = 1;
        dcHireDate = new JDateChooser();
        dcHireDate.setDate(new Date());
        formPanel.add(dcHireDate, gbc);

        // Зарплата
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Зарплата*:"), gbc);
        gbc.gridx = 1;
        txtSalary = new JTextField(10);
        formPanel.add(txtSalary, gbc);

        // Статус
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"Работает", "Уволен", "Отпуск"});
        formPanel.add(cbStatus, gbc);

        // Дата рождения
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата рождения:"), gbc);
        gbc.gridx = 1;
        dcBirthDate = new JDateChooser();
        formPanel.add(dcBirthDate, gbc);

        // Адрес
        gbc.gridx = 0; gbc.gridy = ++row; gbc.weightx = 0;
        formPanel.add(new JLabel("Адрес:"), gbc);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        txtAddress = new JTextArea(3, 20);
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(txtAddress);
        formPanel.add(addressScroll, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnCancel = new JButton("Отмена");

        btnSave.addActionListener(e -> saveEmployee());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setFont(new Font("Dialog", Font.PLAIN, 12));
    }


    private void loadEmployeeData() {
        if (employee != null) {
            txtLastName.setText(employee.getLastName());
            txtFirstName.setText(employee.getFirstName());
            txtMiddleName.setText(employee.getMiddleName());

            // Устанавливаем должность
            if (employee.getPositionId() > 0) {
                List<Position> positions = positionDAO.getAllPositions();
                for (int i = 0; i < positions.size(); i++) {
                    Position pos = positions.get(i);
                    if (pos.getId() == employee.getPositionId()) {
                        cbPosition.setSelectedIndex(i);
                        break;
                    }
                }
            }

            txtPhone.setText(employee.getPhone());
            txtEmail.setText(employee.getEmail());
            txtPassportSeries.setText(employee.getPassportSeries());
            txtPassportNumber.setText(employee.getPassportNumber());
            dcHireDate.setDate(employee.getHireDate());
            txtSalary.setText(String.valueOf(employee.getSalary()));

            // Устанавливаем статус
            String status = employee.getStatus();
            if (status != null) {
                cbStatus.setSelectedItem(status);
            }

            txtAddress.setText(employee.getAddress());
            dcBirthDate.setDate(employee.getBirthDate());
        }
    }

    private void saveEmployee() {
        try {
            // Валидация обязательных полей
            if (txtLastName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите фамилию", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtFirstName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите имя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (cbPosition.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Выберите должность", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtPhone.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите телефон", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtPassportNumber.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите номер паспорта", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (dcHireDate.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Выберите дату приема", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (txtSalary.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите зарплату", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Убедимся, что hireDate установлен
            if (dcHireDate.getDate() == null) {
                dcHireDate.setDate(new Date()); // Установить текущую дату по умолчанию
            }

            double salary;
            try {
                salary = Double.parseDouble(txtSalary.getText().trim());
                if (salary <= 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Введите корректную зарплату", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Создание или обновление сотрудника
            if (!isEditMode) {
                employee = new Employee();
            }

            employee.setLastName(txtLastName.getText().trim());
            employee.setFirstName(txtFirstName.getText().trim());
            employee.setMiddleName(txtMiddleName.getText().trim());

            // Получаем ID выбранной должности
            Position selectedPosition = (Position) cbPosition.getSelectedItem();
            employee.setPositionId(selectedPosition.getId());

            employee.setPhone(txtPhone.getText().trim());
            employee.setEmail(txtEmail.getText().trim());
            employee.setPassportSeries(txtPassportSeries.getText().trim());
            employee.setPassportNumber(txtPassportNumber.getText().trim());
            employee.setHireDate(dcHireDate.getDate());
            employee.setSalary(salary);
            employee.setStatus((String) cbStatus.getSelectedItem());
            employee.setAddress(txtAddress.getText().trim());
            employee.setBirthDate(dcBirthDate.getDate());

            if (isEditMode) {
                employeeDAO.updateEmployee(employee);
                JOptionPane.showMessageDialog(this, "Данные сотрудника обновлены", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                employeeDAO.addEmployee(employee);
                JOptionPane.showMessageDialog(this, "Сотрудник добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}