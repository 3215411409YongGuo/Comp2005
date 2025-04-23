package com.api;

import com.api.models.AdmissionTest;
import com.api.models.PatientTest;
import com.api.service.ApiServiceTest;
import com.api.system.MaternityApiSystemTest;
import com.api.ui.PatientPanelTest;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Integration Test Suite for the Maternity API Application.
 * This suite runs all test classes together to verify application functionality.
 * 
 * Note: If tests fail, check the following issues:
 * 1. Mock objects may need to be set to LENIENT mode
 * 2. When verifying method call counts, use atLeastOnce() instead of exact counts
 * 3. In UI tests, mock objects may need to be reset before each test
 */
@Suite
@SuiteDisplayName("Maternity API Integration Tests")
@SelectClasses({
    // First run model layer tests
    PatientTest.class,
    AdmissionTest.class,
    // Then service layer tests
    ApiServiceTest.class,
    // Then UI layer tests
    PatientPanelTest.class,
    // Finally system tests
    MaternityApiSystemTest.class
})
public class MaternityApiIntegrationTest {
    
    // JUnit 5 provides the @Suite annotation to run multiple test classes together,
    // so we don't need to add any test methods here.
    // This class serves as a container for the test suite configuration.
    
    @Test
    public void verifyTestSuiteConfiguration() {
        // This method is just a placeholder to ensure the test class is recognized
        // and to document the purpose of this class.
        System.out.println("Running Maternity API Integration Test Suite");
    }
} 