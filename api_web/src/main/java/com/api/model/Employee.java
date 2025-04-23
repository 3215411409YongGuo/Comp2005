package com.api.model;

public class Employee {
    private Integer id;
    private String surname;
    private String forename;
    
    public Employee() {
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
    
    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", surname='" + surname + '\'' +
                ", forename='" + forename + '\'' +
                '}';
    }
} 