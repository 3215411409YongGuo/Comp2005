package com.api.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Admission class.
 */
public class AdmissionTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int id = 1;
        String admissionDate = "2025-04-10";
        String dischargeDate = "2025-04-15";
        int patientID = 100;

        // Act
        Admission admission = new Admission(id, admissionDate, dischargeDate, patientID);

        // Assert
        assertEquals(id, admission.getId());
        assertEquals(admissionDate, admission.getAdmissionDate());
        assertEquals(dischargeDate, admission.getDischargeDate());
        assertEquals(patientID, admission.getPatientID());
    }

    @Test
    public void testSetters() {
        // Arrange
        Admission admission = new Admission();

        // Act
        admission.setId(1);
        admission.setAdmissionDate("2025-04-05");
        admission.setDischargeDate("2025-04-12");
        admission.setPatientID(100);

        // Assert
        assertEquals(1, admission.getId());
        assertEquals("2025-04-05", admission.getAdmissionDate());
        assertEquals("2025-04-12", admission.getDischargeDate());
        assertEquals(100, admission.getPatientID());
    }

    @Test
    public void testToString() {
        // Arrange
        Admission admission = new Admission(1, "2025-04-01", "2025-04-08", 100);

        // Act
        String result = admission.toString();

        // Assert
        assertEquals("Admission #1 (Patient ID: 100)", result);
    }
} 