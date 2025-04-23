package com.api.service;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Employee;
import com.api.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DataCacheServiceTest {

    @Mock
    private MaternityApiService apiService;

    @InjectMocks
    private DataCacheService cacheService;

    private List<Patient> mockPatients;
    private List<Admission> mockAdmissions;
    private List<Employee> mockEmployees;
    private List<Allocation> mockAllocations;

    @BeforeEach
    void setUp() {
        // Create test data
        mockPatients = Arrays.asList(
                createPatient(1, "Smith", "John"),
                createPatient(2, "Johnson", "Mary")
        );
        
        mockAdmissions = Arrays.asList(
                createAdmission(101, 1),
                createAdmission(102, 2)
        );
        
        mockEmployees = Arrays.asList(
                createEmployee(301),
                createEmployee(302)
        );
        
        mockAllocations = Arrays.asList(
                createAllocation(201, 101, 301),
                createAllocation(202, 102, 302)
        );
    }

    @Test
    void getAllPatients_shouldRefreshCacheWhenEmpty() {
        // Set up mock behavior
        when(apiService.getAllPatients()).thenReturn(mockPatients);

        // Execute test
        List<Patient> result = cacheService.getAllPatients();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        
        // Verify API service was called only once
        verify(apiService, times(1)).getAllPatients();
        
        // Second call should use cached data, not call API service again
        List<Patient> cachedResult = cacheService.getAllPatients();
        assertEquals(2, cachedResult.size());
        verify(apiService, times(1)).getAllPatients(); // Total call count still 1
    }

    @Test
    void getAllAdmissions_shouldRefreshCacheWhenEmpty() {
        // Set up mock behavior
        when(apiService.getAllAdmissions()).thenReturn(mockAdmissions);

        // Execute test
        List<Admission> result = cacheService.getAllAdmissions();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getId());
        assertEquals(102, result.get(1).getId());
        
        // Verify API service was called only once
        verify(apiService, times(1)).getAllAdmissions();
        
        // Second call should use cached data, not call API service again
        List<Admission> cachedResult = cacheService.getAllAdmissions();
        assertEquals(2, cachedResult.size());
        verify(apiService, times(1)).getAllAdmissions(); // Total call count still 1
    }

    @Test
    void getAllEmployees_shouldRefreshCacheWhenEmpty() {
        // Set up mock behavior
        when(apiService.getAllEmployees()).thenReturn(mockEmployees);

        // Execute test
        List<Employee> result = cacheService.getAllEmployees();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(301, result.get(0).getId());
        assertEquals(302, result.get(1).getId());
        
        // Verify API service was called only once
        verify(apiService, times(1)).getAllEmployees();
        
        // Second call should use cached data, not call API service again
        List<Employee> cachedResult = cacheService.getAllEmployees();
        assertEquals(2, cachedResult.size());
        verify(apiService, times(1)).getAllEmployees(); // Total call count still 1
    }

    @Test
    void getAllAllocations_shouldRefreshCacheWhenEmpty() {
        // Set up mock behavior
        when(apiService.getAllAllocations()).thenReturn(mockAllocations);

        // Execute test
        List<Allocation> result = cacheService.getAllAllocations();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(201, result.get(0).getId());
        assertEquals(202, result.get(1).getId());
        
        // Verify API service was called only once
        verify(apiService, times(1)).getAllAllocations();
        
        // Second call should use cached data, not call API service again
        List<Allocation> cachedResult = cacheService.getAllAllocations();
        assertEquals(2, cachedResult.size());
        verify(apiService, times(1)).getAllAllocations(); // Total call count still 1
    }

    @Test
    void refreshAllCaches_shouldRefreshAllCaches() {
        // Set up mock behavior
        when(apiService.getAllPatients()).thenReturn(mockPatients);
        when(apiService.getAllAdmissions()).thenReturn(mockAdmissions);
        when(apiService.getAllEmployees()).thenReturn(mockEmployees);
        when(apiService.getAllAllocations()).thenReturn(mockAllocations);

        // Execute test
        cacheService.refreshAllCaches();

        // Verify each API method was called once
        verify(apiService, times(1)).getAllPatients();
        verify(apiService, times(1)).getAllAdmissions();
        verify(apiService, times(1)).getAllEmployees();
        verify(apiService, times(1)).getAllAllocations();
        
        // Verify cache contains data
        assertNotNull(cacheService.getAllPatients());
        assertNotNull(cacheService.getAllAdmissions());
        assertNotNull(cacheService.getAllEmployees());
        assertNotNull(cacheService.getAllAllocations());
    }

    // Helper methods to create test data
    private Patient createPatient(int id, String surname, String forename) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setSurname(surname);
        patient.setForename(forename);
        return patient;
    }

    private Admission createAdmission(int id, int patientId) {
        Admission admission = new Admission();
        admission.setId(id);
        admission.setPatientID(patientId);
        return admission;
    }

    private Employee createEmployee(int id) {
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

    private Allocation createAllocation(int id, int admissionId, int employeeId) {
        Allocation allocation = new Allocation();
        allocation.setId(id);
        allocation.setAdmissionID(admissionId);
        allocation.setEmployeeID(employeeId);
        return allocation;
    }
} 