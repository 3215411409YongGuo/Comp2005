package com.api.models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Patient class.
 */
public class PatientTest {

    @Test
    public void testConstructorAndGetters() {
        // Arrange
        int id = 1;
        String surname = "Smith";
        String forename = "John";
        String nhsNumber = "1234567890";

        // Act
        Patient patient = new Patient(id, surname, forename, nhsNumber);

        // Assert
        assertEquals(id, patient.getId());
        assertEquals(surname, patient.getSurname());
        assertEquals(forename, patient.getForename());
        assertEquals(nhsNumber, patient.getNhsNumber());
    }

    @Test
    public void testSetters() {
        // Arrange
        Patient patient = new Patient();

        // Act
        patient.setId(1);
        patient.setSurname("Smith");
        patient.setForename("John");
        patient.setNhsNumber("1234567890");

        // Assert
        assertEquals(1, patient.getId());
        assertEquals("Smith", patient.getSurname());
        assertEquals("John", patient.getForename());
        assertEquals("1234567890", patient.getNhsNumber());
    }

    @Test
    public void testToString() {
        // Arrange
        Patient patient = new Patient(1, "Smith", "John", "1234567890");

        // Act
        String result = patient.toString();

        // Assert
        assertEquals("1: John Smith", result);
    }
} 