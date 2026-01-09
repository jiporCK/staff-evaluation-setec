package view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.*;
import service.*;

public class ViewEvaluationsPanel extends JPanel {
    private User currentUser;
    private EvaluationService evaluationService;
    private JTable evaluationsTable;
    private DefaultTableModel tableModel;

    public ViewEvaluationsPanel(User user) {
        this.currentUser = user;
        this.evaluationService = new EvaluationService();
        initComponents();
        loadEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("View All Staff Evaluations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Period", "Assigned By", "For Staff", "Assign Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluationsTable = new JTable(tableModel);
        add(new JScrollPane(evaluationsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        viewDetailsButton.addActionListener(e -> viewEvaluationDetails());
        refreshButton.addActionListener(e -> loadEvaluations());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadEvaluations() {
        // Call evaluationService.getAllStaffEvaluations()
        List<AssignStaffEvaluation> evaluations = evaluationService.getAllStaffEvaluations();
        tableModel.setRowCount(0);

        if (evaluations != null) {
            for (AssignStaffEvaluation eval : evaluations) {
                Object[] row = {
                        eval.getId(),
                        eval.getPeriodId(), // Should fetch Period name
                        eval.getAssignByStaffId(), // Should fetch Staff name
                        eval.getForStaffId(), // Should fetch Staff name
                        eval.getAssignDate(),
                        eval.getDescription()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void viewEvaluationDetails() {
        int selectedRow = evaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an evaluation");
            return;
        }

        Long evalId = (Long) tableModel.getValueAt(selectedRow, 0);

        // Call evaluationService.getEvaluationListItems(evalId)
        // Show dialog with evaluators and their scores
        List<AssignStaffEvaluationList> items = evaluationService.getEvaluationListItems(evalId);

        if (items != null && !items.isEmpty()) {
            StringBuilder details = new StringBuilder("Evaluators:\n\n");
            for (AssignStaffEvaluationList item : items) {
                ASEAssignScore score = evaluationService.getScoreByEvaluationListId(item.getId());
                details.append("Evaluator ID: ").append(item.getEvaluationStaffId());
                if (score != null) {
                    details.append(" - Score: ").append(score.getScore());
                } else {
                    details.append(" - Score: Not submitted");
                }
                details.append("\n");
            }

            JOptionPane.showMessageDialog(this, details.toString(),
                    "Evaluation Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
