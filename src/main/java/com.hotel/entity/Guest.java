package com.hotel.entity;

import java.util.Date;

public class Guest {
    private int guestId;
    private String surname;      // Фамилия
    private String name;         // Имя
    private String patronymic;   // Отчество
    private String passportSeries;
    private String passportNumber;
    private Date dateOfBirth;
    private String phoneNumber;
    private String email;
    private String address;
    private Date createdAt;

    // Конструктор по умолчанию
    public Guest() {}

    // Конструктор с основными параметрами
    public Guest(String lastName, String firstName, String passportNumber) {
        this.surname = lastName;      // Фамилия
        this.name = firstName;        // Имя
        this.passportNumber = passportNumber;
    }

    // Конструктор со всеми параметрами
    public Guest(String lastName, String firstName, String middleName,
                 String passportSeries, String passportNumber, Date dateOfBirth,
                 String phoneNumber, String email, String address) {
        this.surname = lastName;      // Фамилия
        this.name = firstName;        // Имя
        this.patronymic = middleName; // Отчество
        this.passportSeries = passportSeries;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
    }

    // Геттеры и сеттеры
    public int getGuestId() { return guestId; }
    public void setGuestId(int guestId) { this.guestId = guestId; }

    // Фамилия
    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    // Имя
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Отчество
    public String getPatronymic() { return patronymic; }
    public void setPatronymic(String patronymic) { this.patronymic = patronymic; }

    // Для обратной совместимости (если где-то используются старые методы)
    public String getMiddleName() { return patronymic; }
    public void setMiddleName(String middleName) { this.patronymic = middleName; }

    public String getLastName() { return surname; }
    public void setLastName(String lastName) { this.surname = lastName; }

    public String getPassportSeries() { return passportSeries; }
    public void setPassportSeries(String passportSeries) { this.passportSeries = passportSeries; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return surname + " " + name + " " + (patronymic != null ? patronymic : "");
    }
}