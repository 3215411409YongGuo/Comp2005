package com.api.models;

/**
 * Represents an Allocation entity from the Maternity Web-Service API.
 */
public class Allocation {
    private int id;
    private int admissionID;
    private int employeeID;
    private String startTime;
    private String endTime;

    // Default constructor for Jackson
    public Allocation() {
    }

    public Allocation(int id, int admissionID, int employeeID, String startTime, String endTime) {
        this.id = id;
        this.admissionID = admissionID;
        this.employeeID = employeeID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAdmissionID() {
        return admissionID;
    }

    public void setAdmissionID(int admissionID) {
        this.admissionID = admissionID;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(int employeeID) {
        this.employeeID = employeeID;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public String toString() {
        return "Allocation #" + id + " (Admission: " + admissionID + ", Employee: " + employeeID + ")";
    }
} 