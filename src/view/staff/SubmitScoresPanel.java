package view.staff;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import model.*;
import service.*;

public class SubmitScoresPanel extends JPanel {
    private User currentUser;
    private EvaluationService evaluationService;
    private JTable pendingEvaluationsTable;
    private DefaultTableModel tableModel;

    public SubmitScoresPanel(User user) {
        this.currentUser = user;
        this.evaluationService = new EvaluationService();
        initComponents();
        loadPendingEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Submit Evaluation Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Eval List ID", "Evaluation ID", "Staff Being Evaluated", "Current Score"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingEvaluationsTable = new JTable(tableModel);
        add(new JScrollPane(pendingEvaluationsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton submitScoreButton = new JButton("Submit/Update Score");
        JButton refreshButton = new JButton("Refresh");

        submitScoreButton.addActionListener(e -> submitScore());
        refreshButton.addActionListener(e -> loadPendingEvaluations());

        buttonPanel.add(submitScoreButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPendingEvaluations() {
        // Note: Need to get Staff ID from current user
        // Long evaluatorStaffId = getStaffIdFromUser(currentUser);
        // List<AssignStaffEvaluationList> pending = evaluationService.getPendingEvaluations(evaluatorStaffId);

        tableModel.setRowCount(0);

        // Populate table with pending evaluations
    }

    private void submitScore() {
        int selectedRow = pendingEvaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an evaluation");
            return;
        }

        Long evalListId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Show dialog to enter score
        JTextField scoreField = new JTextField(10);
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 5));
        panel.add(new JLabel("Score (1-5):"));
        panel.add(scoreField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Submit Score",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                BigDecimal score = new BigDecimal(scoreField.getText());

                // Check if score already exists
                ASEAssignScore existingScore = evaluationService.getScoreByEvaluationListId(evalListId);

                if (existingScore != null) {
                    // Update existing score
                    existingScore.setScore(score);
                    boolean success = evaluationService.updateScore(existingScore);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Score updated successfully!");
                        loadPendingEvaluations();
                    }
                } else {
                    // Submit new score
                    ASEAssignScore newScore = new ASEAssignScore();
                    newScore.setAssignStaffEvaluationListId(evalListId);
                    newScore.setCompanyId(currentUser.getCompanyId());
                    newScore.setScore(score);

                    boolean success = evaluationService.submitScore(newScore);
                    if (success) {
                        JOptionPane.showMessageDialog(this, "Score submitted successfully!");
                        loadPendingEvaluations();
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
