package com.api.model;

import java.time.LocalDateTime;

public class Admission {
    private Integer id;
    private String admissionDate;
    private String dischargeDate;
    private Integer patientID;
    
    public Admission() {
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getAdmissionDate() {
        return admissionDate;
    }
    
    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }
    
    public String getDischargeDate() {
        return dischargeDate;
    }
    
    public void setDischargeDate(String dischargeDate) {
        this.dischargeDate = dischargeDate;
    }
    
    public Integer getPatientID() {
        return patientID;
    }
    
    public void setPatientID(Integer patientID) {
        this.patientID = patientID;
    }
    
    @Override
    public String toString() {
        return "Admission{" +
                "id=" + id +
                ", admissionDate='" + admissionDate + '\'' +
                ", dischargeDate='" + dischargeDate + '\'' +
                ", patientID=" + patientID +
                '}';
    }
} 