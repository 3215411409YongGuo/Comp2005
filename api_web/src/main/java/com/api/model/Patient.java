package com.api.model;

public class Patient {
    private Integer id;
    private String surname;
    private String forename;
    private String nhsNumber;
    
    public Patient() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
        return "Patient{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", forename='" + forename + '\'' +
                ", nhsNumber='" + nhsNumber + '\'' +
                '}';
    }
} 