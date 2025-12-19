package com.hotel.entity;

import java.util.Date;

public class Employee {
    private int id;
    private String last_name;
    private String first_name;
    private String middle_name;
    private int positionId; // Изменено с String position
    private String phone;
    private String email;
    private String passportSeries;
    private String passportNumber;
    private Date hireDate;
    private double salary;
    private String status;
    private String address;
    private Date birthDate;
    private Date createdAt;

    // Новое поле для отображения (не хранится в БД)
    private String positionName;

    // Конструкторы
    public Employee() {
        this.status = "Работает"; // Значение по умолчанию
    }

    public Employee(String lastName, String firstName, int positionId,
                    String phone, String passportNumber, Date hireDate, double salary) {
        this.last_name = lastName;
        this.first_name = firstName;
        this.positionId = positionId;
        this.phone = phone;
        this.passportNumber = passportNumber;
        this.hireDate = hireDate;
        this.salary = salary;
        this.status = "Работает";
    }

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLastName() { return last_name; }
    public void setLastName(String lastName) { this.last_name = lastName; }

    public String getFirstName() { return first_name; }
    public void setFirstName(String firstName) { this.first_name = firstName; }

    public String getMiddleName() { return middle_name; }
    public void setMiddleName(String middleName) { this.middle_name = middleName; }

    public int getPositionId() { return positionId; }
    public void setPositionId(int positionId) { this.positionId = positionId; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String passportSeries) { this.passportSeries = passportSeries; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public Date getHireDate() { return hireDate; }
    public void setHireDate(Date hireDate) { this.hireDate = hireDate; }

    public double getSalary() { return salary; }
    public void setSalary(double salary) { this.salary = salary; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public String getPositionName() { return positionName; }
    public void setPositionName(String positionName) { this.positionName = positionName; }

    public String getFullName() {
        return last_name + " " + first_name + (middle_name != null ? " " + middle_name : "");
    }

    @Override
    public String toString() {
        return getFullName() + " - " + positionName;
    }
}