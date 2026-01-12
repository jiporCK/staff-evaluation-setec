package view.admin;

import model.*;
import service.EvaluationService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignEvaluationsPanel extends JPanel {

    private EvaluationService evaluationService;
    private PeriodService periodService;
    private StaffService staffService;
    private JComboBox<PeriodItem> periodCombo;
    private JList<StaffItem> staffList;
    private DefaultListModel<StaffItem> staffListModel;
    private JList<StaffItem> evaluatorList;
    private DefaultListModel<StaffItem> evaluatorListModel;
    private JList<EvaluationPointItem> evaluationPointList;
    private DefaultListModel<EvaluationPointItem> evaluationPointListModel;
    private JTextArea descriptionArea;
    private JTable assignedTable;
    private DefaultTableModel tableModel;
    private JScrollPane assignedScrollPane;
    private JDialog tableDialog;
    private JButton popOutButton;
    private Long currentCompanyId;
    private Long currentUserId;
    private final Color pageBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color textPrimary = new Color(33, 37, 41);
    private final Color textMuted = new Color(110, 116, 122);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font fieldFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

    public AssignEvaluationsPanel(Long companyId, Long userId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.currentCompanyId = companyId;
        this.currentUserId = userId;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setBackground(pageBg);

        // Title
        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Assign Staff Evaluations");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textPrimary);
        JLabel subtitleLabel = new JLabel("Select the evaluation period, staff member, criteria, and evaluators.");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(textMuted);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(cardBg);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 18, 0));
        columnsPanel.setOpaque(false);

        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));

        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));

        Dimension labelSize = new Dimension(180, 28);
        Dimension comboSize = new Dimension(420, 34);
        Dimension listSize = new Dimension(420, 160);
        Dimension descSize = new Dimension(420, 120);

        // Period
        JLabel periodLabel = new JLabel("Period:");
        periodLabel.setPreferredSize(labelSize);
        periodLabel.setFont(labelFont);
        periodLabel.setForeground(textPrimary);
        periodCombo = new JComboBox<>();
        periodCombo.setPreferredSize(comboSize);
        periodCombo.setFont(fieldFont);
        loadPeriods();
        JPanel periodPanel = new JPanel(new BorderLayout(0, 6));
        periodPanel.setBackground(cardBg);
        periodPanel.add(periodLabel, BorderLayout.NORTH);
        periodPanel.add(periodCombo, BorderLayout.CENTER);
        leftColumn.add(periodPanel);
        leftColumn.add(Box.createVerticalStrut(12));

        // Staff to evaluate (multi-select)
        JLabel staffLabel = new JLabel("Staff to Evaluate:");
        staffLabel.setPreferredSize(labelSize);
        staffLabel.setFont(labelFont);
        staffLabel.setForeground(textPrimary);
        staffListModel = new DefaultListModel<>();
        staffList = new JList<>(staffListModel);
        staffList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        staffList.setFont(fieldFont);
        enableCheckList(staffList);
        JScrollPane staffScroll = new JScrollPane(staffList);
        staffScroll.setPreferredSize(listSize);

        JPanel staffPanel = new JPanel(new BorderLayout(5, 5));
        staffPanel.setBackground(cardBg);
        staffPanel.add(staffScroll, BorderLayout.CENTER);

        JPanel staffButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        staffButtonPanel.setBackground(cardBg);
        JButton selectAllStaffButton = new JButton("Select All");
        JButton clearStaffButton = new JButton("Clear Selection");
        styleButton(selectAllStaffButton);
        styleButton(clearStaffButton);

        selectAllStaffButton.addActionListener(e -> {
            selectAll(staffList);
        });
        clearStaffButton.addActionListener(e -> staffList.clearSelection());

        staffButtonPanel.add(selectAllStaffButton);
        staffButtonPanel.add(clearStaffButton);
        staffPanel.add(staffButtonPanel, BorderLayout.SOUTH);

        loadStaff();
        JPanel staffSection = new JPanel(new BorderLayout(0, 6));
        staffSection.setBackground(cardBg);
        staffSection.add(staffLabel, BorderLayout.NORTH);
        staffSection.add(staffPanel, BorderLayout.CENTER);
        JLabel staffHint = new JLabel("Tip: Click items to toggle selection; Select All/Clear helps for bulk changes.");
        staffHint.setFont(subtitleFont);
        staffHint.setForeground(textMuted);
        staffSection.add(staffHint, BorderLayout.SOUTH);
        leftColumn.add(staffSection);
        leftColumn.add(Box.createVerticalStrut(12));

        // Evaluation Points (LEFT column)
        JLabel criteriaLabel = new JLabel("Evaluation Criteria:");
        criteriaLabel.setPreferredSize(labelSize);
        criteriaLabel.setFont(labelFont);
        criteriaLabel.setForeground(textPrimary);
        evaluationPointListModel = new DefaultListModel<>();
        evaluationPointList = new JList<>(evaluationPointListModel);
        evaluationPointList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        evaluationPointList.setFont(fieldFont);
        enableCheckList(evaluationPointList);
        JScrollPane pointScroll = new JScrollPane(evaluationPointList);
        pointScroll.setPreferredSize(listSize);

        JPanel pointPanel = new JPanel(new BorderLayout(5, 5));
        pointPanel.setBackground(cardBg);
        pointPanel.add(pointScroll, BorderLayout.CENTER);

        JPanel pointButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pointButtonPanel.setBackground(cardBg);
        JButton selectAllPointsButton = new JButton("Select All");
        JButton clearPointsButton = new JButton("Clear Selection");
        styleButton(selectAllPointsButton);
        styleButton(clearPointsButton);

        selectAllPointsButton.addActionListener(e -> {
            selectAll(evaluationPointList);
        });
        clearPointsButton.addActionListener(e -> evaluationPointList.clearSelection());

        pointButtonPanel.add(selectAllPointsButton);
        pointButtonPanel.add(clearPointsButton);
        pointPanel.add(pointButtonPanel, BorderLayout.SOUTH);

        loadEvaluationPoints();
        JPanel pointSection = new JPanel(new BorderLayout(0, 6));
        pointSection.setBackground(cardBg);
        pointSection.add(criteriaLabel, BorderLayout.NORTH);
        pointSection.add(pointPanel, BorderLayout.CENTER);
        JLabel pointHint = new JLabel("Tip: Criteria are multi-select; use Select All/Clear to adjust quickly.");
        pointHint.setFont(subtitleFont);
        pointHint.setForeground(textMuted);
        pointSection.add(pointHint, BorderLayout.SOUTH);
        leftColumn.add(pointSection);

        // Right column: Evaluators
        JLabel evaluatorLabel = new JLabel("Select Evaluators:");
        evaluatorLabel.setPreferredSize(labelSize);
        evaluatorLabel.setFont(labelFont);
        evaluatorLabel.setForeground(textPrimary);
        evaluatorListModel = new DefaultListModel<>();
        evaluatorList = new JList<>(evaluatorListModel);
        evaluatorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        evaluatorList.setFont(fieldFont);
        enableCheckList(evaluatorList);
        JScrollPane evaluatorScroll = new JScrollPane(evaluatorList);
        evaluatorScroll.setPreferredSize(listSize);

        JPanel evaluatorPanel = new JPanel(new BorderLayout(5, 5));
        evaluatorPanel.setBackground(cardBg);
        evaluatorPanel.add(evaluatorScroll, BorderLayout.CENTER);

        JPanel evalButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        evalButtonPanel.setBackground(cardBg);
        JButton selectAllButton = new JButton("Select All");
        JButton clearSelectionButton = new JButton("Clear Selection");
        styleButton(selectAllButton);
        styleButton(clearSelectionButton);

        selectAllButton.addActionListener(e -> {
            selectAll(evaluatorList);
        });
        clearSelectionButton.addActionListener(e -> evaluatorList.clearSelection());

        evalButtonPanel.add(selectAllButton);
        evalButtonPanel.add(clearSelectionButton);
        evaluatorPanel.add(evalButtonPanel, BorderLayout.SOUTH);

        loadEvaluators();
        JPanel evaluatorSection = new JPanel(new BorderLayout(0, 6));
        evaluatorSection.setBackground(cardBg);
        evaluatorSection.add(evaluatorLabel, BorderLayout.NORTH);
        evaluatorSection.add(evaluatorPanel, BorderLayout.CENTER);
        JLabel evaluatorHint = new JLabel("Tip: Choose one or more evaluators; clicks toggle selections.");
        evaluatorHint.setFont(subtitleFont);
        evaluatorHint.setForeground(textMuted);
        evaluatorSection.add(evaluatorHint, BorderLayout.SOUTH);
        rightColumn.add(evaluatorSection);
        rightColumn.add(Box.createVerticalStrut(12));

        // Right column: Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setPreferredSize(labelSize);
        descriptionLabel.setFont(labelFont);
        descriptionLabel.setForeground(textPrimary);
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setFont(fieldFont);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(descSize);
        JPanel descriptionSection = new JPanel(new BorderLayout(0, 6));
        descriptionSection.setBackground(cardBg);
        descriptionSection.add(descriptionLabel, BorderLayout.NORTH);
        descriptionSection.add(descScroll, BorderLayout.CENTER);
        rightColumn.add(descriptionSection);
        rightColumn.add(Box.createVerticalStrut(12));

        // Right column: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(cardBg);
        JButton assignButton = new JButton("Assign Evaluation");
        popOutButton = new JButton("View Assign Evaluations");
        JButton clearButton = new JButton("Clear");
        stylePrimaryButton(assignButton);
        styleButton(popOutButton);
        styleButton(clearButton);

        assignButton.addActionListener(e -> assignEvaluation());
        popOutButton.addActionListener(e -> showTableInDialog());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(assignButton);
        buttonPanel.add(popOutButton);
        buttonPanel.add(clearButton);
        rightColumn.add(Box.createVerticalGlue());
        rightColumn.add(buttonPanel);

        columnsPanel.add(leftColumn);
        columnsPanel.add(rightColumn);
        formPanel.add(columnsPanel, BorderLayout.CENTER);

        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formPanel, BorderLayout.CENTER);

        String[] columns = {"ID", "Period", "Staff Name", "Assigned By", "Criteria", "Assign Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedTable = new JTable(tableModel);
        assignedTable.setRowHeight(32);
        assignedTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        assignedTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        assignedScrollPane = new JScrollPane(assignedTable);

        add(formWrapper, BorderLayout.CENTER);

        loadAssignedEvaluations();
    }

    private void styleButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(229, 233, 238));
        button.setForeground(textPrimary);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 204, 210), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(214, 220, 230));
        button.setForeground(textPrimary);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(190, 196, 206), 1, true),
                BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void enableCheckList(JList<?> list) {
        list.setCellRenderer(new CheckBoxListRenderer());
        list.setSelectionModel(new ToggleSelectionModel());
        list.setVisibleRowCount(8);
    }

    private void selectAll(JList<?> list) {
        if (list.getModel().getSize() == 0) {
            return;
        }
        ListSelectionModel selectionModel = list.getSelectionModel();
        if (selectionModel instanceof ToggleSelectionModel) {
            ToggleSelectionModel toggleModel = (ToggleSelectionModel) selectionModel;
            toggleModel.setToggleEnabled(false);
            list.setSelectionInterval(0, list.getModel().getSize() - 1);
            toggleModel.setToggleEnabled(true);
        } else {
            list.setSelectionInterval(0, list.getModel().getSize() - 1);
        }
    }

    private static class ToggleSelectionModel extends DefaultListSelectionModel {
        private boolean toggleEnabled = true;

        void setToggleEnabled(boolean enabled) {
            this.toggleEnabled = enabled;
        }

        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (toggleEnabled && index0 == index1) {
                if (isSelectedIndex(index0)) {
                    removeSelectionInterval(index0, index1);
                } else {
                    addSelectionInterval(index0, index1);
                }
                return;
            }
            super.setSelectionInterval(index0, index1);
        }
    }

    private static class CheckBoxListRenderer extends JCheckBox implements ListCellRenderer<Object> {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            setText(value == null ? "" : value.toString());
            setSelected(isSelected);
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setForeground(list.getForeground());
            setBackground(list.getBackground());
            setOpaque(true);
            return this;
        }
    }

    private void loadPeriods() {
        periodCombo.removeAllItems();
        List<Period> periods = periodService.getActivePeriods();
        for (Period p : periods) {
            if (p.getCompanyId().equals(currentCompanyId)) {
                periodCombo.addItem(new PeriodItem(p));
            }
        }
    }

    private void loadStaff() {
        staffListModel.clear();
        List<StaffService.StaffWithDetails> staffList = staffService.getStaffWithDetails(currentCompanyId);
        for (StaffService.StaffWithDetails swd : staffList) {
            staffListModel.addElement(new StaffItem(swd));
        }
    }

    private void loadEvaluators() {
        evaluatorListModel.clear();
        List<StaffService.StaffWithDetails> staffList = staffService.getStaffWithDetails(currentCompanyId);
        for (StaffService.StaffWithDetails swd : staffList) {
            evaluatorListModel.addElement(new StaffItem(swd));
        }
    }

    private void loadEvaluationPoints() {
        evaluationPointListModel.clear();
        List<EvaluationPoint> points = evaluationService.getEvaluationPointsByCompany(currentCompanyId);
        for (EvaluationPoint ep : points) {
            evaluationPointListModel.addElement(new EvaluationPointItem(ep));
        }
    }

    private void assignEvaluation() {
        try {
            PeriodItem selectedPeriod = (PeriodItem) periodCombo.getSelectedItem();
            if (selectedPeriod == null) {
                JOptionPane.showMessageDialog(this, "Please select a period");
                return;
            }

            List<StaffItem> selectedStaff = staffList.getSelectedValuesList();
            if (selectedStaff.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one staff member to evaluate");
                return;
            }

            List<EvaluationPointItem> selectedPoints = evaluationPointList.getSelectedValuesList();
            if (selectedPoints.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one evaluation criterion");
                return;
            }

            List<StaffItem> selectedEvaluators = evaluatorList.getSelectedValuesList();
            if (selectedEvaluators.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select at least one evaluator");
                return;
            }

            // Extract evaluation point IDs
            List<Long> evaluationPointIds = new ArrayList<>();
            for (EvaluationPointItem item : selectedPoints) {
                evaluationPointIds.add(item.evaluationPoint.getId());
            }

            int assignedCount = 0;
            int evaluatorAssignments = 0;
            for (StaffItem staffItem : selectedStaff) {
                AssignStaffEvaluation ase = new AssignStaffEvaluation();
                ase.setPeriodId(selectedPeriod.period.getId());
                ase.setCompanyId(currentCompanyId);
                ase.setAssignByStaffId(currentUserId);
                ase.setForStaffId(staffItem.staffWithDetails.staff.getId());
                ase.setAssignDate(LocalDateTime.now());
                ase.setDescription(descriptionArea.getText().trim());
                ase.setCreatedBy(currentUserId);

                Long evaluationId = evaluationService.createStaffEvaluation(ase, evaluationPointIds);

                if (evaluationId != null) {
                    assignedCount++;
                    for (StaffItem evaluator : selectedEvaluators) {
                        AssignStaffEvaluationList evalList = new AssignStaffEvaluationList();
                        evalList.setAssignStaffEvaluationId(evaluationId);
                        evalList.setEvaluationStaffId(evaluator.staffWithDetails.staff.getId());
                        evalList.setCompanyId(currentCompanyId);

                        if (evaluationService.addEvaluatorToEvaluation(evalList)) {
                            evaluatorAssignments++;
                        }
                    }
                }
            }

            if (assignedCount > 0) {
                JOptionPane.showMessageDialog(this,
                        "Evaluations assigned successfully!\n" +
                                "Staff selected: " + selectedStaff.size() + "\n" +
                                "Criteria: " + selectedPoints.size() + "\n" +
                                "Evaluator assignments: " + evaluatorAssignments);

                clearForm();
                loadAssignedEvaluations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign evaluations");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        staffList.clearSelection();
        evaluatorList.clearSelection();
        evaluationPointList.clearSelection();
        descriptionArea.setText("");
    }

    private void loadAssignedEvaluations() {
        tableModel.setRowCount(0);
        List<AssignStaffEvaluation> evaluations = evaluationService.getAllStaffEvaluations();

        for (AssignStaffEvaluation ase : evaluations) {
            if (ase.getCompanyId().equals(currentCompanyId)) {
                Period period = periodService.getPeriodById(ase.getPeriodId());
                String periodCode = period != null ? period.getCode() : "N/A";

                String staffName = staffService.getStaffNameById(ase.getForStaffId());
                String assignedByName = staffService.getStaffNameById(ase.getAssignByStaffId());

                // Get assigned evaluation points
                List<EvaluationPoint> assignedPoints = evaluationService.getAssignedEvaluationPoints(ase.getId());
                String criteriaCount = assignedPoints.size() + " criteria";

                tableModel.addRow(new Object[]{
                        ase.getId(),
                        periodCode,
                        staffName,
                        assignedByName,
                        criteriaCount,
                        ase.getAssignDate(),
                        ase.getDescription()
                });
            }
        }
    }

    private void showTableInDialog() {
        if (tableDialog != null && tableDialog.isShowing()) {
            tableDialog.toFront();
            return;
        }

        Window owner = SwingUtilities.getWindowAncestor(this);
        tableDialog = new JDialog(owner, "Assigned Evaluations", Dialog.ModalityType.MODELESS);
        tableDialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        tableDialog.getContentPane().setLayout(new BorderLayout(8, 8));
        tableDialog.getContentPane().setBackground(pageBg);

        JPanel dialogCard = new JPanel(new BorderLayout());
        dialogCard.setBackground(cardBg);
        dialogCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JLabel dialogTitle = new JLabel("Assigned Evaluations");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dialogTitle.setForeground(textPrimary);
        dialogCard.add(dialogTitle, BorderLayout.NORTH);
        dialogCard.add(assignedScrollPane, BorderLayout.CENTER);

        tableDialog.getContentPane().add(dialogCard, BorderLayout.CENTER);
        tableDialog.setSize(900, 400);
        tableDialog.setLocationRelativeTo(this);
        tableDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (assignedScrollPane.getParent() != null) {
                    assignedScrollPane.getParent().remove(assignedScrollPane);
                }
            }
        });
        tableDialog.setVisible(true);
    }

    // Helper class for combo box
    private class PeriodItem {
        Period period;

        PeriodItem(Period period) {
            this.period = period;
        }

        @Override
        public String toString() {
            return period.getCode() + " (" + period.getFromDate() + " to " + period.getToDate() + ")";
        }
    }

    // Helper class for staff selection
    private class StaffItem {
        StaffService.StaffWithDetails staffWithDetails;

        StaffItem(StaffService.StaffWithDetails staffWithDetails) {
            this.staffWithDetails = staffWithDetails;
        }

        @Override
        public String toString() {
            return staffWithDetails.getDisplayName();
        }
    }

    // Helper class for evaluation points
    private class EvaluationPointItem {
        EvaluationPoint evaluationPoint;

        EvaluationPointItem(EvaluationPoint evaluationPoint) {
            this.evaluationPoint = evaluationPoint;
        }

        @Override
        public String toString() {
            return evaluationPoint.getName() + " (" +
                    evaluationPoint.getScoreRangeFrom() + "-" +
                    evaluationPoint.getScoreRangeTo() + ")";
        }
    }
}
