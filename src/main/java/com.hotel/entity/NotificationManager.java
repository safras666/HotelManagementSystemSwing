package com.hotel.util;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class NotificationManager {

    public static void showBookingReminder(Component parent, List<Object[]> upcomingBookings) {
        if (!upcomingBookings.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("<html><b>Предстоящие заселения сегодня:</b><br><br>");

            for (Object[] booking : upcomingBookings) {
                message.append("• Бронирование ID: ").append(booking[0])
                        .append(", Гость: ").append(booking[1])
                        .append(", Номер: ").append(booking[2])
                        .append("<br>");
            }
            message.append("</html>");

            JOptionPane.showMessageDialog(parent, message.toString(),
                    "Напоминание", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static void showCheckoutReminder(Component parent, List<Object[]> checkoutsToday) {
        if (!checkoutsToday.isEmpty()) {
            StringBuilder message = new StringBuilder();
            message.append("<html><b>Сегодня выезжают:</b><br><br>");

            for (Object[] checkout : checkoutsToday) {
                message.append("• Бронирование ID: ").append(checkout[0])
                        .append(", Гость: ").append(checkout[1])
                        .append(", Номер: ").append(checkout[2])
                        .append("<br>");
            }
            message.append("</html>");

            JOptionPane.showMessageDialog(parent, message.toString(),
                    "Напоминание о выезде", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}