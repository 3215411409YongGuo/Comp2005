package com.api.config;

import com.api.model.Admission;
import com.api.model.Allocation;
import com.api.model.Employee;
import com.api.model.Patient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Configuration
@Profile("test")
public class TestConfiguration {

    @Bean
    @Primary
    public WebClient webClientMock() {
        // Create test data
        Patient patient1 = new Patient();
        patient1.setId(1);
        patient1.setSurname("Smith");
        patient1.setForename("John");
        
        Patient patient2 = new Patient();
        patient2.setId(2);
        patient2.setSurname("Johnson");
        patient2.setForename("Mary");
        
        Admission admission1 = new Admission();
        admission1.setId(101);
        admission1.setPatientID(1);
        admission1.setAdmissionDate("2023-01-01T10:00:00");
        admission1.setDischargeDate("2023-01-10T14:00:00");
        
        Admission admission2 = new Admission();
        admission2.setId(102);
        admission2.setPatientID(1);
        admission2.setAdmissionDate("2023-01-15T09:00:00");
        admission2.setDischargeDate("2023-01-20T16:00:00");
        
        Employee employee1 = new Employee();
        employee1.setId(301);
        
        Employee employee2 = new Employee();
        employee2.setId(302);
        
        Allocation allocation1 = new Allocation();
        allocation1.setId(201);
        allocation1.setAdmissionID(101);
        allocation1.setEmployeeID(301);
        
        Allocation allocation2 = new Allocation();
        allocation2.setId(202);
        allocation2.setAdmissionID(101);
        allocation2.setEmployeeID(302);
        
        // Mock WebClient
        WebClient webClientMock = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpec = Mockito.mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);
        
        // Set up basic mock behavior
        when(webClientMock.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersUriSpec.uri(anyString(), anyInt())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Mock API responses
        when(responseSpec.bodyToMono(Patient[].class)).thenReturn(Mono.just(new Patient[]{patient1, patient2}));
        when(responseSpec.bodyToMono(Admission[].class)).thenReturn(Mono.just(new Admission[]{admission1, admission2}));
        when(responseSpec.bodyToMono(Employee[].class)).thenReturn(Mono.just(new Employee[]{employee1, employee2}));
        when(responseSpec.bodyToMono(Allocation[].class)).thenReturn(Mono.just(new Allocation[]{allocation1, allocation2}));
        
        // Mock single entity responses
        when(responseSpec.bodyToMono(Patient.class)).thenReturn(Mono.just(patient1));
        when(responseSpec.bodyToMono(Admission.class)).thenReturn(Mono.just(admission1));
        when(responseSpec.bodyToMono(Employee.class)).thenReturn(Mono.just(employee1));
        when(responseSpec.bodyToMono(Allocation.class)).thenReturn(Mono.just(allocation1));
        
        return webClientMock;
    }
} 