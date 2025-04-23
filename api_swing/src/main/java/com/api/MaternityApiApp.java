package com.api;

import com.api.service.ApiService;
import com.api.ui.AdmissionPanel;
import com.api.ui.PatientPanel;
import com.api.ui.UsabilityFeedbackPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Main application class for the Maternity API Swing application.
 */
public class MaternityApiApp {
    private final ApiService apiService;
    private final JFrame mainFrame;
    private final JTabbedPane tabbedPane;

    public MaternityApiApp() {
        // Initialize the API service
        apiService = new ApiService();

        // Create the main application frame
        mainFrame = new JFrame("Maternity API Application");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 700);
        mainFrame.setLocationRelativeTo(null);

        // Create the tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add the patient panel
        PatientPanel patientPanel = new PatientPanel(apiService);
        tabbedPane.addTab("Patients", new ImageIcon(), patientPanel, "View and search for patients");
        
        // Add the admission panel
        AdmissionPanel admissionPanel = new AdmissionPanel(apiService);
        tabbedPane.addTab("Admissions", new ImageIcon(), admissionPanel, "View and search for admissions");
        
        // Add the usability feedback panel
        UsabilityFeedbackPanel feedbackPanel = new UsabilityFeedbackPanel();
        tabbedPane.addTab("Usability Feedback", new ImageIcon(), feedbackPanel, "Provide feedback on the application");

        // Add the tabbed pane to the frame
        mainFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        // Add window close listener to properly close resources
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    apiService.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * Shows the main application window.
     */
    public void show() {
        mainFrame.setVisible(true);
    }

    /**
     * Application entry point.
     *
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set the look and feel to the system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch the application on the EDT
        SwingUtilities.invokeLater(() -> {
            try {
                MaternityApiApp app = new MaternityApiApp();
                app.show();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Error starting application: " + e.getMessage(),
                        "Application Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
} 