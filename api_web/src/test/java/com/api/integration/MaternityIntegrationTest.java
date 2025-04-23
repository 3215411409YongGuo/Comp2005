package com.api.integration;

import com.api.model.Patient;
import com.api.service.DataCacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MaternityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataCacheService cacheService;

    @Test
    void getPatientsNeverAdmitted_shouldReturnData() throws Exception {
        // Execute integration test and get result
        MvcResult result = mockMvc.perform(get("/api/maternity/patients/never-admitted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Parse response
        String jsonResponse = result.getResponse().getContentAsString();
        List<Patient> patients = objectMapper.readValue(jsonResponse, new TypeReference<List<Patient>>() {});

        // Verify result is not null
        assertNotNull(patients);
        
        // Since this is an integration test, data may vary based on test environment
        // We only verify the API works properly, not specific data values
    }

    @Test
    void getPatientsReadmittedWithin7Days_shouldReturnData() throws Exception {
        // Execute integration test and get result
        MvcResult result = mockMvc.perform(get("/api/maternity/patients/readmitted-within-7-days")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Parse response
        String jsonResponse = result.getResponse().getContentAsString();
        List<Patient> patients = objectMapper.readValue(jsonResponse, new TypeReference<List<Patient>>() {});

        // Verify result is not null - even an empty list is a valid result
        assertNotNull(patients);
    }

    @Test
    void getMonthWithMostAdmissions_shouldReturnData() throws Exception {
        // Execute integration test and get result
        MvcResult result = mockMvc.perform(get("/api/maternity/admissions/month-with-most")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Parse response
        String jsonResponse = result.getResponse().getContentAsString();
        Map<String, Integer> monthData = objectMapper.readValue(jsonResponse, new TypeReference<Map<String, Integer>>() {});

        // Verify result is not null
        assertNotNull(monthData);
        assertFalse(monthData.isEmpty());
        
        // Ensure month format is correct
        monthData.keySet().forEach(key -> assertTrue(key.matches("\\d{4}-\\d{2}")));
        
        // Ensure all values are positive integers
        monthData.values().forEach(value -> assertTrue(value > 0));
    }

    @Test
    void getPatientsWithMultipleStaff_shouldReturnData() throws Exception {
        // Execute integration test and get result
        MvcResult result = mockMvc.perform(get("/api/maternity/patients/with-multiple-staff")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        // Parse response
        String jsonResponse = result.getResponse().getContentAsString();
        List<Patient> patients = objectMapper.readValue(jsonResponse, new TypeReference<List<Patient>>() {});

        // Verify result is not null - even an empty list is a valid result
        assertNotNull(patients);
        
        // Verify each patient has a valid ID
        patients.forEach(patient -> assertNotNull(patient.getId()));
    }
    
    @Test
    void allEndpoints_shouldBeAccessible() throws Exception {
        // Test all endpoints are accessible
        mockMvc.perform(get("/api/maternity/patients/never-admitted"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/maternity/patients/readmitted-within-7-days"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/maternity/admissions/month-with-most"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/maternity/patients/with-multiple-staff"))
                .andExpect(status().isOk());
    }
} 