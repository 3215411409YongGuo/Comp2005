package com.api.system;

import com.api.model.Patient;
import com.api.service.DataCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Performance system tests for the maternity ward application.
 * These tests verify the system's performance under load.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class PerformanceSystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private DataCacheService cacheService;

    @BeforeEach
    void setUp() {
        // Ensure cache is populated before performance tests
        cacheService.refreshAllCaches();
    }

    /**
     * Tests response time for all endpoints
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void responseTime_allEndpoints() {
        // Verify never admitted patients endpoint response time
        long startTime = System.currentTimeMillis();
        ResponseEntity<List<Patient>> response1 = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 1000, "Never admitted endpoint should respond in less than 1 second");
        assertEquals(200, response1.getStatusCodeValue());
        
        // Verify readmitted patients endpoint response time
        startTime = System.currentTimeMillis();
        ResponseEntity<List<Patient>> response2 = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/readmitted-within-7-days",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 1000, "Readmitted endpoint should respond in less than 1 second");
        assertEquals(200, response2.getStatusCodeValue());
        
        // Verify month with most admissions endpoint response time
        startTime = System.currentTimeMillis();
        ResponseEntity<Map<String, Integer>> response3 = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/admissions/month-with-most",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Integer>>() {}
        );
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 1000, "Month with most admissions endpoint should respond in less than 1 second");
        assertEquals(200, response3.getStatusCodeValue());
        
        // Verify patients with multiple staff endpoint response time
        startTime = System.currentTimeMillis();
        ResponseEntity<List<Patient>> response4 = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/with-multiple-staff",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 1000, "Multiple staff endpoint should respond in less than 1 second");
        assertEquals(200, response4.getStatusCodeValue());
    }

    /**
     * Tests system behavior under concurrent load
     */
    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void concurrentRequests_systemStability() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // Submit concurrent requests
        for (int i = 0; i < numberOfThreads; i++) {
            final int index = i % 4; // Cycle through 4 endpoints
            executorService.submit(() -> {
                try {
                    switch (index) {
                        case 0:
                            ResponseEntity<List<Patient>> response1 = restTemplate.exchange(
                                    "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<List<Patient>>() {}
                            );
                            if (response1.getStatusCode().is2xxSuccessful()) {
                                successCount.incrementAndGet();
                            }
                            break;
                        case 1:
                            ResponseEntity<List<Patient>> response2 = restTemplate.exchange(
                                    "http://localhost:" + port + "/api/maternity/patients/readmitted-within-7-days",
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<List<Patient>>() {}
                            );
                            if (response2.getStatusCode().is2xxSuccessful()) {
                                successCount.incrementAndGet();
                            }
                            break;
                        case 2:
                            ResponseEntity<Map<String, Integer>> response3 = restTemplate.exchange(
                                    "http://localhost:" + port + "/api/maternity/admissions/month-with-most",
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<Map<String, Integer>>() {}
                            );
                            if (response3.getStatusCode().is2xxSuccessful()) {
                                successCount.incrementAndGet();
                            }
                            break;
                        case 3:
                            ResponseEntity<List<Patient>> response4 = restTemplate.exchange(
                                    "http://localhost:" + port + "/api/maternity/patients/with-multiple-staff",
                                    HttpMethod.GET,
                                    null,
                                    new ParameterizedTypeReference<List<Patient>>() {}
                            );
                            if (response4.getStatusCode().is2xxSuccessful()) {
                                successCount.incrementAndGet();
                            }
                            break;
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Wait for all threads to complete
        latch.await(5, TimeUnit.SECONDS);
        executorService.shutdown();
        
        // Verify success rate
        assertEquals(numberOfThreads, successCount.get(), 
                "All concurrent requests should complete successfully");
    }
    
    /**
     * Tests cache refresh performance
     */
    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void cacheRefresh_performance() {
        long startTime = System.currentTimeMillis();
        
        // Perform cache refresh
        cacheService.refreshAllCaches();
        
        long endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 3000, 
                "Cache refresh should complete in less than 3 seconds");
        
        // Verify cache contains data
        assertNotNull(cacheService.getAllPatients());
        assertNotNull(cacheService.getAllAdmissions());
        assertNotNull(cacheService.getAllEmployees());
        assertNotNull(cacheService.getAllAllocations());
        
        // Verify subsequent requests use cached data and are fast
        startTime = System.currentTimeMillis();
        cacheService.getAllPatients();
        endTime = System.currentTimeMillis();
        assertTrue((endTime - startTime) < 50, 
                "Cached data retrieval should be very fast (< 50ms)");
    }
    
    /**
     * Tests data consistency between cache and API responses
     */
    @Test
    void dataConsistency_cacheAndAPI() {
        // Get data from cache
        List<Patient> cachedPatients = cacheService.getAllPatients();
        
        // Get data from API
        ResponseEntity<List<Patient>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Patient>>() {}
        );
        List<Patient> apiPatients = response.getBody();
        
        // Verify API patients are a subset of cached patients
        assertNotNull(apiPatients);
        for (Patient apiPatient : apiPatients) {
            boolean found = false;
            for (Patient cachedPatient : cachedPatients) {
                if (apiPatient.getId().equals(cachedPatient.getId())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found, "API patient should exist in cache");
        }
    }
} 