package com.hotel.entity;

import java.util.Date;

public class Booking {
    private int id;
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private String status; // "Забронирован", "Заселен", "Выселен", "Отменен"
    private double totalPrice;
    private Date createdAt;
    private Date updatedAt;

    // Дополнительные поля для отображения (не сохраняются в БД)
    private String guestName;
    private String guestSurname;
    private String roomNumber;

    // Конструкторы
    public Booking() {}

    public Booking(int guestId, int roomId, Date checkInDate, Date checkOutDate,
                   String status, double totalPrice) {
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Date getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(Date checkInDate) {
        this.checkInDate = checkInDate;
    }

    public Date getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(Date checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestSurname() {
        return guestSurname;
    }

    public void setGuestSurname(String guestSurname) {
        this.guestSurname = guestSurname;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    // Вспомогательные методы
    public String getGuestFullName() {
        if (guestSurname != null && guestName != null) {
            return guestSurname + " " + guestName;
        }
        return "";
    }
}