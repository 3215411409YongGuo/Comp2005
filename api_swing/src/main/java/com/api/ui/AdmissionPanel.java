package com.api.ui;

import com.api.models.Admission;
import com.api.models.Patient;
import com.api.service.ApiService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * Panel for displaying and interacting with admission data.
 */
public class AdmissionPanel extends JPanel {
    private final ApiService apiService;
    private final JTable admissionTable;
    private final DefaultTableModel tableModel;
    private final JTextField idField;
    private final JTextArea detailsArea;
    private final JButton searchButton;
    private final JButton refreshButton;

    public AdmissionPanel(ApiService apiService) {
        this.apiService = apiService;
        setLayout(new BorderLayout());

        // Create the table model and table
        String[] columnNames = {"ID", "Patient ID", "Admission Date", "Discharge Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        admissionTable = new JTable(tableModel);
        admissionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        admissionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedAdmissionDetails();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(admissionTable);
        tableScrollPane.setPreferredSize(new Dimension(400, 300));

        // Create the search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Admission ID:"));
        idField = new JTextField(10);
        searchPanel.add(idField);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchAdmission());
        searchPanel.add(searchButton);
        refreshButton = new JButton("Refresh All");
        refreshButton.addActionListener(e -> loadAllAdmissions());
        searchPanel.add(refreshButton);

        // Create the details panel
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Admission Details"));
        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        JScrollPane detailsScrollPane = new JScrollPane(detailsArea);
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Add components to the main panel
        add(searchPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
        add(detailsPanel, BorderLayout.SOUTH);

        // Load initial data
        loadAllAdmissions();
    }

    /**
     * Loads all admissions from the API and displays them in the table.
     */
    public void loadAllAdmissions() {
        SwingWorker<List<Admission>, Void> worker = new SwingWorker<List<Admission>, Void>() {
            @Override
            protected List<Admission> doInBackground() throws Exception {
                return apiService.getAllAdmissions();
            }

            @Override
            protected void done() {
                try {
                    List<Admission> admissions = get();
                    displayAdmissionsInTable(admissions);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AdmissionPanel.this,
                            "Error loading admissions: " + e.getMessage(),
                            "API Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    /**
     * Searches for an admission by ID.
     */
    private void searchAdmission() {
        String idText = idField.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter an admission ID",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            SwingWorker<Admission, Void> worker = new SwingWorker<Admission, Void>() {
                @Override
                protected Admission doInBackground() throws Exception {
                    return apiService.getAdmissionById(id);
                }

                @Override
                protected void done() {
                    try {
                        Admission admission = get();
                        if (admission != null) {
                            displayAdmissionDetails(admission);
                            clearTableSelection();
                            tableModel.setRowCount(0);
                            Object[] rowData = {
                                    admission.getId(),
                                    admission.getPatientID(),
                                    admission.getAdmissionDate(),
                                    admission.getDischargeDate()
                            };
                            tableModel.addRow(rowData);
                        } else {
                            JOptionPane.showMessageDialog(AdmissionPanel.this,
                                    "No admission found with ID: " + id,
                                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(AdmissionPanel.this,
                                "Error searching for admission: " + e.getMessage(),
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
     * Displays the details of the selected admission in the details area.
     */
    private void displaySelectedAdmissionDetails() {
        int selectedRow = admissionTable.getSelectedRow();
        if (selectedRow >= 0) {
            int id = (int) admissionTable.getValueAt(selectedRow, 0);
            try {
                Admission admission = apiService.getAdmissionById(id);
                if (admission != null) {
                    displayAdmissionDetails(admission);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this,
                        "Error loading admission details: " + e.getMessage(),
                        "API Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    /**
     * Displays a list of admissions in the table.
     *
     * @param admissions The list of admissions to display
     */
    private void displayAdmissionsInTable(List<Admission> admissions) {
        tableModel.setRowCount(0);
        for (Admission admission : admissions) {
            Object[] rowData = {
                    admission.getId(),
                    admission.getPatientID(),
                    admission.getAdmissionDate(),
                    admission.getDischargeDate()
            };
            tableModel.addRow(rowData);
        }
        clearAdmissionDetails();
    }

    /**
     * Displays the details of an admission in the details area.
     *
     * @param admission The admission to display
     */
    private void displayAdmissionDetails(Admission admission) {
        StringBuilder sb = new StringBuilder();
        sb.append("Admission ID: ").append(admission.getId()).append("\n");
        sb.append("Admission Date: ").append(admission.getAdmissionDate()).append("\n");
        sb.append("Discharge Date: ");
        if (admission.getDischargeDate() != null && !admission.getDischargeDate().isEmpty()) {
            sb.append(admission.getDischargeDate());
        } else {
            sb.append("Not discharged yet");
        }
        sb.append("\n\n");

        // Load patient data
        try {
            Patient patient = apiService.getPatientById(admission.getPatientID());
            if (patient != null) {
                sb.append("Patient Information:\n");
                sb.append("  - ID: ").append(patient.getId()).append("\n");
                sb.append("  - Name: ").append(patient.getForename()).append(" ").append(patient.getSurname()).append("\n");
                sb.append("  - NHS Number: ").append(patient.getNhsNumber()).append("\n");
            } else {
                sb.append("Patient not found (ID: ").append(admission.getPatientID()).append(")\n");
            }
        } catch (IOException e) {
            sb.append("Error loading patient data: ").append(e.getMessage()).append("\n");
        }

        // Load allocations for this admission
        try {
            List<com.api.models.Allocation> allAllocations = apiService.getAllAllocations();
            sb.append("\nStaff Allocations:\n");
            boolean hasAllocations = false;

            for (com.api.models.Allocation allocation : allAllocations) {
                if (allocation.getAdmissionID() == admission.getId()) {
                    hasAllocations = true;
                    try {
                        com.api.models.Employee employee = apiService.getEmployeeById(allocation.getEmployeeID());
                        String employeeName = (employee != null) ? 
                                employee.getForename() + " " + employee.getSurname() : 
                                "Unknown (ID: " + allocation.getEmployeeID() + ")";
                        
                        sb.append("  - ").append(employeeName)
                            .append(" (").append(allocation.getStartTime()).append(" to ");
                        
                        if (allocation.getEndTime() != null && !allocation.getEndTime().isEmpty()) {
                            sb.append(allocation.getEndTime());
                        } else {
                            sb.append("Present");
                        }
                        
                        sb.append(")\n");
                    } catch (IOException e) {
                        sb.append("  - Employee ").append(allocation.getEmployeeID())
                          .append(" (Error loading details: ").append(e.getMessage()).append(")\n");
                    }
                }
            }
            
            if (!hasAllocations) {
                sb.append("  No staff allocations found for this admission.\n");
            }
        } catch (IOException e) {
            sb.append("\nError loading allocations: ").append(e.getMessage()).append("\n");
        }
        
        detailsArea.setText(sb.toString());
    }

    /**
     * Clears the admission details area.
     */
    private void clearAdmissionDetails() {
        detailsArea.setText("");
    }

    /**
     * Clears the table selection.
     */
    private void clearTableSelection() {
        admissionTable.clearSelection();
    }
} 