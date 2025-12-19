package com.hotel.entity;

import java.util.Date;

public class RoomCleaning {
    private int id;
    private int roomId;
    private int employeeId;
    private Date cleaningDate;
    private String status;
    private String notes;
    private Date createdAt;

    // Дополнительные поля для отображения
    private String roomNumber;
    private String employeeName;

    public RoomCleaning() {}

    public RoomCleaning(int roomId, int employeeId, Date cleaningDate) {
        this.roomId = roomId;
        this.employeeId = employeeId;
        this.cleaningDate = cleaningDate;
        this.status = "Назначена";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    public Date getCleaningDate() { return cleaningDate; }
    public void setCleaningDate(Date cleaningDate) { this.cleaningDate = cleaningDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
}