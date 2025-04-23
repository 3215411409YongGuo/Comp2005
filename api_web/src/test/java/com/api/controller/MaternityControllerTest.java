package com.api.controller;

import com.api.model.Patient;
import com.api.service.MaternityBusinessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class MaternityControllerTest {

    private MockMvc mockMvc;

    @Mock
    private MaternityBusinessService businessService;

    @InjectMocks
    private MaternityController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getPatientsNeverAdmitted_shouldReturnPatients() throws Exception {
        // Prepare test data
        List<Patient> patients = Arrays.asList(
                createPatient(1, "Smith", "John"),
                createPatient(2, "Johnson", "Mary")
        );

        // Set up mock behavior
        when(businessService.getPatientsNeverAdmitted()).thenReturn(patients);

        // Execute test and verify results
        mockMvc.perform(get("/api/maternity/patients/never-admitted"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].surname", is("Smith")))
                .andExpect(jsonPath("$[1].id", is(2)));

        verify(businessService, times(1)).getPatientsNeverAdmitted();
    }

    @Test
    void getPatientsReadmittedWithin7Days_shouldReturnPatients() throws Exception {
        // Prepare test data
        List<Patient> patients = Collections.singletonList(
                createPatient(1, "Smith", "John")
        );

        // Set up mock behavior
        when(businessService.getPatientsReadmittedWithin7Days()).thenReturn(patients);

        // Execute test and verify results
        mockMvc.perform(get("/api/maternity/patients/readmitted-within-7-days"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].surname", is("Smith")));

        verify(businessService, times(1)).getPatientsReadmittedWithin7Days();
    }

    @Test
    void getMonthWithMostAdmissions_shouldReturnMonthData() throws Exception {
        // Prepare test data
        Map<String, Integer> monthData = new HashMap<>();
        monthData.put("2023-01", 10);

        // Set up mock behavior
        when(businessService.getMonthWithMostAdmissions()).thenReturn(monthData);

        // Execute test and verify results
        mockMvc.perform(get("/api/maternity/admissions/month-with-most"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['2023-01']", is(10)));

        verify(businessService, times(1)).getMonthWithMostAdmissions();
    }

    @Test
    void getPatientsWithMultipleStaff_shouldReturnPatients() throws Exception {
        // Prepare test data
        List<Patient> patients = Arrays.asList(
                createPatient(1, "Smith", "John"),
                createPatient(3, "Brown", "David")
        );

        // Set up mock behavior
        when(businessService.getPatientsWithMultipleStaff()).thenReturn(patients);

        // Execute test and verify results
        mockMvc.perform(get("/api/maternity/patients/with-multiple-staff"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(3)));

        verify(businessService, times(1)).getPatientsWithMultipleStaff();
    }

    // Helper method to create test data
    private Patient createPatient(int id, String surname, String forename) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setSurname(surname);
        patient.setForename(forename);
        return patient;
    }
} 