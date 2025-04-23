package com.api.service;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MaternityBusinessService {

    private final MaternityApiService apiService;
    private final DataCacheService cacheService;
    
    // Define multiple date formatters to handle different date formats
    private static final DateTimeFormatter[] DATE_FORMATTERS = {
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
    };

    @Autowired
    public MaternityBusinessService(MaternityApiService apiService, DataCacheService cacheService) {
        this.apiService = apiService;
        this.cacheService = cacheService;
    }

    /**
     * Try to parse a date string using multiple formats
     */
    private LocalDateTime parseDateTime(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDateTime.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                // Try the next format
            }
        }
        
        throw new IllegalArgumentException("Unable to parse date: " + dateString);
    }

    /**
     * F1 - Get a list of patients who have never been admitted
     */
    public List<Patient> getPatientsNeverAdmitted() {
        List<Patient> allPatients = cacheService.getAllPatients();
        List<Admission> allAdmissions = cacheService.getAllAdmissions();
        
        // Get IDs of all patients who have been admitted
        Set<Integer> admittedPatientIds = allAdmissions.stream()
                .map(Admission::getPatientID)
                .collect(Collectors.toSet());
        
        // Filter out patients who have never been admitted
        return allPatients.stream()
                .filter(patient -> !admittedPatientIds.contains(patient.getId()))
                .collect(Collectors.toList());
    }

    /**
     * F2 - Get a list of patients who were readmitted within 7 days after discharge
     */
    public List<Patient> getPatientsReadmittedWithin7Days() {
        List<Admission> allAdmissions = cacheService.getAllAdmissions();
        List<Patient> allPatients = cacheService.getAllPatients();
        Map<Integer, List<Admission>> admissionsByPatient = new HashMap<>();
        
        // Group admission records by patient ID
        for (Admission admission : allAdmissions) {
            admissionsByPatient.computeIfAbsent(admission.getPatientID(), k -> new ArrayList<>()).add(admission);
        }
        
        // Find patient IDs of those who were readmitted
        Set<Integer> readmittedPatientIds = new HashSet<>();
        
        for (Map.Entry<Integer, List<Admission>> entry : admissionsByPatient.entrySet()) {
            List<Admission> patientAdmissions = entry.getValue();
            if (patientAdmissions.size() < 2) {
                continue; // Skip patients with fewer than 2 admissions
            }
            
            // Sort by admission date
            patientAdmissions.sort(Comparator.comparing(Admission::getAdmissionDate));
            
            for (int i = 0; i < patientAdmissions.size() - 1; i++) {
                Admission current = patientAdmissions.get(i);
                Admission next = patientAdmissions.get(i + 1);
                
                // Skip if current record has no discharge date
                if (current.getDischargeDate() == null || current.getDischargeDate().isEmpty()) {
                    continue;
                }
                
                LocalDateTime dischargeDate = parseDateTime(current.getDischargeDate());
                LocalDateTime nextAdmissionDate = parseDateTime(next.getAdmissionDate());
                
                if (dischargeDate == null || nextAdmissionDate == null) {
                    continue; // Skip records with unparseable dates
                }
                
                long daysBetween = ChronoUnit.DAYS.between(dischargeDate, nextAdmissionDate);
                
                if (daysBetween >= 0 && daysBetween <= 7) {
                    readmittedPatientIds.add(current.getPatientID());
                    break; // Found a match for this patient, no need to check further
                }
            }
        }
        
        // Get patient details by IDs
        return allPatients.stream()
                .filter(patient -> readmittedPatientIds.contains(patient.getId()))
                .collect(Collectors.toList());
    }

    /**
     * F3 - Determine which month had the highest number of admissions
     */
    public Map<String, Integer> getMonthWithMostAdmissions() {
        List<Admission> allAdmissions = cacheService.getAllAdmissions();
        
        // Count admissions by month
        Map<YearMonth, Integer> admissionsByMonth = new HashMap<>();
        
        for (Admission admission : allAdmissions) {
            if (admission.getAdmissionDate() != null && !admission.getAdmissionDate().isEmpty()) {
                try {
                    LocalDateTime admissionDate = parseDateTime(admission.getAdmissionDate());
                    if (admissionDate != null) {
                        YearMonth yearMonth = YearMonth.of(admissionDate.getYear(), admissionDate.getMonth());
                        admissionsByMonth.merge(yearMonth, 1, Integer::sum);
                    }
                } catch (Exception e) {
                    // Log error but continue processing other records
                    System.err.println("Error parsing date: " + admission.getAdmissionDate() + " - " + e.getMessage());
                }
            }
        }
        
        // Find the month with the highest number of admissions
        Optional<Map.Entry<YearMonth, Integer>> maxEntry = admissionsByMonth.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        
        Map<String, Integer> result = new HashMap<>();
        if (maxEntry.isPresent()) {
            YearMonth maxMonth = maxEntry.get().getKey();
            int count = maxEntry.get().getValue();
            result.put(maxMonth.toString(), count);
        }
        
        return result;
    }

    /**
     * F4 - Get a list of patients who have more than one staff member assigned
     */
    public List<Patient> getPatientsWithMultipleStaff() {
        List<Admission> allAdmissions = cacheService.getAllAdmissions();
        List<Patient> allPatients = cacheService.getAllPatients();
        List<Allocation> allAllocations = cacheService.getAllAllocations();
        
        // Count different staff members assigned to each admission
        Map<Integer, Set<Integer>> staffByAdmission = new HashMap<>();
        
        for (Allocation allocation : allAllocations) {
            staffByAdmission.computeIfAbsent(allocation.getAdmissionID(), k -> new HashSet<>())
                    .add(allocation.getEmployeeID());
        }
        
        // Find admission IDs with multiple staff
        Set<Integer> multiStaffAdmissionIds = staffByAdmission.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
        
        // Find patient IDs corresponding to these admissions
        Set<Integer> multiStaffPatientIds = allAdmissions.stream()
                .filter(admission -> multiStaffAdmissionIds.contains(admission.getId()))
                .map(Admission::getPatientID)
                .collect(Collectors.toSet());
        
        // Return detailed information for these patients
        return allPatients.stream()
                .filter(patient -> multiStaffPatientIds.contains(patient.getId()))
                .collect(Collectors.toList());
    }
} 