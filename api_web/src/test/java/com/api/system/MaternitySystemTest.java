package com.api.system;

import com.api.model.Patient;
import com.api.service.DataCacheService;
import com.api.service.MaternityBusinessService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * System tests for the maternity ward application.
 * These tests verify the entire system works correctly from end to end.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class MaternitySystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private MaternityBusinessService businessService;
    
    @Autowired
    private DataCacheService cacheService;

    /**
     * Tests that the application context loads successfully
     */
    @Test
    void contextLoads() {
        assertNotNull(businessService);
        assertNotNull(cacheService);
    }

    /**
     * Tests the complete flow of retrieving patients who have never been admitted
     */
    @Test
    void endToEnd_getNeverAdmittedPatients() {
        // First ensure the cache is populated
        assertNotNull(cacheService.getAllPatients());
        assertNotNull(cacheService.getAllAdmissions());
        
        // Call the REST endpoint
        String url = "http://localhost:" + port + "/api/maternity/patients/never-admitted";
        ResponseEntity<List<Patient>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        
        // Verify the HTTP response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify the response matches what we would get directly from the service
        List<Patient> expectedPatients = businessService.getPatientsNeverAdmitted();
        List<Patient> actualPatients = response.getBody();
        
        // Compare response size with expected data
        assertEquals(expectedPatients.size(), actualPatients.size());
    }

    /**
     * Tests the complete flow of retrieving patients readmitted within 7 days
     */
    @Test
    void endToEnd_getPatientsReadmittedWithin7Days() {
        // First ensure the cache is populated
        assertNotNull(cacheService.getAllPatients());
        assertNotNull(cacheService.getAllAdmissions());
        
        // Call the REST endpoint
        String url = "http://localhost:" + port + "/api/maternity/patients/readmitted-within-7-days";
        ResponseEntity<List<Patient>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        
        // Verify the HTTP response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify the response matches what we would get directly from the service
        List<Patient> expectedPatients = businessService.getPatientsReadmittedWithin7Days();
        List<Patient> actualPatients = response.getBody();
        
        // Compare response size with expected data
        assertEquals(expectedPatients.size(), actualPatients.size());
    }

    /**
     * Tests the complete flow of retrieving month with most admissions
     */
    @Test
    void endToEnd_getMonthWithMostAdmissions() {
        // First ensure the cache is populated
        assertNotNull(cacheService.getAllAdmissions());
        
        // Call the REST endpoint
        String url = "http://localhost:" + port + "/api/maternity/admissions/month-with-most";
        ResponseEntity<Map<String, Integer>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Integer>>() {}
        );
        
        // Verify the HTTP response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        
        // Verify the response matches what we would get directly from the service
        Map<String, Integer> expectedData = businessService.getMonthWithMostAdmissions();
        Map<String, Integer> actualData = response.getBody();
        
        // Compare response keys and values with expected data
        assertEquals(expectedData.size(), actualData.size());
        
        // Ensure the month with most admissions is the same
        String expectedMonth = expectedData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
                
        String actualMonth = actualData.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
                
        assertEquals(expectedMonth, actualMonth);
    }

    /**
     * Tests the complete flow of retrieving patients with multiple staff
     */
    @Test
    void endToEnd_getPatientsWithMultipleStaff() {
        // First ensure the cache is populated
        assertNotNull(cacheService.getAllPatients());
        assertNotNull(cacheService.getAllAdmissions());
        assertNotNull(cacheService.getAllAllocations());
        
        // Call the REST endpoint
        String url = "http://localhost:" + port + "/api/maternity/patients/with-multiple-staff";
        ResponseEntity<List<Patient>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        
        // Verify the HTTP response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        // Verify the response matches what we would get directly from the service
        List<Patient> expectedPatients = businessService.getPatientsWithMultipleStaff();
        List<Patient> actualPatients = response.getBody();
        
        // Compare response size with expected data
        assertEquals(expectedPatients.size(), actualPatients.size());
    }
    
    /**
     * Tests the complete chain of data flow through all components
     */
    @Test
    void systemFlow_dataIntegrity() {
        // Force a cache refresh to ensure data consistency
        cacheService.refreshAllCaches();
        
        // Get data from each endpoint
        List<Patient> neverAdmitted = businessService.getPatientsNeverAdmitted();
        List<Patient> readmitted = businessService.getPatientsReadmittedWithin7Days();
        Map<String, Integer> monthData = businessService.getMonthWithMostAdmissions();
        List<Patient> withMultipleStaff = businessService.getPatientsWithMultipleStaff();
        
        // Verify data integrity - patients that have never been admitted cannot be readmitted
        for (Patient patient : neverAdmitted) {
            assertFalse(readmitted.contains(patient), 
                    "A patient cannot both never be admitted and be readmitted");
        }
        
        // Verify month data is valid
        assertFalse(monthData.isEmpty());
        monthData.keySet().forEach(key -> assertTrue(key.matches("\\d{4}-\\d{2}"), 
                "Month format should be YYYY-MM"));
        monthData.values().forEach(value -> assertTrue(value > 0, 
                "Admission count should be positive"));
        
        // Verify the system as a whole is in a consistent state
        assertTrue(cacheService.getAllPatients().size() >= 
                neverAdmitted.size() + readmitted.size() + withMultipleStaff.size() - 
                // Account for potential overlap between readmitted and multiple staff
                countOverlap(readmitted, withMultipleStaff),
                "Total patient count should be consistent with filtered results");
    }
    
    /**
     * Helper method to count overlap between two patient lists
     */
    private int countOverlap(List<Patient> list1, List<Patient> list2) {
        int count = 0;
        for (Patient p1 : list1) {
            for (Patient p2 : list2) {
                if (p1.getId().equals(p2.getId())) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }
} 