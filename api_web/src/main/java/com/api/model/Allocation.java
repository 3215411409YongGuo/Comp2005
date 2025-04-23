package com.api.model;

public class Allocation {
    private Integer id;
    private Integer admissionID;
    private Integer employeeID;
    private String startTime;
    private String endTime;
    
    public Allocation() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getAdmissionID() {
        return admissionID;
    }
    
    public void setAdmissionID(Integer admissionID) {
        this.admissionID = admissionID;
    }
    
    public Integer getEmployeeID() {
        return employeeID;
    }
    
    public void setEmployeeID(Integer employeeID) {
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
        return "Allocation{" +
                "id=" + id +
                ", admissionID=" + admissionID +
                ", employeeID=" + employeeID +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
} 