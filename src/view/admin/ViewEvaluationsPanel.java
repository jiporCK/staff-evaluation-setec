package view.admin;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import model.*;
import service.*;

public class ViewEvaluationsPanel extends JPanel {
    private User currentUser;
    private EvaluationService evaluationService;
    private StaffService staffService;
    private PeriodService periodService;
    private final Color pageBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color textPrimary = new Color(33, 37, 41);
    private final Color textMuted = new Color(110, 116, 122);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

    // Main table components
    private JTable evaluationsTable;
    private DefaultTableModel tableModel;

    // Filter components
    private JComboBox<PeriodWrapper> periodFilterCombo;
    private JComboBox<String> statusFilterCombo;
    private JTextField searchField;
    private JLabel statsLabel;

    // Date formatter
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public ViewEvaluationsPanel(User user) {
        this.currentUser = user;
        this.evaluationService = new EvaluationService();
        this.staffService = new StaffService();
        this.periodService = new PeriodService();
        initComponents();
        loadPeriods();
        loadEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        setBackground(pageBg);

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(createTablePanel());
        splitPane.setBottomComponent(createDetailsPanel());
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.7);
        splitPane.setDividerSize(8);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        splitPane.setBackground(pageBg);
        add(splitPane, BorderLayout.CENTER);

        // Bottom panel with stats and actions
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 12, 0));

        // Title
        JLabel titleLabel = new JLabel("View All Staff Evaluations");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textPrimary);
        JLabel subtitleLabel = new JLabel("Filter and review evaluation progress across staff.");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(textMuted);
        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 4));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Filter Panel
        panel.add(createFilterPanel(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // Row 1: Period and Status filters
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row1.setBackground(cardBg);

        row1.add(new JLabel("Period:"));
        periodFilterCombo = new JComboBox<>();
        periodFilterCombo.setFont(labelFont);
        periodFilterCombo.setPreferredSize(new Dimension(220, 32));
        periodFilterCombo.addActionListener(e -> applyFilters());
        row1.add(periodFilterCombo);

        row1.add(Box.createHorizontalStrut(20));

        row1.add(new JLabel("Status:"));
        statusFilterCombo = new JComboBox<>(new String[]{
                "All", "Completed", "Pending", "In Progress"
        });
        statusFilterCombo.setFont(labelFont);
        statusFilterCombo.setPreferredSize(new Dimension(180, 32));
        statusFilterCombo.addActionListener(e -> applyFilters());
        row1.add(statusFilterCombo);

        panel.add(row1);

        // Row 2: Search
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        row2.setBackground(cardBg);

        row2.add(new JLabel("Search Staff:"));
        searchField = new JTextField(30);
        searchField.setFont(labelFont);
        searchField.setPreferredSize(new Dimension(320, 32));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                applyFilters();
            }
        });
        row2.add(searchField);

        JButton clearButton = new JButton("Clear Filters");
        styleButton(clearButton);
        clearButton.addActionListener(e -> clearFilters());
        row2.add(clearButton);

        panel.add(row2);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        String[] columns = {
                "ID", "Period", "For Staff", "Staff ID", "Position",
                "Assigned By", "Assign Date", "Avg Score", "Status", "Progress"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0 || columnIndex == 3) return Long.class;
                if (columnIndex == 7) return Double.class;
                return String.class;
            }
        };

        evaluationsTable = new JTable(tableModel);
        evaluationsTable.setAutoCreateRowSorter(true);
        evaluationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        evaluationsTable.setRowHeight(32);
        evaluationsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        evaluationsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Custom column widths
        TableColumnModel columnModel = evaluationsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);   // ID
        columnModel.getColumn(1).setPreferredWidth(120);  // Period
        columnModel.getColumn(2).setPreferredWidth(150);  // For Staff
        columnModel.getColumn(3).setPreferredWidth(70);   // Staff ID
        columnModel.getColumn(4).setPreferredWidth(120);  // Position
        columnModel.getColumn(5).setPreferredWidth(120);  // Assigned By
        columnModel.getColumn(6).setPreferredWidth(130);  // Date
        columnModel.getColumn(7).setPreferredWidth(80);   // Score
        columnModel.getColumn(8).setPreferredWidth(90);   // Status
        columnModel.getColumn(9).setPreferredWidth(100);  // Progress

        // Add double-click listener
        evaluationsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewDetailedEvaluation();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(evaluationsTable);
        JLabel tableTitle = new JLabel("Evaluations List");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(textPrimary);
        panel.add(tableTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        buttonsPanel.setBackground(cardBg);

        JButton viewDetailsBtn = createStyledButton("View Full Details");
        viewDetailsBtn.addActionListener(e -> viewDetailedEvaluation());

        JButton viewScoresBtn = createStyledButton("View All Scores");
        viewScoresBtn.addActionListener(e -> viewAllScores());

        JButton exportBtn = createStyledButton("Export Selected");
        exportBtn.addActionListener(e -> exportSelectedEvaluation());

        JButton statsBtn = createStyledButton("View Statistics");
        statsBtn.addActionListener(e -> viewStatistics());

        JButton refreshBtn = createStyledButton("Refresh Data");
        refreshBtn.addActionListener(e -> loadEvaluations());

        buttonsPanel.add(viewDetailsBtn);
        buttonsPanel.add(viewScoresBtn);
        buttonsPanel.add(exportBtn);
        buttonsPanel.add(statsBtn);
        buttonsPanel.add(refreshBtn);

        panel.add(buttonsPanel, BorderLayout.CENTER);

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(229, 233, 238));
        button.setForeground(textPrimary);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(170, 38));
        button.setFont(buttonFont);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 204, 210), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        return button;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        statsLabel = new JLabel("Ready");
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statsLabel.setForeground(textMuted);
        panel.add(statsLabel, BorderLayout.WEST);

        return panel;
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

    // Data Loading Methods

    private void loadPeriods() {
        periodFilterCombo.removeAllItems();
        periodFilterCombo.addItem(new PeriodWrapper(null)); // "All Periods"

        List<Period> periods = periodService.getAllPeriods();
        for (Period period : periods) {
            if (period.getCompanyId().equals(currentUser.getCompanyId())) {
                periodFilterCombo.addItem(new PeriodWrapper(period));
            }
        }
    }

    private void loadEvaluations() {
        List<AssignStaffEvaluation> evaluations = evaluationService.getAllStaffEvaluations();
        tableModel.setRowCount(0);

        int totalCount = 0;
        int completedCount = 0;

        for (AssignStaffEvaluation eval : evaluations) {
            if (!eval.getCompanyId().equals(currentUser.getCompanyId())) {
                continue;
            }

            totalCount++;

            // Get related data
            Staff forStaff = staffService.getStaffById(eval.getForStaffId());
            Staff assignByStaff = staffService.getStaffById(eval.getAssignByStaffId());
            Period period = periodService.getPeriodById(eval.getPeriodId());

            if (forStaff == null) continue;

            String forStaffName = forStaff.getName();
            String assignByName = assignByStaff != null ? assignByStaff.getName() : "Unknown";
            String periodName = period != null ? period.getCode() : "N/A";

            // Get position
            String positionName = "N/A";
            if (forStaff.getPositionId() != null) {
                Position pos = new PositionService().getPositionById(forStaff.getPositionId());
                if (pos != null) positionName = pos.getName();
            }

            // Calculate average score
            BigDecimal avgScore = evaluationService.calculateAverageScore(
                    eval.getForStaffId(), eval.getPeriodId()
            );

            // Handle null avgScore
            if (avgScore == null) {
                avgScore = BigDecimal.ZERO;
            }

            // FIXED: Get evaluation progress - count how many evaluators completed ALL criteria
            List<AssignStaffEvaluationList> evaluators =
                    evaluationService.getEvaluationListItems(eval.getId());
            List<EvaluationPoint> assignedPoints =
                    evaluationService.getAssignedEvaluationPoints(eval.getId());

            int totalEvaluators = evaluators.size();
            int completedEvaluators = 0;
            int totalCriteria = assignedPoints.size();

            for (AssignStaffEvaluationList evaluator : evaluators) {
                List<ASEAssignScore> scores =
                        evaluationService.getScoresByEvaluationListId(evaluator.getId());
                if (scores.size() == totalCriteria) {
                    completedEvaluators++;
                }
            }

            String progress = totalEvaluators > 0 ?
                    completedEvaluators + "/" + totalEvaluators : "0/0";

            String status = determineStatus(avgScore, completedEvaluators, totalEvaluators);
            if (status.equals("Completed")) completedCount++;

            Object[] row = {
                    eval.getId(),
                    periodName,
                    forStaffName,
                    forStaff.getId(),
                    positionName,
                    assignByName,
                    eval.getAssignDate().format(DATE_FORMATTER),
                    avgScore.doubleValue(),
                    status,
                    progress
            };
            tableModel.addRow(row);
        }

        updateStatsLabel(totalCount, completedCount);
    }

    private String determineStatus(BigDecimal avgScore, int submitted, int total) {
        if (total == 0) return "No Evaluators";
        if (submitted == 0) return "Pending";
        if (submitted < total) return "In Progress";
        return "Completed";
    }

    private void updateStatsLabel(int total, int completed) {
        double percentage = total > 0 ? (completed * 100.0 / total) : 0;
        statsLabel.setText(String.format(
                "Total Evaluations: %d | Completed: %d | Pending/In Progress: %d | Completion Rate: %.1f%%",
                total, completed, total - completed, percentage
        ));
    }

    // Filter Methods

    private void applyFilters() {
        PeriodWrapper selectedPeriod = (PeriodWrapper) periodFilterCombo.getSelectedItem();
        String selectedStatus = (String) statusFilterCombo.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        evaluationsTable.setRowSorter(sorter);

        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // Period filter
        if (selectedPeriod != null && selectedPeriod.period != null) {
            filters.add(RowFilter.regexFilter(selectedPeriod.toString(), 1));
        }

        // Status filter
        if (!"All".equals(selectedStatus)) {
            filters.add(RowFilter.regexFilter(selectedStatus, 8));
        }

        // Search filter (searches in For Staff and Staff ID columns)
        if (!searchText.isEmpty()) {
            RowFilter<Object, Object> searchFilter = RowFilter.orFilter(Arrays.asList(
                    RowFilter.regexFilter("(?i)" + searchText, 2), // For Staff name
                    RowFilter.regexFilter("(?i)" + searchText, 3)  // Staff ID
            ));
            filters.add(searchFilter);
        }

        if (filters.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.andFilter(filters));
        }
    }

    private void clearFilters() {
        periodFilterCombo.setSelectedIndex(0);
        statusFilterCombo.setSelectedIndex(0);
        searchField.setText("");
        applyFilters();
    }

    // Action Methods

    private void viewDetailedEvaluation() {
        int selectedRow = evaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluation from the table",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = evaluationsTable.convertRowIndexToModel(selectedRow);
        Long evalId = (Long) tableModel.getValueAt(modelRow, 0);

        showDetailedEvaluationDialog(evalId);
    }

    private void showDetailedEvaluationDialog(Long evalId) {
        // Get evaluation details
        AssignStaffEvaluation eval = findEvaluationById(evalId);
        if (eval == null) {
            JOptionPane.showMessageDialog(this, "Evaluation not found",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Staff forStaff = staffService.getStaffById(eval.getForStaffId());
        Staff assignByStaff = staffService.getStaffById(eval.getAssignByStaffId());
        Period period = periodService.getPeriodById(eval.getPeriodId());

        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evalId);
        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evalId);

        // Create dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Evaluation Details", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        infoPanel.add(createInfoLabel("Evaluation ID:", evalId.toString(), true));
        infoPanel.add(createInfoLabel("Period:", period != null ? period.getCode() : "N/A", false));
        infoPanel.add(createInfoLabel("For Staff:",
                forStaff != null ? forStaff.getName() + " (ID: " + forStaff.getId() + ")" : "N/A", false));
        infoPanel.add(createInfoLabel("Assigned By:",
                assignByStaff != null ? assignByStaff.getName() : "Unknown", false));
        infoPanel.add(createInfoLabel("Assign Date:",
                eval.getAssignDate().format(DATE_FORMATTER), false));
        infoPanel.add(createInfoLabel("Evaluation Criteria:",
                assignedPoints.size() + " criteria", false));
        infoPanel.add(createInfoLabel("Description:",
                eval.getDescription() != null ? eval.getDescription() : "No description", false));

        BigDecimal avgScore = evaluationService.calculateAverageScore(
                eval.getForStaffId(), eval.getPeriodId());

        // Handle null avgScore
        if (avgScore == null) {
            avgScore = BigDecimal.ZERO;
        }

        infoPanel.add(createInfoLabel("Average Score:",
                String.format("%.2f", avgScore.doubleValue()), true));

        dialog.add(infoPanel, BorderLayout.NORTH);

        // FIXED: Evaluators Table with detailed score breakdown
        String[] columns = {"Evaluator", "Position", "Scores Submitted", "Progress", "Avg Score", "Status"};
        DefaultTableModel evalModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable evalTable = new JTable(evalModel);
        evalTable.setRowHeight(25);

        for (AssignStaffEvaluationList evaluator : evaluators) {
            Staff evaluatorStaff = staffService.getStaffById(evaluator.getEvaluationStaffId());
            List<ASEAssignScore> scores =
                    evaluationService.getScoresByEvaluationListId(evaluator.getId());

            String evaluatorName = evaluatorStaff != null ? evaluatorStaff.getName() : "Unknown";
            String position = "N/A";

            if (evaluatorStaff != null && evaluatorStaff.getPositionId() != null) {
                Position pos = new PositionService().getPositionById(evaluatorStaff.getPositionId());
                if (pos != null) position = pos.getName();
            }

            int submittedCount = scores.size();
            int totalCriteria = assignedPoints.size();
            String progress = submittedCount + "/" + totalCriteria;

            // Calculate average for this evaluator
            BigDecimal totalScoreForEvaluator = BigDecimal.ZERO;
            for (ASEAssignScore score : scores) {
                totalScoreForEvaluator = totalScoreForEvaluator.add(score.getScore());
            }

            String avgScoreStr = "N/A";
            if (submittedCount > 0) {
                BigDecimal avg = totalScoreForEvaluator.divide(
                        BigDecimal.valueOf(submittedCount), 2, BigDecimal.ROUND_HALF_UP);
                avgScoreStr = String.format("%.2f", avg.doubleValue());
            }

            String status = submittedCount == totalCriteria ? "Completed" :
                    submittedCount > 0 ? "In Progress" : "Pending";

            Object[] row = {
                    evaluatorName,
                    position,
                    submittedCount + " of " + totalCriteria,
                    progress,
                    avgScoreStr,
                    status
            };
            evalModel.addRow(row);
        }

        dialog.add(new JScrollPane(evalTable), BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createInfoLabel(String label, String value, boolean bold) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Arial", Font.BOLD, 12));
        labelComp.setPreferredSize(new Dimension(150, 20));

        JLabel valueComp = new JLabel(value);
        if (bold) {
            valueComp.setFont(new Font("Arial", Font.BOLD, 12));
            valueComp.setForeground(new Color(33, 150, 243));
        } else {
            valueComp.setFont(new Font("Arial", Font.PLAIN, 12));
        }

        panel.add(labelComp);
        panel.add(valueComp);
        return panel;
    }

    private void viewAllScores() {
        int selectedRow = evaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluation from the table",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = evaluationsTable.convertRowIndexToModel(selectedRow);
        Long evalId = (Long) tableModel.getValueAt(modelRow, 0);

        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evalId);
        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evalId);

        if (evaluators.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No evaluators assigned to this evaluation",
                    "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder details = new StringBuilder();
        details.append("=== Detailed Evaluation Scores ===\n\n");

        for (AssignStaffEvaluationList evaluator : evaluators) {
            Staff evaluatorStaff = staffService.getStaffById(evaluator.getEvaluationStaffId());
            List<ASEAssignScore> scores =
                    evaluationService.getScoresByEvaluationListId(evaluator.getId());

            details.append("Evaluator: ")
                    .append(evaluatorStaff != null ? evaluatorStaff.getName() : "Unknown")
                    .append(" (ID: ").append(evaluator.getEvaluationStaffId()).append(")\n");
            details.append("-".repeat(50)).append("\n");

            Map<Long, ASEAssignScore> scoreMap = new HashMap<>();
            for (ASEAssignScore score : scores) {
                scoreMap.put(score.getEvaluationPointId(), score);
            }

            BigDecimal totalForEvaluator = BigDecimal.ZERO;
            int criteriaScored = 0;

            for (EvaluationPoint point : assignedPoints) {
                details.append("  ").append(point.getName()).append(": ");
                ASEAssignScore score = scoreMap.get(point.getId());

                if (score != null) {
                    details.append(score.getScore());
                    if (score.getComment() != null && !score.getComment().isEmpty()) {
                        details.append(" - ").append(score.getComment());
                    }
                    totalForEvaluator = totalForEvaluator.add(score.getScore());
                    criteriaScored++;
                } else {
                    details.append("Not Submitted");
                }
                details.append("\n");
            }

            if (criteriaScored > 0) {
                BigDecimal avg = totalForEvaluator.divide(
                        BigDecimal.valueOf(criteriaScored), 2, BigDecimal.ROUND_HALF_UP);
                details.append("  Average: ").append(avg).append("\n");
            }
            details.append("\n");
        }

        // Overall statistics
        BigDecimal overallAvg = evaluationService.calculateAverageScore(
                findEvaluationById(evalId).getForStaffId(),
                findEvaluationById(evalId).getPeriodId()
        );

        details.append("=========================\n");
        details.append("Overall Average Score: ").append(overallAvg != null ? overallAvg : "N/A").append("\n");

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 500));

        JOptionPane.showMessageDialog(this, scrollPane,
                "All Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportSelectedEvaluation() {
        int selectedRow = evaluationsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select an evaluation to export",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = evaluationsTable.convertRowIndexToModel(selectedRow);
        Long evalId = (Long) tableModel.getValueAt(modelRow, 0);

        JOptionPane.showMessageDialog(this,
                "Export functionality for Evaluation ID: " + evalId +
                        "\n\nUse the 'Manage Reports' panel to export evaluation forms.",
                "Export Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewStatistics() {
        List<AssignStaffEvaluation> allEvaluations = evaluationService.getAllStaffEvaluations();

        int total = 0;
        int completed = 0;
        int pending = 0;
        int inProgress = 0;
        BigDecimal totalScore = BigDecimal.ZERO;
        int scoreCount = 0;

        Map<Long, Integer> periodCounts = new HashMap<>();

        for (AssignStaffEvaluation eval : allEvaluations) {
            if (!eval.getCompanyId().equals(currentUser.getCompanyId())) continue;

            total++;

            List<AssignStaffEvaluationList> evaluators =
                    evaluationService.getEvaluationListItems(eval.getId());
            List<EvaluationPoint> assignedPoints =
                    evaluationService.getAssignedEvaluationPoints(eval.getId());

            int totalEvaluators = evaluators.size();
            int totalCriteria = assignedPoints.size();
            int completedEvaluators = 0;

            for (AssignStaffEvaluationList evaluator : evaluators) {
                List<ASEAssignScore> scores =
                        evaluationService.getScoresByEvaluationListId(evaluator.getId());

                for (ASEAssignScore score : scores) {
                    totalScore = totalScore.add(score.getScore());
                    scoreCount++;
                }

                if (scores.size() == totalCriteria) {
                    completedEvaluators++;
                }
            }

            String status = determineStatus(
                    evaluationService.calculateAverageScore(eval.getForStaffId(), eval.getPeriodId()),
                    completedEvaluators, totalEvaluators
            );

            switch (status) {
                case "Completed": completed++; break;
                case "Pending": pending++; break;
                case "In Progress": inProgress++; break;
            }

            periodCounts.put(eval.getPeriodId(),
                    periodCounts.getOrDefault(eval.getPeriodId(), 0) + 1);
        }

        BigDecimal avgScore = scoreCount > 0 ?
                totalScore.divide(BigDecimal.valueOf(scoreCount), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        StringBuilder stats = new StringBuilder();
        stats.append("=== Evaluation Statistics ===\n\n");
        stats.append("Total Evaluations: ").append(total).append("\n");
        stats.append("Completed: ").append(completed).append("\n");
        stats.append("In Progress: ").append(inProgress).append("\n");
        stats.append("Pending: ").append(pending).append("\n");
        stats.append("Overall Average Score: ").append(avgScore).append("\n\n");
        stats.append("=== By Period ===\n");

        for (Map.Entry<Long, Integer> entry : periodCounts.entrySet()) {
            Period period = periodService.getPeriodById(entry.getKey());
            stats.append(period != null ? period.getCode() : "Unknown")
                    .append(": ").append(entry.getValue()).append(" evaluations\n");
        }

        JTextArea textArea = new JTextArea(stats.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 350));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Statistics", JOptionPane.INFORMATION_MESSAGE);
    }

    // Helper Methods

    private AssignStaffEvaluation findEvaluationById(Long id) {
        List<AssignStaffEvaluation> all = evaluationService.getAllStaffEvaluations();
        for (AssignStaffEvaluation eval : all) {
            if (eval.getId().equals(id)) {
                return eval;
            }
        }
        return null;
    }

    // Wrapper Classes

    private static class PeriodWrapper {
        Period period;

        PeriodWrapper(Period period) {
            this.period = period;
        }

        @Override
        public String toString() {
            return period == null ? "All Periods" :
                    period.getCode() + " - " + period.getCode();
        }
    }
}
