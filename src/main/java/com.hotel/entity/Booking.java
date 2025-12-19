package com.hotel.entity;

import java.util.Date;

public class Booking {
    private int id;
    private int guestId;
    private int roomId;
    private Date checkInDate;
    private Date checkOutDate;
    private String status;
    private double totalPrice;
    private Date createdAt;

    // Новые поля для отображения
    private String guestSurname;
    private String guestName;
    private String roomNumber;
    private int employeeId; // Новое поле
    private String employeeLastName; // Для отображения
    private String employeeFirstName; // Для отображения

    // Конструкторы, геттеры и сеттеры
    public Booking() {}

    public Booking(int guestId, int roomId, Date checkInDate, Date checkOutDate,
                   String status, double totalPrice, int employeeId) {
        this.guestId = guestId;
        this.roomId = roomId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.status = status;
        this.totalPrice = totalPrice;
        this.employeeId = employeeId;
        this.createdAt = new Date();
    }

    // Геттеры и сеттеры (добавьте новые)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public Date getCheckInDate() { return checkInDate; }
    public void setCheckInDate(Date checkInDate) { this.checkInDate = checkInDate; }

    public Date getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(Date checkOutDate) { this.checkOutDate = checkOutDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getGuestSurname() { return guestSurname; }
    public void setGuestSurname(String guestSurname) { this.guestSurname = guestSurname; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    // Новые геттеры и сеттеры для сотрудника
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public String getEmployeeLastName() { return employeeLastName; }
    public void setEmployeeLastName(String employeeLastName) { this.employeeLastName = employeeLastName; }

    public String getEmployeeFirstName() { return employeeFirstName; }
    public void setEmployeeFirstName(String employeeFirstName) { this.employeeFirstName = employeeFirstName; }

    public String getAdministratorName() {
        if (employeeLastName != null && employeeFirstName != null) {
            return employeeLastName + " " + employeeFirstName;
        }
        return "Не указан";
    }
    // Метод для получения полного имени сотрудника
    public String getEmployeeFullName() {
        if (employeeLastName != null && employeeFirstName != null) {
            return employeeLastName + " " + employeeFirstName;
        }
        return "";
    }

    // Метод для получения стоимости как целого числа
    public int getTotalPriceAsInt() {
        return (int) totalPrice;
    }

    // Метод для установки стоимости как целого числа
    public void setTotalPriceAsInt(int price) {
        this.totalPrice = price;
    }

    // Для обратной совместимости
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}