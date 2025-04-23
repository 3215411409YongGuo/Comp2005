package com.api.controller;

import com.api.model.Patient;
import com.api.service.MaternityBusinessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maternity")
@Tag(name = "Maternity API", description = "Maternity Ward API endpoints")
public class MaternityController {

    private final MaternityBusinessService businessService;

    @Autowired
    public MaternityController(MaternityBusinessService businessService) {
        this.businessService = businessService;
    }
    
    @GetMapping
    public RedirectView redirectToRoot() {
        return new RedirectView("/");
    }

    @Operation(summary = "Get patients never admitted", description = "Returns a list of patients who have never been admitted to the hospital")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/patients/never-admitted")
    public List<Patient> getPatientsNeverAdmitted() {
        return businessService.getPatientsNeverAdmitted();
    }

    @Operation(summary = "Get patients readmitted within 7 days", description = "Returns a list of patients who were readmitted within 7 days of discharge")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/patients/readmitted-within-7-days")
    public List<Patient> getPatientsReadmittedWithin7Days() {
        return businessService.getPatientsReadmittedWithin7Days();
    }

    @Operation(summary = "Get month with most admissions", description = "Returns the month with the highest number of admissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved month data",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/admissions/month-with-most")
    public Map<String, Integer> getMonthWithMostAdmissions() {
        return businessService.getMonthWithMostAdmissions();
    }

    @Operation(summary = "Get patients with multiple staff", description = "Returns a list of patients who have more than one staff member assigned to them")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of patients",
                    content = { @Content(mediaType = "application/json", schema = @Schema(implementation = Patient.class)) }),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/patients/with-multiple-staff")
    public List<Patient> getPatientsWithMultipleStaff() {
        return businessService.getPatientsWithMultipleStaff();
    }
} 