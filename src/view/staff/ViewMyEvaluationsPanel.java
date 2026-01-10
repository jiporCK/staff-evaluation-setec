package view.staff;

import model.AssignStaffEvaluation;
import model.Period;
import model.Staff;
import model.User;
import service.EvaluationService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ViewMyEvaluationsPanel extends JPanel {
    private User currentUser;
    private Staff staff;
    private StaffService staffService;
    private EvaluationService evaluationService;
    private PeriodService periodService;

    private JTable evaluationsTable;
    private DefaultTableModel tableModel;

    public ViewMyEvaluationsPanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staff = staffService.getStaffByUserId(currentUser.getId());
        initComponents();
        loadMyEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Evaluations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel subtitleLabel = new JLabel("Evaluations assigned to you");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 12));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"Evaluation ID", "Period", "Assigned By", "Assign Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluationsTable = new JTable(tableModel);
        evaluationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(evaluationsTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton viewDetailsButton = new JButton("View Details");
        JButton refreshButton = new JButton("Refresh");

        viewDetailsButton.addActionListener(e -> viewDetails());
        refreshButton.addActionListener(e -> loadMyEvaluations());

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMyEvaluations() {
        tableModel.setRowCount(0);
        if (staff == null) {
            JOptionPane.showMessageDialog(this,
                    "Staff profile not found",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<AssignStaffEvaluation> evaluations = evaluationService.getEvaluationsForStaff(staff.getId());

        for (AssignStaffEvaluation eval : evaluations) {
            Period period = periodService.getPeriodById(eval.getPeriodId());
            String periodCode = period != null ? period.getCode() : "N/A";

            String assignedByName = staffService.getStaffNameById(eval.getAssignByStaffId());

            tableModel.addRow(new Object[]{
                    eval.getId(),
                    periodCode,
                    assignedByName,
                    eval.getAssignDate()
            });
        }

        if (evaluations.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No evaluations assigned to you yet",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewDetails() {
        int selectedRow = evaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluation to view details");
            return;
        }

        Long evaluationId = (Long) tableModel.getValueAt(selectedRow, 0);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Evaluation Details", true);
        dialog.setSize(700, 500);

        EvaluationDetailPanel detailPanel = new EvaluationDetailPanel(
                evaluationId,
                currentUser.getCompanyId());
        dialog.add(detailPanel);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}