package view.staff;

import model.*;
import service.EvaluationService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitScoresPanel extends JPanel {
    private User currentUser;
    private Staff staff;
    private StaffService staffService;
    private EvaluationService evaluationService;

    private JTable pendingEvaluationsTable;
    private DefaultTableModel tableModel;

    public SubmitScoresPanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        this.evaluationService = new EvaluationService();
        this.staff = staffService.getStaffByUserId(currentUser.getId());
        initComponents();
        loadPendingEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Submit Evaluation Scores");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Eval ID", "Staff Being Evaluated", "Criteria Count", "Scores Submitted", "Progress", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        pendingEvaluationsTable = new JTable(tableModel);
        add(new JScrollPane(pendingEvaluationsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton submitScoreButton = new JButton("Submit Scores for Selected");
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        submitScoreButton.addActionListener(e -> submitScoresForEvaluation());
        viewDetailsButton.addActionListener(e -> viewEvaluationDetails());
        refreshButton.addActionListener(e -> loadPendingEvaluations());

        buttonPanel.add(submitScoreButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadPendingEvaluations() {
        tableModel.setRowCount(0);
        if (staff == null) {
            JOptionPane.showMessageDialog(this,
                    "Staff profile not found for current user",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get all evaluations where this staff is an evaluator
        List<AssignStaffEvaluationList> allEvaluations = new ArrayList<>();
        List<AssignStaffEvaluation> staffEvals = evaluationService.getAllStaffEvaluations();

        for (AssignStaffEvaluation ase : staffEvals) {
            List<AssignStaffEvaluationList> evaluators =
                    evaluationService.getEvaluationListItems(ase.getId());

            for (AssignStaffEvaluationList evaluator : evaluators) {
                if (evaluator.getEvaluationStaffId().equals(staff.getId())) {
                    allEvaluations.add(evaluator);

                    // Get evaluation details
                    String staffName = staffService.getStaffNameById(ase.getForStaffId());

                    // Get assigned evaluation points
                    List<EvaluationPoint> points =
                            evaluationService.getAssignedEvaluationPoints(ase.getId());

                    // Get submitted scores for this evaluator
                    List<ASEAssignScore> submittedScores =
                            evaluationService.getScoresByEvaluationListId(evaluator.getId());

                    int totalCriteria = points.size();
                    int submittedCount = submittedScores.size();

                    String progress = submittedCount + "/" + totalCriteria;
                    String status = submittedCount == totalCriteria ? "Completed" :
                            submittedCount > 0 ? "In Progress" : "Pending";

                    tableModel.addRow(new Object[]{
                            ase.getId(),
                            staffName,
                            totalCriteria,
                            submittedCount,
                            progress,
                            status
                    });

                    break; // Only add once per evaluation
                }
            }
        }

        if (allEvaluations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No evaluations assigned. You're all caught up!",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void submitScoresForEvaluation() {
        int selectedRow = pendingEvaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an evaluation");
            return;
        }

        Long evalId = (Long) tableModel.getValueAt(selectedRow, 0);
        String staffName = (String) tableModel.getValueAt(selectedRow, 1);

        // Find the evaluation
        AssignStaffEvaluation evaluation = findEvaluationById(evalId);
        if (evaluation == null) {
            JOptionPane.showMessageDialog(this, "Evaluation not found");
            return;
        }

        // Find the evaluator list item for this staff
        AssignStaffEvaluationList evaluatorListItem = null;
        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evalId);

        for (AssignStaffEvaluationList item : evaluators) {
            if (item.getEvaluationStaffId().equals(staff.getId())) {
                evaluatorListItem = item;
                break;
            }
        }

        if (evaluatorListItem == null) {
            JOptionPane.showMessageDialog(this, "You are not assigned as an evaluator for this evaluation");
            return;
        }

        // Get evaluation points for this evaluation
        List<EvaluationPoint> points =
                evaluationService.getAssignedEvaluationPoints(evalId);

        if (points.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No evaluation criteria assigned to this evaluation");
            return;
        }

        // Show scoring dialog
        showScoringDialog(evaluatorListItem, points, staffName);
    }

    private void showScoringDialog(AssignStaffEvaluationList evaluatorListItem,
                                   List<EvaluationPoint> points, String staffName) {

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Submit Scores for " + staffName, true);
        dialog.setSize(700, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel headerLabel = new JLabel("Evaluating: " + staffName);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(headerLabel);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Table for scoring
        String[] columns = {"Criterion", "Range", "Weight", "Current Score", "New Score", "Comment"};
        DefaultTableModel scoreTableModel = new DefaultTableModel(columns, 0);
        JTable scoreTable = new JTable(scoreTableModel);

        // Load current scores
        Map<Long, ASEAssignScore> existingScores = new HashMap<>();
        List<ASEAssignScore> scores =
                evaluationService.getScoresByEvaluationListId(evaluatorListItem.getId());

        for (ASEAssignScore score : scores) {
            existingScores.put(score.getEvaluationPointId(), score);
        }

        // Populate table
        for (EvaluationPoint point : points) {
            ASEAssignScore existing = existingScores.get(point.getId());

            String currentScore = existing != null ?
                    existing.getScore().toString() : "Not submitted";
            String currentComment = existing != null && existing.getComment() != null ?
                    existing.getComment() : "";

            scoreTableModel.addRow(new Object[]{
                    point.getName(),
                    point.getScoreRangeFrom() + " - " + point.getScoreRangeTo(),
                    point.getWeight(),
                    currentScore,
                    existing != null ? existing.getScore().toString() : "",
                    currentComment
            });
        }

        // Make only "New Score" and "Comment" columns editable
        JTable editableScoreTable = new JTable(scoreTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4 || column == 5; // New Score and Comment columns
            }
        };

        editableScoreTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(editableScoreTable);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Instructions panel
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        instructionPanel.add(new JLabel("Instructions:"));
        instructionPanel.add(new JLabel("• Enter scores in the 'New Score' column within the specified range"));
        instructionPanel.add(new JLabel("• Optionally add comments for each criterion"));
        instructionPanel.add(new JLabel("• Click 'Save All Scores' when done"));

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save All Scores");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {

            // ✅ FORCE commit of the currently edited cell
            if (editableScoreTable.isEditing()) {
                editableScoreTable.getCellEditor().stopCellEditing();
            }

            if (saveScoresFromTable(editableScoreTable, points, evaluatorListItem)) {
                dialog.dispose();
                loadPendingEvaluations();
                JOptionPane.showMessageDialog(this,
                        "Scores saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });


        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(instructionPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(bottomPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private boolean saveScoresFromTable(JTable table, List<EvaluationPoint> points,
                                        AssignStaffEvaluationList evaluatorListItem) {
        try {
            int successCount = 0;
            int totalRows = points.size();

            for (int i = 0; i < totalRows; i++) {
                EvaluationPoint point = points.get(i);

                // Get new score from table
                Object newScoreObj = table.getValueAt(i, 4); // New Score column
                if (newScoreObj == null || newScoreObj.toString().trim().isEmpty()) {
                    continue; // Skip if no score entered
                }

                String newScoreStr = newScoreObj.toString().trim();
                BigDecimal newScore;

                try {
                    newScore = new BigDecimal(newScoreStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid score for '" + point.getName() + "': " + newScoreStr,
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // Validate range
                if (newScore.compareTo(point.getScoreRangeFrom()) < 0 ||
                        newScore.compareTo(point.getScoreRangeTo()) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Score for '" + point.getName() + "' must be between " +
                                    point.getScoreRangeFrom() + " and " + point.getScoreRangeTo(),
                            "Invalid Range",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // Get comment
                Object commentObj = table.getValueAt(i, 5);
                String comment = commentObj != null ? commentObj.toString().trim() : "";

                // Check if score already exists
                ASEAssignScore existingScore =
                        evaluationService.getScore(evaluatorListItem.getId(), point.getId());

                boolean success;
                if (existingScore != null) {
                    // Update existing
                    existingScore.setScore(newScore);
                    existingScore.setComment(comment);
                    success = evaluationService.updateScore(existingScore);
                } else {
                    // Create new
                    ASEAssignScore newScoreObj2 = new ASEAssignScore();
                    newScoreObj2.setAssignStaffEvaluationListId(evaluatorListItem.getId());
                    newScoreObj2.setEvaluationPointId(point.getId());
                    newScoreObj2.setCompanyId(currentUser.getCompanyId());
                    newScoreObj2.setScore(newScore);
                    newScoreObj2.setComment(comment);
                    success = evaluationService.submitScore(newScoreObj2);
                }

                if (success) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "No scores were saved. Please enter at least one score.",
                        "No Data",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving scores: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private void viewEvaluationDetails() {
        int selectedRow = pendingEvaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an evaluation");
            return;
        }

        Long evalId = (Long) tableModel.getValueAt(selectedRow, 0);
        String staffName = (String) tableModel.getValueAt(selectedRow, 1);

        // Find the evaluator list item
        AssignStaffEvaluationList evaluatorListItem = null;
        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evalId);

        for (AssignStaffEvaluationList item : evaluators) {
            if (item.getEvaluationStaffId().equals(staff.getId())) {
                evaluatorListItem = item;
                break;
            }
        }

        if (evaluatorListItem == null) {
            return;
        }

        // Get scores
        List<ASEAssignScore> scores =
                evaluationService.getScoresByEvaluationListId(evaluatorListItem.getId());
        List<EvaluationPoint> points =
                evaluationService.getAssignedEvaluationPoints(evalId);

        StringBuilder details = new StringBuilder();
        details.append("Evaluation for: ").append(staffName).append("\n\n");
        details.append("Your Submitted Scores:\n");
        details.append("=".repeat(50)).append("\n\n");

        Map<Long, ASEAssignScore> scoreMap = new HashMap<>();
        for (ASEAssignScore score : scores) {
            scoreMap.put(score.getEvaluationPointId(), score);
        }

        for (EvaluationPoint point : points) {
            details.append(point.getName()).append(":\n");
            ASEAssignScore score = scoreMap.get(point.getId());
            if (score != null) {
                details.append("  Score: ").append(score.getScore()).append("\n");
                if (score.getComment() != null && !score.getComment().isEmpty()) {
                    details.append("  Comment: ").append(score.getComment()).append("\n");
                }
            } else {
                details.append("  Not submitted yet\n");
            }
            details.append("\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Evaluation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private AssignStaffEvaluation findEvaluationById(Long id) {
        List<AssignStaffEvaluation> all = evaluationService.getAllStaffEvaluations();
        for (AssignStaffEvaluation eval : all) {
            if (eval.getId().equals(id)) {
                return eval;
            }
        }
        return null;
    }
}