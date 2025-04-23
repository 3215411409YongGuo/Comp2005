package com.api.service;

import com.api.models.Admission;
import com.api.models.Allocation;
import com.api.models.Employee;
import com.api.models.Patient;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ApiService class.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ApiServiceTest {

    @Mock
    private CloseableHttpClient httpClient;

    @Mock
    private CloseableHttpResponse response;

    @Mock
    private HttpEntity entity;

    @Mock
    private StatusLine statusLine;

    private ApiService apiServiceWithMockedClient;

    @BeforeEach
    public void setUp() throws IOException {
        // Set up common mocks
        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
        when(response.getEntity()).thenReturn(entity);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(200);

        // Creating an ApiService instance with mocked httpClient would require modification
        // of the ApiService class to accept a custom httpClient in the constructor.
        // For this test, we'll use reflection to replace the private httpClient field.
        apiServiceWithMockedClient = new ApiService();
        try {
            java.lang.reflect.Field field = ApiService.class.getDeclaredField("httpClient");
            field.setAccessible(true);
            field.set(apiServiceWithMockedClient, httpClient);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to inject mocked HTTP client: " + e.getMessage());
        }
    }

    @Test
    public void testGetAllPatients() throws IOException {
        // Arrange
        String mockResponse = "[{\"id\":1,\"surname\":\"Smith\",\"forename\":\"John\",\"nhsNumber\":\"1234567890\"}]";
        
        try (MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            entityUtils.when(() -> EntityUtils.toString(any(HttpEntity.class))).thenReturn(mockResponse);
            
            // Act
            List<Patient> patients = apiServiceWithMockedClient.getAllPatients();
            
            // Assert
            assertNotNull(patients);
            assertEquals(1, patients.size());
            assertEquals(1, patients.get(0).getId());
            assertEquals("Smith", patients.get(0).getSurname());
            assertEquals("John", patients.get(0).getForename());
            assertEquals("1234567890", patients.get(0).getNhsNumber());
            
            // Verify
            verify(httpClient).execute(any(HttpGet.class));
        }
    }

    @Test
    public void testGetPatientById() throws IOException {
        // Arrange
        String mockResponse = "{\"id\":1,\"surname\":\"Smith\",\"forename\":\"John\",\"nhsNumber\":\"1234567890\"}";
        
        try (MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            entityUtils.when(() -> EntityUtils.toString(any(HttpEntity.class))).thenReturn(mockResponse);
            
            // Act
            Patient patient = apiServiceWithMockedClient.getPatientById(1);
            
            // Assert
            assertNotNull(patient);
            assertEquals(1, patient.getId());
            assertEquals("Smith", patient.getSurname());
            assertEquals("John", patient.getForename());
            assertEquals("1234567890", patient.getNhsNumber());
            
            // Verify
            verify(httpClient).execute(any(HttpGet.class));
        }
    }

    @Test
    public void testGetPatientByIdNotFound() throws IOException {
        // Arrange
        when(statusLine.getStatusCode()).thenReturn(404);
        when(httpClient.execute(any(HttpGet.class))).thenThrow(new IOException("404 Not Found"));
        
        // Act
        Patient patient = apiServiceWithMockedClient.getPatientById(999);
        
        // Assert
        assertNull(patient);
    }

    @Test
    public void testGetAllAdmissions() throws IOException {
        // Arrange
        String mockResponse = "[{\"id\":1,\"admissionDate\":\"2025-04-10\",\"dischargeDate\":\"2025-04-20\",\"patientID\":1}]";
        
        try (MockedStatic<EntityUtils> entityUtils = mockStatic(EntityUtils.class)) {
            entityUtils.when(() -> EntityUtils.toString(any(HttpEntity.class))).thenReturn(mockResponse);
            
            // Act
            List<Admission> admissions = apiServiceWithMockedClient.getAllAdmissions();
            
            // Assert
            assertNotNull(admissions);
            assertEquals(1, admissions.size());
            assertEquals(1, admissions.get(0).getId());
            assertEquals(1, admissions.get(0).getPatientID());
            assertEquals("2025-04-10", admissions.get(0).getAdmissionDate());
            assertEquals("2025-04-20", admissions.get(0).getDischargeDate());
            
            // Verify
            verify(httpClient).execute(any(HttpGet.class));
        }
    }

    @Test
    public void testClose() throws IOException {
        // Act
        apiServiceWithMockedClient.close();
        
        // Verify
        verify(httpClient).close();
    }
} 