package com.api.service;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Employee;
import com.api.model.Patient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DataCacheService {

    private final MaternityApiService apiService;
    
    private final AtomicReference<List<Patient>> patientsCache = new AtomicReference<>();
    private final AtomicReference<List<Admission>> admissionsCache = new AtomicReference<>();
    private final AtomicReference<List<Employee>> employeesCache = new AtomicReference<>();
    private final AtomicReference<List<Allocation>> allocationsCache = new AtomicReference<>();

    @Autowired
    public DataCacheService(MaternityApiService apiService) {
        this.apiService = apiService;
    }

    @PostConstruct
    public void init() {
        refreshAllCaches();
    }

    // Refresh data cache every 12 hours
    @Scheduled(fixedRate = 12 * 60 * 60 * 1000)
    public void refreshAllCaches() {
        refreshPatientsCache();
        refreshAdmissionsCache();
        refreshEmployeesCache();
        refreshAllocationsCache();
    }
    
    public void refreshPatientsCache() {
        List<Patient> patients = apiService.getAllPatients();
        if (patients != null) {
            patientsCache.set(patients);
        }
    }
    
    public void refreshAdmissionsCache() {
        List<Admission> admissions = apiService.getAllAdmissions();
        if (admissions != null) {
            admissionsCache.set(admissions);
        }
    }
    
    public void refreshEmployeesCache() {
        List<Employee> employees = apiService.getAllEmployees();
        if (employees != null) {
            employeesCache.set(employees);
        }
    }
    
    public void refreshAllocationsCache() {
        List<Allocation> allocations = apiService.getAllAllocations();
        if (allocations != null) {
            allocationsCache.set(allocations);
        }
    }
    
    public List<Patient> getAllPatients() {
        List<Patient> patients = patientsCache.get();
        if (patients == null) {
            refreshPatientsCache();
            patients = patientsCache.get();
        }
        return patients;
    }
    
    public List<Admission> getAllAdmissions() {
        List<Admission> admissions = admissionsCache.get();
        if (admissions == null) {
            refreshAdmissionsCache();
            admissions = admissionsCache.get();
        }
        return admissions;
    }
    
    public List<Employee> getAllEmployees() {
        List<Employee> employees = employeesCache.get();
        if (employees == null) {
            refreshEmployeesCache();
            employees = employeesCache.get();
        }
        return employees;
    }
    
    public List<Allocation> getAllAllocations() {
        List<Allocation> allocations = allocationsCache.get();
        if (allocations == null) {
            refreshAllocationsCache();
            allocations = allocationsCache.get();
        }
        return allocations;
    }
} 