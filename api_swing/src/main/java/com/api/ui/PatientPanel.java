package com.api.ui;

import com.api.models.Patient;
import com.api.service.ApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Panel for displaying and interacting with patient data.
 */
public class PatientPanel extends JPanel {
    private final ApiService apiService;
    private final JTable patientTable;
    private final DefaultTableModel tableModel;
    private final JTextField idField;
    private final JTextArea detailsArea;
    private final JButton searchButton;
    private final JButton refreshButton;

    public PatientPanel(ApiService apiService) {
        this.apiService = apiService;
        setLayout(new BorderLayout());

        // Create the table model and table
        String[] columnNames = {"ID", "Name", "NHS Number"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedPatientDetails();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(patientTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));

        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Patient ID:"));
        idField = new JTextField(10);
        searchPanel.add(idField);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchPatient());
        searchPanel.add(searchButton);
        refreshButton = new JButton("Refresh All");
        refreshButton.addActionListener(e -> loadAllPatients());
        searchPanel.add(refreshButton);

        // Create the details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Add components to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);

        // Load initial data
        loadAllPatients();
    }

    /**
     * Loads all patients from the API and displays them in the table.
     */
    public void loadAllPatients() {
        SwingWorker<List<Patient>, Void> worker = new SwingWorker<List<Patient>, Void>() {
            @Override
            protected List<Patient> doInBackground() throws Exception {
                return apiService.getAllPatients();
            }

            @Override
            protected void done() {
                try {
                    List<Patient> patients = get();
                    displayPatientsInTable(patients);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(PatientPanel.this,
                            "Error loading patients: " + e.getMessage(),
                            "API Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Searches for a patient by ID.
     */
    private void searchPatient() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a patient ID",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            SwingWorker<Patient, Void> worker = new SwingWorker<Patient, Void>() {
                @Override
                protected Patient doInBackground() throws Exception {
                    return apiService.getPatientById(id);
                }

                @Override
                protected void done() {
                    try {
                        Patient patient = get();
                        if (patient != null) {
                            displayPatientDetails(patient);
                            clearTableSelection();
                            tableModel.setRowCount(0);
                            Object[] rowData = {
                                    patient.getId(),
                                    patient.getForename() + " " + patient.getSurname(),
                                    patient.getNhsNumber()
                            };
                            tableModel.addRow(rowData);
                        } else {
                            JOptionPane.showMessageDialog(PatientPanel.this,
                                    "No patient found with ID: " + id,
                                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(PatientPanel.this,
                                "Error searching for patient: " + e.getMessage(),
                                "API Error", JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric ID",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Displays the details of the selected patient in the details area.
     */
    private void displaySelectedPatientDetails() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) patientTable.getValueAt(selectedRow, 0);
            try {
                Patient patient = apiService.getPatientById(id);
                if (patient != null) {
                    displayPatientDetails(patient);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading patient details: " + e.getMessage(),
                        "API Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a list of patients in the table.
     *
     * @param patients The list of patients to display
     */
    private void displayPatientsInTable(List<Patient> patients) {
        tableModel.setRowCount(0);
        for (Patient patient : patients) {
            Object[] rowData = {
                    patient.getId(),
                    patient.getForename() + " " + patient.getSurname(),
                    patient.getNhsNumber()
            };
            tableModel.addRow(rowData);
        }
        clearPatientDetails();
    }

    /**
     * Displays the details of a patient in the details area.
     *
     * @param patient The patient to display
     */
    private void displayPatientDetails(Patient patient) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(patient.getId()).append("\n");
        sb.append("Name: ").append(patient.getForename()).append(" ").append(patient.getSurname()).append("\n");
        sb.append("NHS Number: ").append(patient.getNhsNumber()).append("\n");
        
        // Load admissions for this patient
        try {
            List<com.api.models.Admission> allAdmissions = apiService.getAllAdmissions();
            sb.append("\nAdmissions:\n");
            boolean hasAdmissions = false;
            
            for (com.api.models.Admission admission : allAdmissions) {
                if (admission.getPatientID() == patient.getId()) {
                    hasAdmissions = true;
                    sb.append("  - Admission #").append(admission.getId())
                      .append(" (").append(admission.getAdmissionDate()).append(" to ");
                    
                    if (admission.getDischargeDate() != null && !admission.getDischargeDate().isEmpty()) {
                        sb.append(admission.getDischargeDate());
                    } else {
                        sb.append("Present");
                    }
                    
                    sb.append(")\n");
                }
            }
            
            if (!hasAdmissions) {
                sb.append("  No admissions found for this patient.\n");
            }
        } catch (IOException e) {
            sb.append("\nError loading admissions: ").append(e.getMessage()).append("\n");
        }
        
        detailsArea.setText(sb.toString());
    }

    /**
     * Clears the patient details area.
     */
    private void clearPatientDetails() {
        detailsArea.setText("");
    }

    /**
     * Clears the table selection.
     */
    private void clearTableSelection() {
        patientTable.clearSelection();
    }
} 