package com.api.service;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Employee;
import com.api.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class MaternityApiService {

    private final WebClient webClient;

    @Autowired
    public MaternityApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<Patient> getAllPatients() {
        return webClient.get()
                .uri("/Patients")
                .retrieve()
                .bodyToMono(Patient[].class)
                .map(Arrays::asList)
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public Patient getPatientById(Integer id) {
        return webClient.get()
                .uri("/Patients/{id}", id)
                .retrieve()
                .bodyToMono(Patient.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public List<Admission> getAllAdmissions() {
        return webClient.get()
                .uri("/Admissions")
                .retrieve()
                .bodyToMono(Admission[].class)
                .map(Arrays::asList)
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public Admission getAdmissionById(Integer id) {
        return webClient.get()
                .uri("/Admissions/{id}", id)
                .retrieve()
                .bodyToMono(Admission.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public List<Employee> getAllEmployees() {
        return webClient.get()
                .uri("/Employees")
                .retrieve()
                .bodyToMono(Employee[].class)
                .map(Arrays::asList)
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public Employee getEmployeeById(Integer id) {
        return webClient.get()
                .uri("/Employees/{id}", id)
                .retrieve()
                .bodyToMono(Employee.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }

    public List<Allocation> getAllAllocations() {
        return webClient.get()
                .uri("/Allocations")
                .retrieve()
                .bodyToMono(Allocation[].class)
                .map(Arrays::asList)
                .onErrorReturn(Collections.emptyList())
                .block();
    }

    public Allocation getAllocationById(Integer id) {
        return webClient.get()
                .uri("/Allocations/{id}", id)
                .retrieve()
                .bodyToMono(Allocation.class)
                .onErrorResume(e -> Mono.empty())
                .block();
    }
} 