package com.hotel.util;

import com.hotel.dao.BookingDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.entity.Room; // Добавьте этот импорт
import java.util.List; // Добавьте этот импорт
import java.util.Timer;
import java.util.TimerTask;

public class StatusSynchronizer {
    private BookingDAO bookingDAO;
    private RoomDAO roomDAO;
    private Timer syncTimer;

    public StatusSynchronizer(BookingDAO bookingDAO, RoomDAO roomDAO) {
        this.bookingDAO = bookingDAO;
        this.roomDAO = roomDAO;
    }

    public void startSync() {
        syncTimer = new Timer(true);

        // Синхронизируем каждую минуту
        syncTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                syncAllRoomStatuses();
            }
        }, 0, 60 * 1000); // 1 минута
    }

    public void stopSync() {
        if (syncTimer != null) {
            syncTimer.cancel();
        }
    }

    private void syncAllRoomStatuses() {
        try {
            System.out.println("Начата автоматическая синхронизация статусов комнат...");

            // Получаем все комнаты и синхронизируем их статусы
            List<Room> rooms = roomDAO.getAllRooms();
            for (Room room : rooms) {
                roomDAO.syncRoomStatusFromBookings(room.getId());
            }

            System.out.println("Синхронизация статусов завершена. Обработано комнат: " + rooms.size());

        } catch (Exception e) {
            System.err.println("Ошибка при синхронизации статусов: " + e.getMessage());
        }
    }
}