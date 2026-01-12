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
    private final Color panelBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color headerBg = new Color(233, 236, 241);
    private final Color headerText = new Color(33, 37, 41);
    private final Color rowAlt = new Color(248, 249, 252);
    private final Color rowText = new Color(40, 45, 50);
    private final Color selectionBg = new Color(214, 220, 230);
    private final Color selectionText = new Color(20, 24, 28);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 13);

    public MyEvaluationResultsPanel(Long staffId, Long companyId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.currentStaffId = staffId;
        this.currentCompanyId = companyId;

        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        // Title
        JLabel titleLabel = new JLabel("My Evaluation Results");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        JLabel subtitleLabel = new JLabel("View your performance evaluations and scores");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(new Color(108, 113, 118));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
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
        table.setFont(tableFont);
        table.setForeground(rowText);
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 233, 238));
        table.setSelectionBackground(selectionBg);
        table.setSelectionForeground(selectionText);
        table.setIntercellSpacing(new Dimension(0, 6));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(headerText);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? cardBg : rowAlt);
                }
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardBg);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardBg);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.setOpaque(false);
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
