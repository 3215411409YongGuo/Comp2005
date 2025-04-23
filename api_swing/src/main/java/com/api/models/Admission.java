package com.api.models;

/**
 * Represents an Admission entity from the Maternity Web-Service API.
 */
public class Admission {
    private int id;
    private String admissionDate;
    private String dischargeDate;
    private int patientID;

    // Default constructor for Jackson
    public Admission() {
    }

    public Admission(int id, String admissionDate, String dischargeDate, int patientID) {
        this.id = id;
        this.admissionDate = admissionDate;
        this.dischargeDate = dischargeDate;
        this.patientID = patientID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getPatientID() {
        return patientID;
    }

    public void setPatientID(int patientID) {
        this.patientID = patientID;
    }
    
    @Override
    public String toString() {
        return "Admission #" + id + " (Patient ID: " + patientID + ")";
    }
} 