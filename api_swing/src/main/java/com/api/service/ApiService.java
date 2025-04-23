package com.api.service;

import com.api.models.Admission;
import com.api.models.Allocation;
import com.api.models.Employee;
import com.api.models.Patient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for interacting with the Maternity Web-Service API.
 */
public class ApiService {
    private static final String BASE_URL = "https://web.socem.plymouth.ac.uk/COMP2005/api";
    private final ObjectMapper objectMapper;
    private final CloseableHttpClient httpClient;

    public ApiService() {
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClients.createDefault();
    }

    /**
     * Fetches all patients from the API.
     *
     * @return List of Patient objects
     * @throws IOException if an error occurs during the HTTP request
     */
    public List<Patient> getAllPatients() throws IOException {
        String endpoint = "/Patients";
        String response = makeGetRequest(endpoint);
        return objectMapper.readValue(response, new TypeReference<List<Patient>>() {});
    }

    /**
     * Fetches a patient by ID from the API.
     *
     * @param id The ID of the patient to fetch
     * @return The Patient object or null if not found
     * @throws IOException if an error occurs during the HTTP request
     */
    public Patient getPatientById(int id) throws IOException {
        String endpoint = "/Patients/" + id;
        try {
            String response = makeGetRequest(endpoint);
            return objectMapper.readValue(response, Patient.class);
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Fetches all admissions from the API.
     *
     * @return List of Admission objects
     * @throws IOException if an error occurs during the HTTP request
     */
    public List<Admission> getAllAdmissions() throws IOException {
        String endpoint = "/Admissions";
        String response = makeGetRequest(endpoint);
        return objectMapper.readValue(response, new TypeReference<List<Admission>>() {});
    }

    /**
     * Fetches an admission by ID from the API.
     *
     * @param id The ID of the admission to fetch
     * @return The Admission object or null if not found
     * @throws IOException if an error occurs during the HTTP request
     */
    public Admission getAdmissionById(int id) throws IOException {
        String endpoint = "/Admissions/" + id;
        try {
            String response = makeGetRequest(endpoint);
            return objectMapper.readValue(response, Admission.class);
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Fetches all employees from the API.
     *
     * @return List of Employee objects
     * @throws IOException if an error occurs during the HTTP request
     */
    public List<Employee> getAllEmployees() throws IOException {
        String endpoint = "/Employees";
        String response = makeGetRequest(endpoint);
        return objectMapper.readValue(response, new TypeReference<List<Employee>>() {});
    }

    /**
     * Fetches an employee by ID from the API.
     *
     * @param id The ID of the employee to fetch
     * @return The Employee object or null if not found
     * @throws IOException if an error occurs during the HTTP request
     */
    public Employee getEmployeeById(int id) throws IOException {
        String endpoint = "/Employees/" + id;
        try {
            String response = makeGetRequest(endpoint);
            return objectMapper.readValue(response, Employee.class);
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Fetches all allocations from the API.
     *
     * @return List of Allocation objects
     * @throws IOException if an error occurs during the HTTP request
     */
    public List<Allocation> getAllAllocations() throws IOException {
        String endpoint = "/Allocations";
        String response = makeGetRequest(endpoint);
        return objectMapper.readValue(response, new TypeReference<List<Allocation>>() {});
    }

    /**
     * Fetches an allocation by ID from the API.
     *
     * @param id The ID of the allocation to fetch
     * @return The Allocation object or null if not found
     * @throws IOException if an error occurs during the HTTP request
     */
    public Allocation getAllocationById(int id) throws IOException {
        String endpoint = "/Allocations/" + id;
        try {
            String response = makeGetRequest(endpoint);
            return objectMapper.readValue(response, Allocation.class);
        } catch (IOException e) {
            if (e.getMessage().contains("404")) {
                return null;
            }
            throw e;
        }
    }

    /**
     * Makes a GET request to the API.
     *
     * @param endpoint The API endpoint to call
     * @return The response body as a String
     * @throws IOException if an error occurs during the HTTP request
     */
    private String makeGetRequest(String endpoint) throws IOException {
        HttpGet request = new HttpGet(BASE_URL + endpoint);
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                return EntityUtils.toString(entity);
            } else {
                throw new IOException("No response body");
            }
        }
    }

    /**
     * Closes the HTTP client.
     *
     * @throws IOException if an error occurs while closing the client
     */
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
} 