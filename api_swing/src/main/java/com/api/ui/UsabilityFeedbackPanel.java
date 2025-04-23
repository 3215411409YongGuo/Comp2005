package com.api.ui;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Panel for collecting usability feedback from users.
 */
public class UsabilityFeedbackPanel extends JPanel {
    private final JTextArea feedbackArea;
    private final JComboBox<String> taskComboBox;
    private final JSlider difficultySlider;
    private final JButton submitButton;
    private final String[] tasks = {
            "Search for a patient by ID",
            "View all patients",
            "View patient details and admissions",
            "Search for an admission by ID",
            "View all admissions",
            "View admission details and staff allocations",
            "Other (please specify in comments)"
    };

    public UsabilityFeedbackPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title label
        JLabel titleLabel = new JLabel("Usability Feedback");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Task selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Task performed:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        taskComboBox = new JComboBox<>(tasks);
        formPanel.add(taskComboBox, gbc);

        // Difficulty slider
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Task difficulty:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        difficultySlider = new JSlider(JSlider.HORIZONTAL, 1, 5, 3);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);
        difficultySlider.setSnapToTicks(true);

        // Add labels to the slider
        java.util.Hashtable<Integer, JLabel> labelTable = new java.util.Hashtable<>();
        labelTable.put(1, new JLabel("Very Easy"));
        labelTable.put(2, new JLabel("Easy"));
        labelTable.put(3, new JLabel("Moderate"));
        labelTable.put(4, new JLabel("Difficult"));
        labelTable.put(5, new JLabel("Very Difficult"));
        difficultySlider.setLabelTable(labelTable);

        formPanel.add(difficultySlider, gbc);

        // Feedback text area
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        feedbackArea = new JTextArea(10, 30);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Comments and Suggestions"));
        formPanel.add(scrollPane, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Submit button
        submitButton = new JButton("Submit Feedback");
        submitButton.addActionListener(e -> submitFeedback());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Submits the feedback and saves it to a log file.
     */
    private void submitFeedback() {
        String task = (String) taskComboBox.getSelectedItem();
        int difficulty = difficultySlider.getValue();
        String comments = feedbackArea.getText().trim();

        if (comments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please provide some comments before submitting.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Format the feedback
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = dateFormat.format(new Date());
        
        StringBuilder feedback = new StringBuilder();
        feedback.append("=== Usability Feedback ===\n");
        feedback.append("Date: ").append(timestamp).append("\n");
        feedback.append("Task: ").append(task).append("\n");
        feedback.append("Difficulty: ").append(difficulty).append("/5\n");
        feedback.append("Comments:\n").append(comments).append("\n\n");

        // Save to log file
        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdir();
            }
            
            File logFile = new File(logsDir, "usability_feedback.log");
            boolean fileExists = logFile.exists();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                if (!fileExists) {
                    writer.write("# Maternity Web-Service API Usability Feedback Log\n\n");
                }
                writer.write(feedback.toString());
                writer.flush();
            }
            
            JOptionPane.showMessageDialog(this,
                    "Thank you for your feedback! It has been recorded.",
                    "Feedback Submitted", JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            taskComboBox.setSelectedIndex(0);
            difficultySlider.setValue(3);
            feedbackArea.setText("");
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving feedback: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
} 