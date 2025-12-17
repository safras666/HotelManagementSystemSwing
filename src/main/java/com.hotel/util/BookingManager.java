package com.hotel.util;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BookingManager {
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private Timer timer;

    public BookingManager(BookingDAO bookingDAO, RoomDAO roomDAO) {
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;
    }

    public void startAutoCheck() {
        timer = new Timer(true);

        // Проверяем каждые 5 минут
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkOverdueBookings();
                checkCheckoutTime();
            }
        }, 0, 5 * 60 * 1000); // 5 минут
    }

    public void stopAutoCheck() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private void checkOverdueBookings() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            Date now = new Date();

            for (Booking booking : bookings) {
                if ("Забронирован".equals(booking.getStatus())) {
                    // Если дата заезда прошла, а бронирование еще не подтверждено
                    if (booking.getCheckInDate() != null &&
                            booking.getCheckInDate().before(now)) {

                        // Автоматически отменяем просроченное бронирование
                        bookingDAO.updateBookingStatus(booking.getId(), "Отменен");

                        // Освобождаем номер
                        Room room = roomDAO.getRoomById(booking.getRoomId());
                        if (room != null) {
                            room.setStatus("Свободен");
                            roomDAO.updateRoom(room);
                        }

                        System.out.println("Автоматически отменено просроченное бронирование ID: " + booking.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при проверке просроченных бронирований: " + e.getMessage());
        }
    }

    private void checkCheckoutTime() {
        try {
            List<Booking> bookings = bookingDAO.getAllBookings();
            Date now = new Date();

            for (Booking booking : bookings) {
                if ("Заселен".equals(booking.getStatus())) {
                    // Если дата выезда прошла
                    if (booking.getCheckOutDate() != null &&
                            booking.getCheckOutDate().before(now)) {

                        // Автоматически выселяем
                        bookingDAO.updateBookingStatus(booking.getId(), "Выселен");

                        // Освобождаем номер
                        Room room = roomDAO.getRoomById(booking.getRoomId());
                        if (room != null) {
                            room.setStatus("Свободен");
                            roomDAO.updateRoom(room);
                        }

                        System.out.println("Автоматически выселен гость из бронирования ID: " + booking.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка при проверке времени выезда: " + e.getMessage());
        }
    }

    // Метод для расчета продолжительности пребывания
    public static int calculateStayDuration(Date checkIn, Date checkOut) {
        long diff = checkOut.getTime() - checkIn.getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    // Метод для проверки доступности номера
    public boolean isRoomAvailable(int roomId, Date checkIn, Date checkOut, int excludeBookingId) {
        List<Booking> bookings = bookingDAO.getBookingsByRoomId(roomId);

        for (Booking booking : bookings) {
            // Исключаем текущее бронирование при редактировании
            if (booking.getId() == excludeBookingId) {
                continue;
            }

            // Проверяем активные бронирования
            if ("Забронирован".equals(booking.getStatus()) ||
                    "Заселен".equals(booking.getStatus())) {

                // Проверяем пересечение дат
                if (checkIn.before(booking.getCheckOutDate()) &&
                        checkOut.after(booking.getCheckInDate())) {
                    return false;
                }
            }
        }

        return true;
    }
}