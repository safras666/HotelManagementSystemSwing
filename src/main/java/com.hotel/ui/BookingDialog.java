package com.hotel.ui;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.GuestDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Guest;
import com.hotel.entity.Room;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BookingDialog extends JDialog {
    private JComboBox<Guest> guestCombo;
    private JComboBox<Room> roomCombo;
    private JTextField checkInField;
    private JTextField checkOutField;
    private JComboBox<String> statusCombo;
    private JTextField totalPriceField;

    private JButton saveButton;
    private JButton cancelButton;
    private JButton calculateButton;

    private BookingDAO bookingDAO;
    private GuestDAO guestDAO;
    private RoomDAO roomDAO;
    private boolean isEditMode = false;
    private Booking editingBooking;
    private MainWindow mainWindow;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public BookingDialog(MainWindow parent, BookingDAO bookingDAO,
                         GuestDAO guestDAO, RoomDAO roomDAO) {
        super(parent, "Новое бронирование", true);
        this.mainWindow = parent;
        this.bookingDAO = bookingDAO;
        this.guestDAO = guestDAO;
        this.roomDAO = roomDAO;
        initComponents();
        loadGuests();
        loadAllRooms();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 450);
    }

    public BookingDialog(MainWindow parent, BookingDAO bookingDAO,
                         GuestDAO guestDAO, RoomDAO roomDAO, Booking booking) {
        super(parent, "Редактирование бронирования", true);
        this.mainWindow = parent;
        this.bookingDAO = bookingDAO;
        this.guestDAO = guestDAO;
        this.roomDAO = roomDAO;
        this.editingBooking = booking;
        this.isEditMode = true;
        initComponents();
        loadGuests();
        loadAllRooms();
        fillBookingData();
        pack();
        setLocationRelativeTo(parent);
        setSize(500, 450);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Гость
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Гость*:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        guestCombo = new JComboBox<>();
        guestCombo.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(guestCombo, gbc);
        row++;

        // Номер
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Номер*:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        roomCombo = new JComboBox<>();
        roomCombo.setPreferredSize(new Dimension(250, 30));
        fieldsPanel.add(roomCombo, gbc);
        row++;

        // Дата заезда
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Дата заезда* (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        checkInField = new JTextField(10);
        checkInField.setText(dateFormat.format(new Date()));
        checkInField.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(checkInField, gbc);
        row++;

        // Дата выезда
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Дата выезда* (ДД.ММ.ГГГГ):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        checkOutField = new JTextField(10);
        // Завтрашняя дата
        Date tomorrow = new Date(System.currentTimeMillis() + 86400000L);
        checkOutField.setText(dateFormat.format(tomorrow));
        checkOutField.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(checkOutField, gbc);

        // Кнопка расчета
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        calculateButton = new JButton("Рассчитать");
        calculateButton.setPreferredSize(new Dimension(100, 30));
        fieldsPanel.add(calculateButton, gbc);
        row++;

        // Статус
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Статус*:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        String[] statuses = {"Забронирован", "Заселен", "Выселен", "Отменен"};
        statusCombo = new JComboBox<>(statuses);
        statusCombo.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(statusCombo, gbc);
        row++;

        // Стоимость
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.3;
        fieldsPanel.add(new JLabel("Стоимость (руб.):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        totalPriceField = new JTextField("0");
        totalPriceField.setPreferredSize(new Dimension(150, 30));
        fieldsPanel.add(totalPriceField, gbc);
        row++;

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
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculatePrice();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveBooking();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        getRootPane().setDefaultButton(saveButton);

        // Автоматический расчет при загрузке
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                calculatePrice();
            }
        });
    }

    private void loadGuests() {
        try {
            List<Guest> guests = guestDAO.getAllGuests();
            guestCombo.removeAllItems();
            System.out.println("Загружено гостей: " + guests.size());

            for (Guest guest : guests) {
                guestCombo.addItem(guest);
            }

            guestCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Guest) {
                        Guest guest = (Guest) value;
                        setText(guest.getMiddleName() + " " + guest.getFirstName() +
                                " (ID: " + guest.getGuestId() + ")");
                    }
                    return this;
                }
            });

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке гостей: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadAllRooms() {
        try {
            List<Room> rooms = roomDAO.getAllRooms();
            roomCombo.removeAllItems();
            System.out.println("Загружено номеров: " + rooms.size());

            for (Room room : rooms) {
                roomCombo.addItem(room);
            }

            roomCombo.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(JList<?> list, Object value,
                                                              int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof Room) {
                        Room room = (Room) value;
                        setText(room.getRoomNumber() + " - " + room.getRoomType() +
                                " (" + room.getPrice() + " руб./сут, " + room.getStatus() + ")");
                    }
                    return this;
                }
            });

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке номеров: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fillBookingData() {
        if (editingBooking != null) {
            try {
                // Устанавливаем гостя
                Guest guest = guestDAO.getGuestById(editingBooking.getGuestId());
                if (guest != null) {
                    for (int i = 0; i < guestCombo.getItemCount(); i++) {
                        Guest comboGuest = guestCombo.getItemAt(i);
                        if (comboGuest.getGuestId() == guest.getGuestId()) {
                            guestCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                // Устанавливаем номер
                Room room = roomDAO.getRoomById(editingBooking.getRoomId());
                if (room != null) {
                    for (int i = 0; i < roomCombo.getItemCount(); i++) {
                        Room comboRoom = roomCombo.getItemAt(i);
                        if (comboRoom.getId() == room.getId()) {
                            roomCombo.setSelectedIndex(i);
                            break;
                        }
                    }
                }

                checkInField.setText(dateFormat.format(editingBooking.getCheckInDate()));
                checkOutField.setText(dateFormat.format(editingBooking.getCheckOutDate()));
                statusCombo.setSelectedItem(editingBooking.getStatus());
                totalPriceField.setText(String.format("%.0f", editingBooking.getTotalPrice()));

            } catch (Exception e) {
                System.err.println("Ошибка при заполнении данных бронирования: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void calculatePrice() {
        try {
            if (roomCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите номер для расчета стоимости",
                        "Ошибка", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Room selectedRoom = (Room) roomCombo.getSelectedItem();

            // Парсим даты
            Date checkIn = dateFormat.parse(checkInField.getText());
            Date checkOut = dateFormat.parse(checkOutField.getText());

            // Проверяем, что дата выезда позже даты заезда
            if (!checkOut.after(checkIn)) {
                JOptionPane.showMessageDialog(this,
                        "Дата выезда должна быть позже даты заезда",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Рассчитываем количество ночей
            long diff = checkOut.getTime() - checkIn.getTime();
            long nights = diff / (1000 * 60 * 60 * 24);

            if (nights <= 0) {
                nights = 1;
            }

            // Рассчитываем стоимость (округляем до целого)
            double totalPrice = selectedRoom.getPrice() * nights;
            long roundedPrice = Math.round(totalPrice);

            totalPriceField.setText(String.valueOf(roundedPrice));

        } catch (ParseException e) {
            System.err.println("Неверный формат даты: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Неверный формат даты. Используйте ДД.ММ.ГГГГ",
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            System.err.println("Ошибка при расчете стоимости: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveBooking() {
        try {
            // Проверка выбора гостя
            if (guestCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите гостя", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка выбора номера
            if (roomCombo.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this,
                        "Выберите номер", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Получаем выбранные данные
            Guest selectedGuest = (Guest) guestCombo.getSelectedItem();
            Room selectedRoom = (Room) roomCombo.getSelectedItem();

            // Проверка и парсинг дат
            Date checkIn, checkOut;
            try {
                checkIn = dateFormat.parse(checkInField.getText());
                checkOut = dateFormat.parse(checkOutField.getText());

                if (!checkOut.after(checkIn)) {
                    JOptionPane.showMessageDialog(this,
                            "Дата выезда должна быть позже даты заезда",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (ParseException e) {
                JOptionPane.showMessageDialog(this,
                        "Неверный формат даты. Используйте ДД.ММ.ГГГГ",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка стоимости
            double totalPrice;
            try {
                totalPrice = Double.parseDouble(totalPriceField.getText());

                if (totalPrice <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "Стоимость должна быть больше 0",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Неверный формат стоимости. Используйте целое число",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = (String) statusCombo.getSelectedItem();

            System.out.println("Создание бронирования:");
            System.out.println("Гость ID: " + selectedGuest.getGuestId());
            System.out.println("Номер ID: " + selectedRoom.getId());
            System.out.println("Статус: " + status);
            System.out.println("Стоимость: " + totalPrice);

            // Проверка доступности номера (только для новых бронирований)
            if (!isEditMode) {
                boolean isAvailable = bookingDAO.isRoomAvailable(selectedRoom.getId(), checkIn, checkOut);
                if (!isAvailable) {
                    JOptionPane.showMessageDialog(this,
                            "Номер " + selectedRoom.getRoomNumber() + " недоступен на выбранные даты",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            try {
                // Создаем бронирование
                Booking booking = new Booking();
                booking.setGuestId(selectedGuest.getGuestId());
                booking.setRoomId(selectedRoom.getId());
                booking.setCheckInDate(checkIn);
                booking.setCheckOutDate(checkOut);
                booking.setStatus(status);
                booking.setTotalPrice(totalPrice);

                if (isEditMode && editingBooking != null) {
                    booking.setId(editingBooking.getId());
                    bookingDAO.updateBooking(booking);
                    JOptionPane.showMessageDialog(this,
                            "Бронирование обновлено",
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    bookingDAO.addBooking(booking);

                    // Обновляем статус номера
                    if ("Забронирован".equals(status) || "Заселен".equals(status)) {
                        selectedRoom.setStatus("Занят");
                        roomDAO.updateRoom(selectedRoom);
                    }

                    JOptionPane.showMessageDialog(this,
                            "Бронирование создано успешно! ID: " + booking.getId(),
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                }

                // Обновляем таблицы в главном окне
                if (mainWindow != null) {
                    mainWindow.refreshRoomsTable();
                    mainWindow.refreshBookingsTable();
                    System.out.println("Таблицы обновлены");
                }

                dispose();
                if (isEditMode && editingBooking != null) {
                    booking.setId(editingBooking.getId());
                    bookingDAO.updateBooking(booking);

                    // При редактировании синхронизируем статус комнаты
                    roomDAO.syncRoomStatusFromBookings(booking.getRoomId());

                    JOptionPane.showMessageDialog(this,
                            "Бронирование обновлено",
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    bookingDAO.addBooking(booking);

                    // При создании нового устанавливаем статус комнаты
                    if ("Забронирован".equals(status)) {
                        roomDAO.updateRoomStatus(selectedRoom.getId(), "Забронирован");
                    } else if ("Заселен".equals(status)) {
                        roomDAO.updateRoomStatus(selectedRoom.getId(), "Занят");
                    }

                    JOptionPane.showMessageDialog(this,
                            "Бронирование создано успешно",
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                }

                // Сообщаем MainWindow об обновлении
                if (mainWindow != null) {
                    mainWindow.refreshAllTables();
                }

                dispose();

            } catch (Exception e) {
                System.err.println("Ошибка при сохранении бронирования: " + e.getMessage());
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Ошибка при сохранении: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при сохранении бронирования: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Ошибка при сохранении: " + e.getMessage(),
                    "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}