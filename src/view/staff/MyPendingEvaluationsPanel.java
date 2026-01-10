//package view.staff;
//
//import model.ASEAssignScore;
//import model.AssignStaffEvaluation;
//import model.AssignStaffEvaluationList;
//import service.EvaluationService;
//import service.StaffService;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.math.BigDecimal;
//import java.util.List;
//
//public class MyPendingEvaluationsPanel extends JPanel {
//
//    private EvaluationService evaluationService;
//    private StaffService staffService;
//    private JTable table;
//    private DefaultTableModel tableModel;
//    private Long currentStaffId;
//    private Long currentCompanyId;
//
//    public MyPendingEvaluationsPanel(Long staffId, Long companyId) {
//        this.evaluationService = new EvaluationService();
//        this.staffService = new StaffService();
//        this.currentStaffId = staffId;
//        this.currentCompanyId = companyId;
//
//        setLayout(new BorderLayout(10, 10));
//        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        // Title
//        JLabel titleLabel = new JLabel("My Pending Evaluations");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        JLabel subtitleLabel = new JLabel("Evaluations where you need to submit scores");
//        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
//
//        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
//        headerPanel.add(titleLabel);
//        headerPanel.add(subtitleLabel);
//        add(headerPanel, BorderLayout.NORTH);
//
//        // Table
//        String[] columns = {"List ID", "Evaluation ID", "Staff Being Evaluated", "Created At", "Status"};
//        tableModel = new DefaultTableModel(columns, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//        table = new JTable(tableModel);
//        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//        JScrollPane scrollPane = new JScrollPane(table);
//        add(scrollPane, BorderLayout.CENTER);
//
//        // Button Panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JButton submitScoreButton = new JButton("Submit Score");
//        JButton viewDetailsButton = new JButton("View Details");
//        JButton refreshButton = new JButton("Refresh");
//
//        submitScoreButton.addActionListener(e -> openSubmitScoreDialog());
//        viewDetailsButton.addActionListener(e -> viewEvaluationDetails());
//        refreshButton.addActionListener(e -> loadData());
//
//        buttonPanel.add(submitScoreButton);
//        buttonPanel.add(viewDetailsButton);
//        buttonPanel.add(refreshButton);
//        add(buttonPanel, BorderLayout.SOUTH);
//
//        loadData();
//    }
//
//    private void loadData() {
//        tableModel.setRowCount(0);
//        List<AssignStaffEvaluationList> pendingList = evaluationService.getPendingEvaluations(currentStaffId);
//
//        for (AssignStaffEvaluationList item : pendingList) {
//            // Get the evaluation details to show which staff is being evaluated
//            List<AssignStaffEvaluation> evaluations = evaluationService.getAllStaffEvaluations();
//            String staffName = "N/A";
//
//            for (AssignStaffEvaluation ase : evaluations) {
//                if (ase.getId().equals(item.getAssignStaffEvaluationId())) {
//                    staffName = staffService.getStaffNameById(ase.getForStaffId());
//                    break;
//                }
//            }
//
//            tableModel.addRow(new Object[]{
//                    item.getId(),
//                    item.getAssignStaffEvaluationId(),
//                    staffName,
//                    item.getCreatedAt(),
//                    "Pending"
//            });
//        }
//
//        if (pendingList.isEmpty()) {
//            JOptionPane.showMessageDialog(this,
//                    "No pending evaluations. You're all caught up!",
//                    "Info",
//                    JOptionPane.INFORMATION_MESSAGE);
//        }
//    }
//
//    private void openSubmitScoreDialog() {
//        int selectedRow = table.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an evaluation to submit a score");
//            return;
//        }
//
//        Long listId = (Long) tableModel.getValueAt(selectedRow, 0);
//        Long evaluationId = (Long) tableModel.getValueAt(selectedRow, 1);
//        String staffName = (String) tableModel.getValueAt(selectedRow, 2);
//
//        // Open submit score dialog
//        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Submit Score", true);
//        dialog.setLayout(new GridLayout(5, 2, 10, 10));
//        dialog.setSize(450, 220);
//        dialog.getContentPane().setBackground(Color.WHITE);
//
//        JLabel titleLabel = new JLabel("Submit Evaluation Score");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
//
//        JLabel staffLabel = new JLabel("Staff Being Evaluated:");
//        JLabel staffValueLabel = new JLabel(staffName);
//        staffValueLabel.setFont(new Font("Arial", Font.BOLD, 12));
//
//        JLabel scoreLabel = new JLabel("Score:");
//        JTextField scoreField = new JTextField();
//
//        JButton submitButton = new JButton("Submit");
//        JButton cancelButton = new JButton("Cancel");
//
//        submitButton.addActionListener(e -> {
//            try {
//                BigDecimal score = new BigDecimal(scoreField.getText().trim());
//
//                if (score.compareTo(BigDecimal.ZERO) < 0 || score.compareTo(BigDecimal.valueOf(100)) > 0) {
//                    JOptionPane.showMessageDialog(dialog, "Score must be between 0 and 100");
//                    return;
//                }
//
//                ASEAssignScore newScore = new ASEAssignScore();
//                newScore.setAssignStaffEvaluationListId(listId);
//                newScore.setCompanyId(currentCompanyId);
//                newScore.setScore(score);
//
//                boolean success = evaluationService.submitScore(newScore);
//
//                if (success) {
//                    JOptionPane.showMessageDialog(dialog,
//                            "Score submitted successfully for " + staffName);
//                    dialog.dispose();
//                    loadData();
//                } else {
//                    JOptionPane.showMessageDialog(dialog,
//                            "Failed to submit score. Please try again.");
//                }
//            } catch (NumberFormatException ex) {
//                JOptionPane.showMessageDialog(dialog,
//                        "Please enter a valid numeric score");
//            } catch (Exception ex) {
//                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
//            }
//        });
//
//        cancelButton.addActionListener(e -> dialog.dispose());
//
//        dialog.add(titleLabel);
//        dialog.add(new JLabel());
//        dialog.add(staffLabel);
//        dialog.add(staffValueLabel);
//        dialog.add(scoreLabel);
//        dialog.add(scoreField);
//        dialog.add(new JLabel());
//        dialog.add(new JLabel());
//        dialog.add(submitButton);
//        dialog.add(cancelButton);
//
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
//    }
//
//    private void viewEvaluationDetails() {
//        int selectedRow = table.getSelectedRow();
//        if (selectedRow == -1) {
//            JOptionPane.showMessageDialog(this, "Please select an evaluation to view details");
//            return;
//        }
//
//        Long evaluationId = (Long) tableModel.getValueAt(selectedRow, 1);
//
//        // Open EvaluationDetailPanel
//        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Evaluation Details", true);
//        dialog.setSize(700, 500);
//
//        // Create and add EvaluationDetailPanel
//        EvaluationDetailPanel detailPanel = new EvaluationDetailPanel(evaluationId, currentCompanyId);
//        dialog.add(detailPanel);
//
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
//    }
//}