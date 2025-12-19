package com.hotel.entity;

public class Room {
    private int id;
    private String roomNumber;
    private String roomType;
    private int floor;
    private String status;
    private double price;
    private int capacity;
    private String description;

    // Конструкторы
    public Room() {}

    public Room(String roomNumber, String roomType, int floor, String status,
                double price, int capacity, String description) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.floor = floor;
        this.status = status;
        this.price = price;
        this.capacity = capacity;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPriceAsInt() {
        return (int) price;
    }

    // Для использования в расчетах
    public int getPriceForCalculation() {
        return (int) Math.round(price); // Округление до ближайшего целого
    }
}