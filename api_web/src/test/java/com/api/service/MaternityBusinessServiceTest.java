package com.api.service;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaternityBusinessServiceTest {

    @Mock
    private MaternityApiService apiService;

    @Mock
    private DataCacheService cacheService;

    @InjectMocks
    private MaternityBusinessService businessService;

    private List<Patient> mockPatients;
    private List<Admission> mockAdmissions;
    private List<Allocation> mockAllocations;

    @BeforeEach
    void setUp() {
        // Prepare test data
        mockPatients = Arrays.asList(
                createPatient(1, "Smith", "John"),
                createPatient(2, "Johnson", "Mary"),
                createPatient(3, "Brown", "David")
        );

        mockAdmissions = Arrays.asList(
                createAdmission(101, 1, "2023-01-01T10:00:00", "2023-01-10T14:00:00"),
                createAdmission(102, 1, "2023-01-15T09:00:00", "2023-01-20T16:00:00"), // Readmitted after 5 days
                createAdmission(103, 2, "2023-02-05T08:00:00", "2023-02-15T11:00:00")
                // Patient 3 has no admission record
        );

        mockAllocations = Arrays.asList(
                createAllocation(201, 101, 301), // Patient 1's first admission, staff 301
                createAllocation(202, 101, 302), // Patient 1's first admission, staff 302
                createAllocation(203, 102, 301), // Patient 1's second admission, staff 301
                createAllocation(204, 103, 303)  // Patient 2's admission, staff 303
        );
    }

    @Test
    void getPatientsNeverAdmitted_shouldReturnPatientsWithNoAdmissions() {
        // Set up mock behavior
        when(cacheService.getAllPatients()).thenReturn(mockPatients);
        when(cacheService.getAllAdmissions()).thenReturn(mockAdmissions);

        // Execute test
        List<Patient> result = businessService.getPatientsNeverAdmitted();

        // Verify results
        assertEquals(1, result.size());
        assertEquals(3, result.get(0).getId());
        verify(cacheService, times(1)).getAllPatients();
        verify(cacheService, times(1)).getAllAdmissions();
    }

    @Test
    void getPatientsReadmittedWithin7Days_shouldReturnPatientsReadmittedWithin7Days() {
        // Set up mock behavior
        when(cacheService.getAllAdmissions()).thenReturn(mockAdmissions);
        when(cacheService.getAllPatients()).thenReturn(mockPatients);

        // Execute test
        List<Patient> result = businessService.getPatientsReadmittedWithin7Days();

        // Verify results
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId());
        verify(cacheService, times(1)).getAllAdmissions();
        verify(cacheService, times(1)).getAllPatients();
    }

    @Test
    void getMonthWithMostAdmissions_shouldReturnMonthWithHighestAdmissions() {
        // Set up mock behavior
        when(cacheService.getAllAdmissions()).thenReturn(mockAdmissions);

        // Execute test
        Map<String, Integer> result = businessService.getMonthWithMostAdmissions();

        // Verify results
        assertFalse(result.isEmpty());
        assertEquals(2, result.get("2023-01")); // January has 2 admissions
        verify(cacheService, times(1)).getAllAdmissions();
    }

    @Test
    void getPatientsWithMultipleStaff_shouldReturnPatientsWithMoreThanOneStaffAssigned() {
        // Set up mock behavior
        when(cacheService.getAllAdmissions()).thenReturn(mockAdmissions);
        when(cacheService.getAllPatients()).thenReturn(mockPatients);
        when(cacheService.getAllAllocations()).thenReturn(mockAllocations);

        // Execute test
        List<Patient> result = businessService.getPatientsWithMultipleStaff();

        // Verify results
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId()); // Patient 1 has multiple staff
        verify(cacheService, times(1)).getAllAdmissions();
        verify(cacheService, times(1)).getAllPatients();
        verify(cacheService, times(1)).getAllAllocations();
    }

    // Helper methods to create test data
    private Patient createPatient(int id, String surname, String forename) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setSurname(surname);
        patient.setForename(forename);
        return patient;
    }

    private Admission createAdmission(int id, int patientId, String admissionDate, String dischargeDate) {
        Admission admission = new Admission();
        admission.setId(id);
        admission.setPatientID(patientId);
        admission.setAdmissionDate(admissionDate);
        admission.setDischargeDate(dischargeDate);
        return admission;
    }

    private Allocation createAllocation(int id, int admissionId, int employeeId) {
        Allocation allocation = new Allocation();
        allocation.setId(id);
        allocation.setAdmissionID(admissionId);
        allocation.setEmployeeID(employeeId);
        return allocation;
    }
} 