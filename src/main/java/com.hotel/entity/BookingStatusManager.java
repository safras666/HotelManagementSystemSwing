package com.hotel.entity;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Booking;
import com.hotel.entity.Room;

public class BookingStatusManager {
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;

    public BookingStatusManager(BookingDAO bookingDAO, RoomDAO roomDAO) {
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;
    }

    // Отменить бронирование
    public boolean cancelBooking(int bookingId) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                System.err.println("Бронирование не найдено: " + bookingId);
                return false;
            }

            if (!"Забронирован".equals(booking.getStatus())) {
                System.err.println("Нельзя отменить бронирование со статусом: " + booking.getStatus());
                return false;
            }

            // Обновляем статус бронирования
            boolean bookingUpdated = bookingDAO.updateBookingStatus(bookingId, "Отменен");

            if (bookingUpdated) {
                // Синхронизируем статус комнаты
                roomDAO.syncRoomStatusFromBookings(booking.getRoomId());
                System.out.println("Бронирование " + bookingId + " отменено, комната синхронизирована");
                return true;
            }

        } catch (Exception e) {
            System.err.println("Ошибка при отмене бронирования: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Заселить гостя
    public boolean checkInBooking(int bookingId) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                System.err.println("Бронирование не найдено: " + bookingId);
                return false;
            }

            if (!"Забронирован".equals(booking.getStatus())) {
                System.err.println("Нельзя заселить бронирование со статусом: " + booking.getStatus());
                return false;
            }

            // Обновляем статус бронирования
            boolean bookingUpdated = bookingDAO.updateBookingStatus(bookingId, "Заселен");

            if (bookingUpdated) {
                // Обновляем статус комнаты
                roomDAO.updateRoomStatus(booking.getRoomId(), "Занят");
                System.out.println("Гость заселен по бронированию " + bookingId + ", комната занята");
                return true;
            }

        } catch (Exception e) {
            System.err.println("Ошибка при заселении: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Выселить гостя
    public boolean checkOutBooking(int bookingId) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                System.err.println("Бронирование не найдено: " + bookingId);
                return false;
            }

            if (!"Заселен".equals(booking.getStatus())) {
                System.err.println("Нельзя выселить бронирование со статусом: " + booking.getStatus());
                return false;
            }

            // Обновляем статус бронирования
            boolean bookingUpdated = bookingDAO.updateBookingStatus(bookingId, "Выселен");

            if (bookingUpdated) {
                // Синхронизируем статус комнаты
                roomDAO.syncRoomStatusFromBookings(booking.getRoomId());
                System.out.println("Гость выселен по бронированию " + bookingId + ", комната синхронизирована");
                return true;
            }

        } catch (Exception e) {
            System.err.println("Ошибка при выселении: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    // Обновить статус бронирования
    public boolean updateBookingStatus(int bookingId, String newStatus) {
        try {
            Booking booking = bookingDAO.getBookingById(bookingId);
            if (booking == null) {
                return false;
            }

            boolean bookingUpdated = bookingDAO.updateBookingStatus(bookingId, newStatus);

            if (bookingUpdated) {
                // Автоматически обновляем статус комнаты
                switch (newStatus) {
                    case "Забронирован":
                        roomDAO.updateRoomStatus(booking.getRoomId(), "Забронирован");
                        break;
                    case "Заселен":
                        roomDAO.updateRoomStatus(booking.getRoomId(), "Занят");
                        break;
                    case "Выселен":
                    case "Отменен":
                        roomDAO.syncRoomStatusFromBookings(booking.getRoomId());
                        break;
                }
                return true;
            }

        } catch (Exception e) {
            System.err.println("Ошибка при обновлении статуса: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}