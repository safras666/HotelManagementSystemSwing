package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.EmployeeDAO; // Импортируем DAO сотрудников
import com.hotel.entity.Booking;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;
import com.hotel.entity.Employee; // Импортируем Employee
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class BookingDialog extends JDialog {
    private BookingDAO bookingDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private EmployeeDAO employeeDAO; // Добавляем DAO сотрудников
    private Booking booking;
    private boolean isEditMode;

    private JComboBox<Guest> cbGuest;
    private JComboBox<Room> cbRoom;
    private JComboBox<Employee> cbEmployee; // Новый ComboBox для сотрудников
    private JDateChooser dcCheckIn;
    private JDateChooser dcCheckOut;
    private JTextField txtTotalPrice;
    private JComboBox<String> cbStatus;

    public BookingDialog(JFrame parent, BookingDAO bookingDAO,
                         GuestDAO guestDAO, RoomDAO roomDAO,
                         EmployeeDAO employeeDAO) { // Обновленный конструктор
        this(parent, bookingDAO, guestDAO, roomDAO, employeeDAO, null);
    }

    public BookingDialog(JFrame parent, BookingDAO bookingDAO,
                         GuestDAO guestDAO, RoomDAO roomDAO,
                         EmployeeDAO employeeDAO, Booking booking) { // Обновленный конструктор
        super(parent, true);
        this.bookingDAO = bookingDAO;
        this.guestDAO = guestDAO;
        this.roomDAO = roomDAO;
        this.employeeDAO = employeeDAO; // Инициализируем
        this.booking = booking;
        this.isEditMode = booking != null;

        initComponents();
        setTitle(isEditMode ? "Редактирование бронирования" : "Новое бронирование");
        setSize(550, 500); // Увеличили размер для нового поля
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Форма
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        int row = 0;

        // Гость
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Гость*:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;

        List<Guest> guests = guestDAO.getAllGuests();
        cbGuest = new JComboBox<>(guests.toArray(new Guest[0]));
        cbGuest.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Guest) {
                    Guest guest = (Guest) value;
                    setText(guest.getLastName() + " " + guest.getName() +
                            " (" + guest.getPassportNumber() + ")");
                }
                return this;
            }
        });
        formPanel.add(cbGuest, gbc);
        row++;

        // Номер
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Номер*:"), gbc);
        gbc.gridx = 1;

        List<Room> availableRooms = roomDAO.getAvailableRooms();
        cbRoom = new JComboBox<>(availableRooms.toArray(new Room[0]));
        cbRoom.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room room = (Room) value;
                    setText(room.getRoomNumber() + " - " + room.getRoomType() +
                            " (" + room.getPrice() + " руб./сут)");
                }
                return this;
            }
        });
        formPanel.add(cbRoom, gbc);
        row++;

        // СОТРУДНИК (НОВОЕ ПОЛЕ) - ТОЛЬКО АДМИНИСТРАТОРЫ
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Сотрудник* (администратор):"), gbc); // Измененная подпись
        gbc.gridx = 1;

        // Получаем активных сотрудников
        List<Employee> employees = employeeDAO.getEmployeesByStatus("Работает");
        List<Employee> administrators = employeeDAO.getEmployeesByPosition("Administrator");
        // Если нет администраторов, показываем сообщение
        if (administrators.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "В системе нет сотрудников с должностью 'Administrator'.\n" +
                            "Добавьте администратора через меню сотрудников.",
                    "Внимание", JOptionPane.WARNING_MESSAGE);
        }

        cbEmployee = new JComboBox<>(administrators.toArray(new Employee[0]));
        cbEmployee.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Employee) {
                    Employee emp = (Employee) value;
                    setText(emp.getLastName() + " " + emp.getFirstName() +
                            " (" + emp.getPositionName() + ")");
                }
                return this;
            }
        });
        formPanel.add(cbEmployee, gbc);
        row++;

        // Дата заезда
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата заезда*:"), gbc);
        gbc.gridx = 1;
        dcCheckIn = new JDateChooser();
        dcCheckIn.setDate(new Date());
        formPanel.add(dcCheckIn, gbc);
        row++;

        // Дата выезда
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Дата выезда*:"), gbc);
        gbc.gridx = 1;
        dcCheckOut = new JDateChooser();
        dcCheckOut.setDate(new Date(System.currentTimeMillis() + 86400000)); // Завтра
        formPanel.add(dcCheckOut, gbc);
        row++;

        // Стоимость
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Общая стоимость:"), gbc);
        gbc.gridx = 1;
        txtTotalPrice = new JTextField(10);
        // Автоматический расчет стоимости при выборе номера
        cbRoom.addActionListener(e -> calculateTotalPrice());
        dcCheckIn.addPropertyChangeListener(e -> calculateTotalPrice());
        dcCheckOut.addPropertyChangeListener(e -> calculateTotalPrice());
        formPanel.add(txtTotalPrice, gbc);
        row++;

        // Статус
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Статус:"), gbc);
        gbc.gridx = 1;
        cbStatus = new JComboBox<>(new String[]{"Забронирован", "Заселен", "Выселен", "Отменен"});
        cbStatus.setSelectedItem("Забронирован"); // Значение по умолчанию
        formPanel.add(cbStatus, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("Сохранить");
        JButton btnCancel = new JButton("Отмена");

        btnSave.addActionListener(e -> saveBooking());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Если режим редактирования - загружаем данные
        if (isEditMode) {
            loadBookingData();
        }

        // Рассчитать стоимость при загрузке
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        try {
            Room selectedRoom = (Room) cbRoom.getSelectedItem();
            Date checkIn = dcCheckIn.getDate();
            Date checkOut = dcCheckOut.getDate();

            if (selectedRoom != null && checkIn != null && checkOut != null) {
                // Рассчитываем количество дней
                long diffInMillis = checkOut.getTime() - checkIn.getTime();
                long days = diffInMillis / (1000 * 60 * 60 * 24);

                // Минимум 1 день
                if (days <= 0) {
                    days = 1;
                }

                // Рассчитываем общую стоимость как целое число
                int totalPrice = (int) (selectedRoom.getPrice() * days);

                // Убедимся, что цена не равна 0
                if (totalPrice <= 0) {
                    totalPrice = (int) selectedRoom.getPrice();
                }

                txtTotalPrice.setText(String.valueOf(totalPrice));
            }
        } catch (Exception e) {
            txtTotalPrice.setText("");
        }
    }

    private void loadBookingData() {
        if (booking != null) {
            // Установить гостя
            List<Guest> guests = guestDAO.getAllGuests();
            for (int i = 0; i < guests.size(); i++) {
                if (guests.get(i).getGuestId() == booking.getGuestId()) {
                    cbGuest.setSelectedIndex(i);
                    break;
                }
            }

            // Установить номер
            List<Room> rooms = roomDAO.getAllRooms();
            for (int i = 0; i < rooms.size(); i++) {
                if (rooms.get(i).getId() == booking.getRoomId()) {
                    cbRoom.setSelectedIndex(i);
                    break;
                }
            }

            // Установить сотрудника (только из администраторов)
            List<Employee> administrators = employeeDAO.getEmployeesByPosition("Administrator");
            for (int i = 0; i < administrators.size(); i++) {
                if (administrators.get(i).getId() == booking.getEmployeeId()) {
                    cbEmployee.setSelectedIndex(i);
                    break;
                }
            }

            // Если сотрудник не найден в списке администраторов
            if (cbEmployee.getSelectedItem() == null && administrators.size() > 0) {
                cbEmployee.setSelectedIndex(0);
                JOptionPane.showMessageDialog(this,
                        "Предыдущий сотрудник не является администратором.\n" +
                                "Установлен первый доступный администратор.",
                        "Внимание", JOptionPane.WARNING_MESSAGE);
            }

            dcCheckIn.setDate(booking.getCheckInDate());
            dcCheckOut.setDate(booking.getCheckOutDate());

            // Отображаем целое число
            txtTotalPrice.setText(String.valueOf((int) booking.getTotalPrice()));

            // Установить статус
            String status = booking.getStatus();
            for (int i = 0; i < cbStatus.getItemCount(); i++) {
                if (cbStatus.getItemAt(i).equals(status)) {
                    cbStatus.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void saveBooking() {
        try {
            Guest selectedGuest = (Guest) cbGuest.getSelectedItem();
            Room selectedRoom = (Room) cbRoom.getSelectedItem();
            Employee selectedEmployee = (Employee) cbEmployee.getSelectedItem();

            if (selectedGuest == null) {
                JOptionPane.showMessageDialog(this, "Выберите гостя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Выберите номер", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (selectedEmployee == null) {
                JOptionPane.showMessageDialog(this, "Выберите сотрудника", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dcCheckIn.getDate() == null || dcCheckOut.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Выберите даты заезда и выезда", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (dcCheckOut.getDate().before(dcCheckIn.getDate())) {
                JOptionPane.showMessageDialog(this, "Дата выезда должна быть позже даты заезда", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int totalPrice;
            try {
                // Удаляем все нецифровые символы (кроме минуса для отрицательных чисел)
                String priceText = txtTotalPrice.getText().trim().replaceAll("[^\\d-]", "");

                totalPrice = Integer.parseInt(priceText);

                if (totalPrice <= 0) {
                    JOptionPane.showMessageDialog(this, "Стоимость должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Введите корректную стоимость (целое число, например: 1500)",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!isEditMode) {
                booking = new Booking();
            }

            booking.setGuestId(selectedGuest.getGuestId());
            booking.setRoomId(selectedRoom.getId());
            booking.setEmployeeId(selectedEmployee.getId());
            booking.setCheckInDate(dcCheckIn.getDate());
            booking.setCheckOutDate(dcCheckOut.getDate());
            booking.setTotalPrice(totalPrice); // Сохраняем как целое число
            booking.setStatus((String) cbStatus.getSelectedItem());

            if (isEditMode) {
                bookingDAO.updateBooking(booking);
                JOptionPane.showMessageDialog(this, "Бронирование обновлено", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } else {
                bookingDAO.addBooking(booking);
                JOptionPane.showMessageDialog(this, "Бронирование создано", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }

            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка при сохранении: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private boolean isAdministrator(Employee employee) {
        return employee != null && "Administrator".equals(employee.getPositionName());
    }
}