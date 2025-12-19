package com.hotel.entity;

import java.util.Date;

public class Position {
    private int id;
    private String name;
    private String description;
    private Date createdAt;

    public Position() {}

    public Position(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return name != null ? name : ""; // Возвращаем название должности
    }
}