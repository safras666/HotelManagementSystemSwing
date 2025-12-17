package com.hotel.entity;

public class Room {
    private int roomId;
    private String roomNumber;
    private String typeName;
    private int floor;
    private String roomStatus;
    private double pricePerDay;
    private int maxGuests;

    // Конструкторы
    public Room() {}

    public Room(String roomNumber, String typeName, int floor, double price) {
        this.roomNumber = roomNumber;
        this.typeName = typeName;
        this.floor = floor;
        this.pricePerDay = price;
        this.roomStatus = "available";
    }

    // Геттеры и сеттеры


    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public String getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    // Вспомогательный метод
    public String getStatusDisplay() {
        switch (roomStatus) {
            case "available": return "Свободен";
            case "occupied": return "Занят";
            case "reserved": return "Забронирован";
            default: return roomStatus;
        }
    }
}