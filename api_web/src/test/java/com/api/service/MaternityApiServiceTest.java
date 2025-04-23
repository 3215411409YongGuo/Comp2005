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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class MaternityApiServiceTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private MaternityApiService apiService;

    @BeforeEach
    void setUp() {
        // Set up basic WebClient mock behavior
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void getAllPatients_shouldReturnListOfPatients() {
        // Prepare test data
        Patient patient1 = new Patient();
        patient1.setId(1);
        patient1.setSurname("Smith");
        patient1.setForename("John");

        Patient patient2 = new Patient();
        patient2.setId(2);
        patient2.setSurname("Johnson");
        patient2.setForename("Mary");

        Patient[] patients = {patient1, patient2};

        // Set up mock behavior
        when(responseSpec.bodyToMono(Patient[].class)).thenReturn(Mono.just(patients));

        // Execute test
        List<Patient> result = apiService.getAllPatients();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getId());
        assertEquals("Smith", result.get(0).getSurname());
        assertEquals(2, result.get(1).getId());
        assertEquals("Johnson", result.get(1).getSurname());
    }

    @Test
    void getPatientById_shouldReturnPatientWhenExists() {
        // Prepare test data
        Patient patient = new Patient();
        patient.setId(1);
        patient.setSurname("Smith");
        patient.setForename("John");

        // Set up mock behavior
        when(responseSpec.bodyToMono(Patient.class)).thenReturn(Mono.just(patient));

        // Execute test
        Patient result = apiService.getPatientById(1);

        // Verify results
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Smith", result.getSurname());
    }

    @Test
    void getAllAdmissions_shouldReturnListOfAdmissions() {
        // Prepare test data
        Admission admission1 = new Admission();
        admission1.setId(101);
        admission1.setPatientID(1);

        Admission admission2 = new Admission();
        admission2.setId(102);
        admission2.setPatientID(2);

        Admission[] admissions = {admission1, admission2};

        // Set up mock behavior
        when(responseSpec.bodyToMono(Admission[].class)).thenReturn(Mono.just(admissions));

        // Execute test
        List<Admission> result = apiService.getAllAdmissions();

        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(101, result.get(0).getId());
        assertEquals(1, result.get(0).getPatientID());
        assertEquals(102, result.get(1).getId());
    }

    @Test
    void getAllEmployees_shouldReturnListOfEmployees() {
        // Prepare test data
        Employee employee1 = new Employee();
        employee1.setId(301);
        
        Employee employee2 = new Employee();
        employee2.setId(302);
        
        Employee[] employees = {employee1, employee2};
        
        // Set up mock behavior
        when(responseSpec.bodyToMono(Employee[].class)).thenReturn(Mono.just(employees));
        
        // Execute test
        List<Employee> result = apiService.getAllEmployees();
        
        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(301, result.get(0).getId());
        assertEquals(302, result.get(1).getId());
    }
    
    @Test
    void getAllAllocations_shouldReturnListOfAllocations() {
        // Prepare test data
        Allocation allocation1 = new Allocation();
        allocation1.setId(201);
        allocation1.setAdmissionID(101);
        allocation1.setEmployeeID(301);
        
        Allocation allocation2 = new Allocation();
        allocation2.setId(202);
        allocation2.setAdmissionID(102);
        allocation2.setEmployeeID(302);
        
        Allocation[] allocations = {allocation1, allocation2};
        
        // Set up mock behavior
        when(responseSpec.bodyToMono(Allocation[].class)).thenReturn(Mono.just(allocations));
        
        // Execute test
        List<Allocation> result = apiService.getAllAllocations();
        
        // Verify results
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(201, result.get(0).getId());
        assertEquals(101, result.get(0).getAdmissionID());
        assertEquals(301, result.get(0).getEmployeeID());
        assertEquals(202, result.get(1).getId());
    }
    
    @Test
    void getAllocationsById_shouldReturnAllocationWhenExists() {
        // Prepare test data
        Allocation allocation = new Allocation();
        allocation.setId(201);
        allocation.setAdmissionID(101);
        allocation.setEmployeeID(301);
        
        // Set up mock behavior
        when(responseSpec.bodyToMono(Allocation.class)).thenReturn(Mono.just(allocation));
        
        // Execute test
        Allocation result = apiService.getAllocationById(201);
        
        // Verify results
        assertNotNull(result);
        assertEquals(201, result.getId());
        assertEquals(101, result.getAdmissionID());
        assertEquals(301, result.getEmployeeID());
    }
    
    @Test
    void getPatientById_shouldReturnNullWhenNotExists() {
        // Set up mock behavior
        when(responseSpec.bodyToMono(Patient.class)).thenReturn(Mono.error(new RuntimeException("Not found")));
        
        // Execute test
        Patient result = apiService.getPatientById(999);
        
        // Verify results
        assertNull(result);
    }
} 