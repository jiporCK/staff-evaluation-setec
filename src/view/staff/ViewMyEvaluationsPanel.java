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
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        JLabel titleLabel = new JLabel("My Evaluations");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        JLabel subtitleLabel = new JLabel("Evaluations assigned to you");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(new Color(108, 113, 118));

        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
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
        evaluationsTable.setFont(tableFont);
        evaluationsTable.setForeground(rowText);
        evaluationsTable.setRowHeight(28);
        evaluationsTable.setShowHorizontalLines(true);
        evaluationsTable.setShowVerticalLines(false);
        evaluationsTable.setGridColor(new Color(230, 233, 238));
        evaluationsTable.setSelectionBackground(selectionBg);
        evaluationsTable.setSelectionForeground(selectionText);
        evaluationsTable.setIntercellSpacing(new Dimension(0, 6));
        evaluationsTable.getTableHeader().setReorderingAllowed(false);
        evaluationsTable.getTableHeader().setFont(headerFont);
        evaluationsTable.getTableHeader().setBackground(headerBg);
        evaluationsTable.getTableHeader().setForeground(headerText);
        evaluationsTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        evaluationsTable.setFillsViewportHeight(true);
        evaluationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evaluationsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        evaluationsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
        JScrollPane scrollPane = new JScrollPane(evaluationsTable);
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.setOpaque(false);
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
