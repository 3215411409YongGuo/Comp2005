package com.api.models;

/**
 * Represents an Employee entity from the Maternity Web-Service API.
 */
public class Employee {
    private int id;
    private String surname;
    private String forename;

    // Default constructor for Jackson
    public Employee() {
    }

    public Employee(int id, String surname, String forename) {
        this.id = id;
        this.surname = surname;
        this.forename = forename;
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
    
    @Override
    public String toString() {
        return id + ": " + forename + " " + surname;
    }
} 