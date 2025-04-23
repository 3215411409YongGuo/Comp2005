package com.api.models;

/**
 * Represents a Patient entity from the Maternity Web-Service API.
 */
public class Patient {
    private int id;
    private String surname;
    private String forename;
    private String nhsNumber;

    // Default constructor for Jackson
    public Patient() {
    }

    public Patient(int id, String surname, String forename, String nhsNumber) {
        this.id = id;
        this.surname = surname;
        this.forename = forename;
        this.nhsNumber = nhsNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getNhsNumber() {
        return nhsNumber;
    }

    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }
    
    @Override
    public String toString() {
        return id + ": " + forename + " " + surname;
    }
} 