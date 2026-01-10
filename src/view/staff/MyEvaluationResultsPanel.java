package view.staff;

import model.AssignStaffEvaluation;
import model.Period;
import service.EvaluationService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class MyEvaluationResultsPanel extends JPanel {

    private EvaluationService evaluationService;
    private PeriodService periodService;
    private StaffService staffService;
    private JTable table;
    private DefaultTableModel tableModel;
    private Long currentStaffId;
    private Long currentCompanyId;

    public MyEvaluationResultsPanel(Long staffId, Long companyId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.currentStaffId = staffId;
        this.currentCompanyId = companyId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("My Evaluation Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        JLabel subtitleLabel = new JLabel("View your performance evaluations and scores");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Evaluation ID", "Period", "Assigned By", "Average Score", "Status", "Assign Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        viewDetailsButton.addActionListener(e -> viewDetails());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);

        // Get all evaluations for this staff member
        List<AssignStaffEvaluation> evaluations = evaluationService.getEvaluationsForStaff(currentStaffId);

        for (AssignStaffEvaluation eval : evaluations) {
            if (eval.getCompanyId().equals(currentCompanyId)) {
                Period period = periodService.getPeriodById(eval.getPeriodId());
                String periodCode = period != null ? period.getCode() : "N/A";

                // Get assigned by staff name
                String assignedByName = staffService.getStaffNameById(eval.getAssignByStaffId());

                // Calculate average score for this evaluation
                BigDecimal avgScore = evaluationService.calculateAverageScore(currentStaffId, eval.getPeriodId());

                // Determine status (simple check if score exists)
                String status = avgScore.compareTo(BigDecimal.ZERO) > 0 ? "Completed" : "Pending";

                tableModel.addRow(new Object[]{
                        eval.getId(),
                        periodCode,
                        assignedByName,
                        avgScore.compareTo(BigDecimal.ZERO) > 0 ?
                                String.format("%.2f", avgScore) : "N/A",
                        status,
                        eval.getAssignDate()
                });
            }
        }

        if (evaluations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No evaluations found for your account",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an evaluation to view details");
            return;
        }

        Long evaluationId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Open detail dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Evaluation Details", true);
        dialog.setSize(700, 500);

        // Add EvaluationDetailPanel
        EvaluationDetailPanel detailPanel = new EvaluationDetailPanel(evaluationId, currentCompanyId);
        dialog.add(detailPanel);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}