package view.admin;

import service.*;
import model.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class ManageReportsPanel extends JPanel {

    private FilePersistentService fileService;
    private EvaluationService evaluationService;
    private PeriodService periodService;
    private StaffService staffService;
    private PositionService positionService;
    private DepartmentService departmentService;
    private OfficeService officeService;

    // UI Components
    private JComboBox<PeriodWrapper> periodComboBox;
    private JComboBox<String> rankingRangeComboBox;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;
    private JSpinner topNSpinner;
    private JTextField evaluationIdField;
    private Long currentCompanyId;
    private final Color pageBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color textPrimary = new Color(33, 37, 41);
    private final Color textMuted = new Color(110, 116, 122);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

    public ManageReportsPanel(Long companyId) {
        this.currentCompanyId = companyId;
        initializeServices();
        initializeUI();
        loadPeriods();
    }

    private void initializeServices() {
        fileService = new FilePersistentService();
        evaluationService = new EvaluationService();
        periodService = new PeriodService();
        staffService = new StaffService();
        positionService = new PositionService();
        departmentService = new DepartmentService();
        officeService = new OfficeService();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(16, 16));
        setBorder(new EmptyBorder(24, 24, 24, 24));
        setBackground(pageBg);

        // Title Panel
        add(createTitlePanel(), BorderLayout.NORTH);

        // Main Content - Split into Control and Display
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(createControlPanel());
        splitPane.setRightComponent(createDisplayPanel());
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(10);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        splitPane.setBackground(pageBg);
        add(splitPane, BorderLayout.CENTER);

        // Status Panel
        add(createStatusPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 12, 0));

        JLabel titleLabel = new JLabel("Manage Reports & Rankings");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textPrimary);
        JLabel subtitleLabel = new JLabel("Generate reports by period, ranking range, or evaluation ID.");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(textMuted);
        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGroup.setOpaque(false);
        titleGroup.add(titleLabel);
        titleGroup.add(subtitleLabel);
        panel.add(titleGroup, BorderLayout.WEST);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                new EmptyBorder(12, 12, 12, 12)
        ));
        panel.setBackground(cardBg);

        // Period Selection
        panel.add(createSectionPanel("Select Period", createPeriodPanel()));
        panel.add(Box.createVerticalStrut(15));

        // Top N Rankings
        panel.add(createSectionPanel("Top N Rankings", createTopNPanel()));
        panel.add(Box.createVerticalStrut(15));

        // Range Rankings (10-20, 20-30, etc.)
        panel.add(createSectionPanel("Range Rankings", createRangePanel()));
        panel.add(Box.createVerticalStrut(15));

        // Individual Evaluation
        panel.add(createSectionPanel("Individual Evaluation", createIndividualPanel()));
        panel.add(Box.createVerticalStrut(15));

        // Full Export
//        panel.add(createSectionPanel("Export Options", createExportPanel()));

        return panel;
    }

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(cardBg);

        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(textPrimary);
        panel.add(label, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPeriodPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(cardBg);
        periodComboBox = new JComboBox<>();
        periodComboBox.setFont(labelFont);
        periodComboBox.addActionListener(e -> refreshDisplayTable());
        JLabel label = new JLabel("Period:");
        label.setFont(labelFont);
        label.setForeground(textPrimary);
        panel.add(label, BorderLayout.WEST);
        panel.add(periodComboBox, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTopNPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.setBackground(cardBg);

        JLabel label = new JLabel("Top N:");
        label.setFont(labelFont);
        label.setForeground(textPrimary);
        panel.add(label);
        topNSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 5));
        topNSpinner.setFont(labelFont);
        panel.add(topNSpinner);

        JButton viewButton = new JButton("View Rankings");
        styleButton(viewButton);
        viewButton.addActionListener(e -> viewTopNRankings());
        panel.add(viewButton);

        JButton exportButton = new JButton("Export to Excel");
        styleButton(exportButton);
        exportButton.addActionListener(e -> exportTopNReport());
        panel.add(exportButton);

        return panel;
    }

    private JPanel createRangePanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 5, 5));
        panel.setBackground(cardBg);

        rankingRangeComboBox = new JComboBox<>(new String[]{
                "Top 1-10", "Top 11-20", "Top 21-30", "Top 31-40", "Top 41-50"
        });
        rankingRangeComboBox.setFont(labelFont);
        panel.add(rankingRangeComboBox);

        JButton viewRangeButton = new JButton("View Range");
        styleButton(viewRangeButton);
        viewRangeButton.addActionListener(e -> viewRangeRankings());
        panel.add(viewRangeButton);

        return panel;
    }

    private JPanel createIndividualPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(cardBg);

        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputPanel.setBackground(cardBg);
        JLabel label = new JLabel("Evaluation ID:");
        label.setFont(labelFont);
        label.setForeground(textPrimary);
        inputPanel.add(label, BorderLayout.WEST);
        evaluationIdField = new JTextField();
        evaluationIdField.setFont(labelFont);
        inputPanel.add(evaluationIdField, BorderLayout.CENTER);
        panel.add(inputPanel);

        JButton viewEvalButton = new JButton("View Evaluation");
        styleButton(viewEvalButton);
        viewEvalButton.addActionListener(e -> viewIndividualEvaluation());
        panel.add(viewEvalButton);

        JButton exportEvalButton = new JButton("Export Form");
        styleButton(exportEvalButton);
        exportEvalButton.addActionListener(e -> exportEvaluationForm());
        panel.add(exportEvalButton);

        return panel;
    }

//    private JPanel createExportPanel() {
//        JPanel panel = new JPanel(new GridLayout(1, 1));
//
//        JButton exportAllButton = new JButton("Export Full Period Report");
//        exportAllButton.setBackground(new Color(76, 175, 80));
//        exportAllButton.setForeground(Color.WHITE);
//        exportAllButton.addActionListener(e -> exportFullPeriodReport());
//        panel.add(exportAllButton);
//
//        return panel;
//    }

    private JPanel createDisplayPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // Table
        String[] columns = {"Rank", "Staff ID", "Name", "Position", "Department",
                "Office", "Avg Score", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        reportTable = new JTable(tableModel);
        reportTable.setAutoCreateRowSorter(true);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.setRowHeight(32);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Customize column widths
        reportTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reportTable.getColumnModel().getColumn(1).setPreferredWidth(70);
        reportTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reportTable.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scrollPane = new JScrollPane(reportTable);
        JLabel title = new JLabel("Report Preview");
        title.setFont(new Font("Segoe UI", Font.BOLD, 14));
        title.setForeground(textPrimary);
        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(cardBg);
        JButton refreshButton = new JButton("Refresh");
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> refreshDisplayTable());
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(textMuted);
        panel.add(statusLabel, BorderLayout.WEST);

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
        periodComboBox.removeAllItems();
        List<Period> periods = periodService.getAllPeriods();

        for (Period period : periods) {
            if (period.getCompanyId().equals(currentCompanyId)) {
                periodComboBox.addItem(new PeriodWrapper(period));
            }
        }
    }

    private void refreshDisplayTable() {
        PeriodWrapper selectedPeriod = (PeriodWrapper) periodComboBox.getSelectedItem();
        if (selectedPeriod == null) {
            setStatus("Please select a period", Color.ORANGE);
            return;
        }

        int topN = (Integer) topNSpinner.getValue();
        viewTopNRankings(topN);
    }

    // Action Methods

    private void viewTopNRankings() {
        int topN = (Integer) topNSpinner.getValue();
        viewTopNRankings(topN);
    }

    private void viewTopNRankings(int topN) {
        PeriodWrapper selectedPeriod = (PeriodWrapper) periodComboBox.getSelectedItem();
        if (selectedPeriod == null) {
            JOptionPane.showMessageDialog(this, "Please select a period",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0);
        List<StaffRankingData> rankings = getStaffRankings(selectedPeriod.period.getId());

        int count = 0;
        for (StaffRankingData data : rankings) {
            if (count >= topN) break;

            tableModel.addRow(new Object[]{
                    count + 1,
                    data.staff.getId(),
                    data.staff.getName(),
                    data.positionName,
                    data.departmentName,
                    data.officeName,
                    String.format("%.2f", data.averageScore.doubleValue()),
                    data.status
            });
            count++;
        }

        setStatus("Displaying top " + count + " employees", new Color(76, 175, 80));
    }

    private void viewRangeRankings() {
        PeriodWrapper selectedPeriod = (PeriodWrapper) periodComboBox.getSelectedItem();
        if (selectedPeriod == null) {
            JOptionPane.showMessageDialog(this, "Please select a period",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedRange = (String) rankingRangeComboBox.getSelectedItem();
        int[] range = parseRange(selectedRange);

        tableModel.setRowCount(0);
        List<StaffRankingData> rankings = getStaffRankings(selectedPeriod.period.getId());

        for (int i = range[0] - 1; i < Math.min(range[1], rankings.size()); i++) {
            StaffRankingData data = rankings.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    data.staff.getId(),
                    data.staff.getName(),
                    data.positionName,
                    data.departmentName,
                    data.officeName,
                    String.format("%.2f", data.averageScore.doubleValue()),
                    data.status
            });
        }

        setStatus("Displaying rankings " + range[0] + "-" + range[1], new Color(76, 175, 80));
    }

    private void viewIndividualEvaluation() {
        String evalIdText = evaluationIdField.getText().trim();
        if (evalIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Evaluation ID",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long evalId = Long.parseLong(evalIdText);
            AssignStaffEvaluation eval = findEvaluationById(evalId);

            if (eval == null) {
                JOptionPane.showMessageDialog(this, "Evaluation not found",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            showEvaluationDetails(eval);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Evaluation ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportTopNReport() {
        PeriodWrapper selectedPeriod = (PeriodWrapper) periodComboBox.getSelectedItem();
        if (selectedPeriod == null) {
            JOptionPane.showMessageDialog(this, "Please select a period",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Top N Report");
        fileChooser.setSelectedFile(new File("reports/list_top_10_employees.xls"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();

            if (!path.toLowerCase().endsWith(".xls")) {
                path += ".xls";
            }

            boolean success = fileService.generateTop10EmployeesReport(
                    path,
                    selectedPeriod.period.getId(),
                    currentCompanyId
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Report exported successfully to:\n" + path,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                setStatus("Report exported to " + path, new Color(76, 175, 80));
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to export report",
                        "Error", JOptionPane.ERROR_MESSAGE);
                setStatus("Export failed", Color.RED);
            }
        }
    }

    private void exportEvaluationForm() {
        String evalIdText = evaluationIdField.getText().trim();
        if (evalIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Evaluation ID",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Long evalId = Long.parseLong(evalIdText);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Evaluation Form");
            fileChooser.setSelectedFile(new File("reports/staff_evaluation_form.xls"));

            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String path = file.getAbsolutePath();

                if (!path.toLowerCase().endsWith(".xls")) {
                    path += ".xls";
                }

                boolean success = fileService.generateStaffEvaluationForm(
                        path,
                        evalId,
                        currentCompanyId
                );

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Evaluation form exported successfully to:\n" + path,
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    setStatus("Evaluation form exported to " + path, new Color(76, 175, 80));
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to export evaluation form",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    setStatus("Export failed", Color.RED);
                }
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Evaluation ID format",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

//    private void exportFullPeriodReport() {
//        PeriodWrapper selectedPeriod = (PeriodWrapper) periodComboBox.getSelectedItem();
//        if (selectedPeriod == null) {
//            JOptionPane.showMessageDialog(this, "Please select a period",
//                    "Error", JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//
//        JFileChooser fileChooser = new JFileChooser();
//        fileChooser.setDialogTitle("Save Full Period Report");
//        fileChooser.setSelectedFile(new File("period_report_" +
//                selectedPeriod.period.getCode() + ".xls"));
//
//        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
//            File file = fileChooser.getSelectedFile();
//            String path = file.getAbsolutePath();
//
//            if (!path.toLowerCase().endsWith(".xls")) {
//                path += ".xls";
//            }
//
//            boolean success = fileService.exportPeriodEvaluations(
//                    path,
//                    selectedPeriod.period.getId(),
//                    currentCompanyId
//            );
//
//            if (success) {
//                JOptionPane.showMessageDialog(this,
//                        "Full period report exported successfully to:\n" + path,
//                        "Success", JOptionPane.INFORMATION_MESSAGE);
//                setStatus("Full report exported to " + path, new Color(76, 175, 80));
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Failed to export period report",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                setStatus("Export failed", Color.RED);
//            }
//        }
//    }

    // Helper Methods

    private List<StaffRankingData> getStaffRankings(Long periodId) {
        List<AssignStaffEvaluation> evaluations = evaluationService.getAllStaffEvaluations();
        Map<Long, BigDecimal> staffScores = new HashMap<>();

        for (AssignStaffEvaluation eval : evaluations) {
            if (!eval.getPeriodId().equals(periodId) ||
                    !eval.getCompanyId().equals(currentCompanyId)) {
                continue;
            }

            BigDecimal avgScore = evaluationService.calculateAverageScore(
                    eval.getForStaffId(), eval.getPeriodId());

            // Added null check before comparison
            if (avgScore != null && avgScore.compareTo(BigDecimal.ZERO) > 0) {
                staffScores.put(eval.getForStaffId(), avgScore);
            }
        }

        List<StaffRankingData> rankings = new ArrayList<>();
        for (Map.Entry<Long, BigDecimal> entry : staffScores.entrySet()) {
            Staff staff = staffService.getStaffById(entry.getKey());
            if (staff == null) continue;

            Position position = staff.getPositionId() != null ?
                    positionService.getPositionById(staff.getPositionId()) : null;
            Department department = staff.getDepartmentId() != null ?
                    departmentService.getDepartmentById(staff.getDepartmentId()) : null;
            Office office = staff.getOfficeId() != null ?
                    officeService.getOfficeById(staff.getOfficeId()) : null;

            rankings.add(new StaffRankingData(
                    staff,
                    entry.getValue(),
                    position != null ? position.getName() : "N/A",
                    department != null ? department.getName() : "N/A",
                    office != null ? office.getName() : "N/A",
                    "Completed"
            ));
        }

        rankings.sort((a, b) -> b.averageScore.compareTo(a.averageScore));
        return rankings;
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

    private void showEvaluationDetails(AssignStaffEvaluation eval) {
        Staff staff = staffService.getStaffById(eval.getForStaffId());
        if (staff == null) return;

        Period period = periodService.getPeriodById(eval.getPeriodId());
        BigDecimal avgScore = evaluationService.calculateAverageScore(
                eval.getForStaffId(), eval.getPeriodId());


        if (avgScore == null) {
            avgScore = BigDecimal.ZERO;
        }

        String details = String.format(
                "Evaluation Details:\n\n" +
                        "Evaluation ID: %d\n" +
                        "Staff: %s (ID: %d)\n" +
                        "Period: %s\n" +
                        "Average Score: %.2f\n" +
                        "Evaluation Date: %s\n",
                eval.getId(),
                staff.getName(),
                staff.getId(),
                period != null ? period.getCode() : "N/A",
                avgScore.doubleValue(),
                eval.getAssignDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        );

        JOptionPane.showMessageDialog(this, details,
                "Evaluation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private int[] parseRange(String rangeText) {
        // Parse "Top 1-10" to [1, 10]
        String[] parts = rangeText.replace("Top ", "").split("-");
        return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
    }

    private void setStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }

    // Wrapper Classes

    private static class PeriodWrapper {
        Period period;

        PeriodWrapper(Period period) {
            this.period = period;
        }

        @Override
        public String toString() {
            return period.getCode() + " - " + period.getCode();
        }
    }

    private static class StaffRankingData {
        Staff staff;
        BigDecimal averageScore;
        String positionName;
        String departmentName;
        String officeName;
        String status;

        StaffRankingData(Staff staff, BigDecimal averageScore, String positionName,
                         String departmentName, String officeName, String status) {
            this.staff = staff;
            this.averageScore = averageScore;
            this.positionName = positionName;
            this.departmentName = departmentName;
            this.officeName = officeName;
            this.status = status;
        }
    }
}
