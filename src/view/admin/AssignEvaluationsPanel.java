package view.admin;

import model.*;
import service.EvaluationService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AssignEvaluationsPanel extends JPanel {

    private EvaluationService evaluationService;
    private PeriodService periodService;
    private StaffService staffService;
    private JComboBox<PeriodItem> periodCombo;
    private JComboBox<StaffItem> staffCombo;
    private JList<StaffItem> evaluatorList;
    private DefaultListModel<StaffItem> evaluatorListModel;
    private JList<EvaluationPointItem> evaluationPointList;
    private DefaultListModel<EvaluationPointItem> evaluationPointListModel;
    private JTextArea descriptionArea;
    private JTable assignedTable;
    private DefaultTableModel tableModel;
    private Long currentCompanyId;
    private Long currentUserId;

    public AssignEvaluationsPanel(Long companyId, Long userId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.currentCompanyId = companyId;
        this.currentUserId = userId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Assign Staff Evaluations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Evaluation"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Period
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Period:"), gbc);
        gbc.gridx = 1;
        periodCombo = new JComboBox<>();
        loadPeriods();
        formPanel.add(periodCombo, gbc);

        // Staff to evaluate
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Staff to Evaluate:"), gbc);
        gbc.gridx = 1;
        staffCombo = new JComboBox<>();
        loadStaff();
        formPanel.add(staffCombo, gbc);

        // Evaluation Points (NEW)
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Evaluation Criteria:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.5;

        evaluationPointListModel = new DefaultListModel<>();
        evaluationPointList = new JList<>(evaluationPointListModel);
        evaluationPointList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane pointScroll = new JScrollPane(evaluationPointList);
        pointScroll.setPreferredSize(new Dimension(300, 100));

        JPanel pointPanel = new JPanel(new BorderLayout(5, 5));
        pointPanel.add(pointScroll, BorderLayout.CENTER);

        JPanel pointButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllPointsButton = new JButton("Select All");
        JButton clearPointsButton = new JButton("Clear Selection");

        selectAllPointsButton.addActionListener(e -> {
            evaluationPointList.setSelectionInterval(0, evaluationPointListModel.getSize() - 1);
        });
        clearPointsButton.addActionListener(e -> evaluationPointList.clearSelection());

        pointButtonPanel.add(selectAllPointsButton);
        pointButtonPanel.add(clearPointsButton);
        pointPanel.add(pointButtonPanel, BorderLayout.SOUTH);

        loadEvaluationPoints();
        formPanel.add(pointPanel, gbc);

        // Evaluators
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Select Evaluators:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.5;

        evaluatorListModel = new DefaultListModel<>();
        evaluatorList = new JList<>(evaluatorListModel);
        evaluatorList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane evaluatorScroll = new JScrollPane(evaluatorList);
        evaluatorScroll.setPreferredSize(new Dimension(300, 100));

        JPanel evaluatorPanel = new JPanel(new BorderLayout(5, 5));
        evaluatorPanel.add(evaluatorScroll, BorderLayout.CENTER);

        JPanel evalButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton selectAllButton = new JButton("Select All");
        JButton clearSelectionButton = new JButton("Clear Selection");

        selectAllButton.addActionListener(e -> {
            evaluatorList.setSelectionInterval(0, evaluatorListModel.getSize() - 1);
        });
        clearSelectionButton.addActionListener(e -> evaluatorList.clearSelection());

        evalButtonPanel.add(selectAllButton);
        evalButtonPanel.add(clearSelectionButton);
        evaluatorPanel.add(evalButtonPanel, BorderLayout.SOUTH);

        loadEvaluators();
        formPanel.add(evaluatorPanel, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descriptionArea = new JTextArea(3, 20);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Buttons
        gbc.gridx = 1; gbc.gridy = 5;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assignButton = new JButton("Assign Evaluation");
        JButton clearButton = new JButton("Clear");

        assignButton.addActionListener(e -> assignEvaluation());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(assignButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // Table of assigned evaluations
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Assigned Evaluations"));

        String[] columns = {"ID", "Period", "Staff Name", "Assigned By", "Criteria", "Assign Date", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assignedTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(assignedTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh List");
        refreshButton.addActionListener(e -> loadAssignedEvaluations());
        tablePanel.add(refreshButton, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        loadAssignedEvaluations();
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
        staffCombo.removeAllItems();
        List<StaffService.StaffWithDetails> staffList = staffService.getStaffWithDetails(currentCompanyId);
        for (StaffService.StaffWithDetails swd : staffList) {
            staffCombo.addItem(new StaffItem(swd));
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

            StaffItem selectedStaff = (StaffItem) staffCombo.getSelectedItem();
            if (selectedStaff == null) {
                JOptionPane.showMessageDialog(this, "Please select a staff member to evaluate");
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

            // Create evaluation assignment
            AssignStaffEvaluation ase = new AssignStaffEvaluation();
            ase.setPeriodId(selectedPeriod.period.getId());
            ase.setCompanyId(currentCompanyId);
            ase.setAssignByStaffId(currentUserId);
            ase.setForStaffId(selectedStaff.staffWithDetails.staff.getId());
            ase.setAssignDate(LocalDateTime.now());
            ase.setDescription(descriptionArea.getText().trim());
            ase.setCreatedBy(currentUserId);

            // FIXED: Pass evaluation point IDs
            Long evaluationId = evaluationService.createStaffEvaluation(ase, evaluationPointIds);

            if (evaluationId != null) {
                // Add evaluators to the evaluation
                int successCount = 0;
                for (StaffItem evaluator : selectedEvaluators) {
                    AssignStaffEvaluationList evalList = new AssignStaffEvaluationList();
                    evalList.setAssignStaffEvaluationId(evaluationId);
                    evalList.setEvaluationStaffId(evaluator.staffWithDetails.staff.getId());
                    evalList.setCompanyId(currentCompanyId);

                    if (evaluationService.addEvaluatorToEvaluation(evalList)) {
                        successCount++;
                    }
                }

                JOptionPane.showMessageDialog(this,
                        "Evaluation assigned successfully!\n" +
                                "Staff: " + selectedStaff.staffWithDetails.staff.getName() + "\n" +
                                "Criteria: " + selectedPoints.size() + "\n" +
                                "Evaluators added: " + successCount + " of " + selectedEvaluators.size());

                clearForm();
                loadAssignedEvaluations();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to assign evaluation");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void clearForm() {
        staffCombo.setSelectedIndex(-1);
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