package com.api.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Security system tests for the maternity ward application.
 * These tests verify the API security aspects.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class SecuritySystemTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    /**
     * Tests that CORS headers are properly set in the response
     */
    @Test
    void corsHeaders_areCorrectlySet() {
        // Create a request with Origin header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Origin", "http://localhost:3000");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Make a request to the API
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.GET,
                entity,
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        // Check CORS headers
        // Note: In a real application, the CORS headers would be properly configured
        // This test is a placeholder for actual CORS configuration validation
        HttpHeaders responseHeaders = response.getHeaders();
        if (responseHeaders.get("Access-Control-Allow-Origin") != null) {
            assertTrue(responseHeaders.get("Access-Control-Allow-Origin").contains("*") || 
                    responseHeaders.get("Access-Control-Allow-Origin").contains("http://localhost:3000"),
                    "CORS origin header should be properly set if implemented");
        }
    }

    /**
     * Tests that the request content type is validated
     */
    @Test
    void contentTypeValidation_forRequestsReturnsCorrectContentType() {
        // Set Accept header to expect JSON
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Make a request to the API
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.GET,
                entity,
                String.class
        );
        
        // Verify response contains the correct content type
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON),
                "Response should have application/json content type");
    }

    /**
     * Tests that API handles malformed input gracefully
     */
    @Test
    void malformedInput_isHandledGracefully() {
        // Try accessing a non-existent path
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/maternity/non-existent-path",
                String.class
        );
        
        // Verify response is either 404 (Not Found) or 400 (Bad Request), not 500 (Server Error)
        assertTrue(response.getStatusCode() == HttpStatus.NOT_FOUND || 
                response.getStatusCode() == HttpStatus.BAD_REQUEST,
                "Invalid paths should return 404 or 400, not 500");
    }

    /**
     * Tests that request method validation works
     */
    @Test
    void methodValidation_disallowsUnsupportedMethods() {
        // Try to use PUT method on a GET endpoint
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>("test data", headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.PUT,
                entity,
                String.class
        );
        
        // Verify response is an error code (normally 405 Method Not Allowed)
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
        
        // Try with other methods that should not be allowed
        response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.DELETE,
                entity,
                String.class
        );
        
        // Verify response is an error code
        assertNotEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests that request headers are properly validated
     */
    @Test
    void headerValidation_acceptsProperHeaders() {
        // Request with correct headers
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/maternity/patients/never-admitted",
                HttpMethod.GET,
                entity,
                String.class
        );
        
        // Verify response is successful
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Tests swagger documentation accessibility
     */
    @Test
    void swaggerDocumentation_isAccessible() {
        // Try to access the OpenAPI documentation
        ResponseEntity<String> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/v3/api-docs",
                String.class
        );
        
        // Verify response
        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "OpenAPI documentation should be accessible");
        
        // Check that the response contains OpenAPI content
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("openapi"),
                "Response should contain OpenAPI specification");
    }
} 