package com.hotel.entity;

import java.util.Date;

public class Guest {
    private int guestId;
    private String lastName;
    private String firstName;
    private String middleName;
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
        this.lastName = lastName;
        this.firstName = firstName;
        this.passportNumber = passportNumber;
    }

    // Конструктор со всеми параметрами
    public Guest(String lastName, String firstName, String middleName,
                 String passportSeries, String passportNumber, Date dateOfBirth,
                 String phoneNumber, String email, String address) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
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

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

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
        return lastName + " " + firstName + " " + (middleName != null ? middleName : "");
    }
}