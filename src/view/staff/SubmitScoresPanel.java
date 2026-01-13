package view.staff;

import model.*;
import service.EvaluationService;
import service.LookupService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubmitScoresPanel extends JPanel {
    private User currentUser;
    private Staff staff;
    private StaffService staffService;
    private EvaluationService evaluationService;
    private PeriodService periodService;
    private LookupService lookupService;

    private JComboBox<EvaluationContext> evaluationSelector;
    private JPanel scoringContainer;
    private final Color panelBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color headerBg = new Color(233, 236, 241);
    private final Color headerText = new Color(33, 37, 41);
    private final Color rowAlt = new Color(248, 249, 252);
    private final Color rowText = new Color(40, 45, 50);
    private final Color selectionBg = new Color(214, 220, 230);
    private final Color selectionText = new Color(20, 24, 28);
    private final Color editableBg = new Color(255, 249, 230);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 13);

    public SubmitScoresPanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.lookupService = new LookupService();
        this.staff = staffService.getStaffByUserId(currentUser.getId());
        initComponents();
        loadEvaluations(null);
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        JLabel titleLabel = new JLabel("Submit Evaluation Scores");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel);

        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        selectorPanel.setOpaque(false);
        JLabel selectorLabel = new JLabel("Select Evaluation:");
        evaluationSelector = new JComboBox<>();
        evaluationSelector.setPreferredSize(new Dimension(360, 28));
        JButton refreshButton = new JButton("Refresh");
        selectorPanel.add(selectorLabel);
        selectorPanel.add(evaluationSelector);
        selectorPanel.add(refreshButton);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 6)));
        headerPanel.add(selectorPanel);
        add(headerPanel, BorderLayout.NORTH);

        scoringContainer = new JPanel(new BorderLayout());
        scoringContainer.setOpaque(false);
        add(scoringContainer, BorderLayout.CENTER);

        evaluationSelector.addActionListener(e -> renderSelectedEvaluation());
        refreshButton.addActionListener(e -> loadEvaluations(getSelectedEvaluationId()));
    }

    private void loadEvaluations(Long selectedEvalId) {
        if (staff == null) {
            JOptionPane.showMessageDialog(this,
                    "Staff profile not found for current user",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<EvaluationContext> contexts = new ArrayList<>();
        List<AssignStaffEvaluation> staffEvals = evaluationService.getAllStaffEvaluations();
        if (staffEvals == null) {
            staffEvals = new ArrayList<>();
        }

        for (AssignStaffEvaluation ase : staffEvals) {
            List<AssignStaffEvaluationList> evaluators =
                    evaluationService.getEvaluationListItems(ase.getId());

            for (AssignStaffEvaluationList evaluator : evaluators) {
                if (evaluator.getEvaluationStaffId().equals(staff.getId())) {
                    // Get evaluation details
                    String staffName = staffService.getStaffNameById(ase.getForStaffId());

                    List<EvaluationPoint> points =
                            evaluationService.getAssignedEvaluationPoints(ase.getId());

                    List<ASEAssignScore> submittedScores =
                            evaluationService.getScoresByEvaluationListId(evaluator.getId());

                    int totalCriteria = points.size();
                    int submittedCount = submittedScores.size();

                    String progress = submittedCount + "/" + totalCriteria;
                    String status = submittedCount == totalCriteria ? "Completed" :
                            submittedCount > 0 ? "In Progress" : "Pending";

                    contexts.add(new EvaluationContext(ase, evaluator, staffName, points,
                            submittedCount, totalCriteria, progress, status));

                    break; // Only add once per evaluation
                }
            }
        }

        DefaultComboBoxModel<EvaluationContext> model =
                new DefaultComboBoxModel<>(contexts.toArray(new EvaluationContext[0]));
        evaluationSelector.setModel(model);
        if (!contexts.isEmpty()) {
            int selectedIndex = 0;
            if (selectedEvalId != null) {
                for (int i = 0; i < contexts.size(); i++) {
                    if (selectedEvalId.equals(contexts.get(i).evaluation.getId())) {
                        selectedIndex = i;
                        break;
                    }
                }
            }
            evaluationSelector.setSelectedIndex(selectedIndex);
        }

        renderSelectedEvaluation();
    }

    private void renderSelectedEvaluation() {
        scoringContainer.removeAll();
        EvaluationContext context = (EvaluationContext) evaluationSelector.getSelectedItem();
        if (context == null) {
            scoringContainer.add(buildEmptyState(), BorderLayout.CENTER);
        } else {
            scoringContainer.add(buildScoringPanel(context), BorderLayout.CENTER);
        }
        scoringContainer.revalidate();
        scoringContainer.repaint();
    }

    private JPanel buildEmptyState() {
        JPanel emptyPanel = new JPanel(new GridBagLayout());
        emptyPanel.setOpaque(false);
        JLabel emptyLabel = new JLabel("No evaluations assigned. You're all caught up!");
        emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emptyLabel.setForeground(new Color(90, 96, 102));
        emptyPanel.add(emptyLabel);
        return emptyPanel;
    }

    private JPanel buildScoringPanel(EvaluationContext context) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        String periodLabel = "Month: N/A  Year: N/A";
        if (context.evaluation != null) {
            Period period = periodService.getPeriodById(context.evaluation.getPeriodId());
            if (period != null && period.getFromDate() != null) {
                periodLabel = "Month: " + period.getFromDate().getMonth() +
                        "  Year: " + period.getFromDate().getYear();
            } else if (period != null) {
                periodLabel = "Period: " + period.getCode();
            }
        }

        String assessorName = staff != null ? staff.getName() : "N/A";
        String assessorId = staff != null ? String.valueOf(staff.getId()) : "N/A";
        String assessorDept = "N/A";
        String assessorPosition = "N/A";
        if (staff != null) {
            String deptName = lookupService.getDepartmentNameById(staff.getDepartmentId());
            String positionName = lookupService.getPositionNameById(staff.getPositionId());
            assessorDept = deptName != null ? deptName : "N/A";
            assessorPosition = positionName != null ? positionName : "N/A";
        }

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(cardBg);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 6, 12));
        JLabel titleLabel = new JLabel("Staff Evaluation Form", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(titlePanel);

        JPanel periodPanel = new JPanel(new BorderLayout());
        periodPanel.setBackground(cardBg);
        periodPanel.setBorder(BorderFactory.createEmptyBorder(0, 12, 10, 12));
        JLabel periodInfoLabel = new JLabel(periodLabel, SwingConstants.CENTER);
        periodInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        periodInfoLabel.setForeground(new Color(90, 96, 102));
        periodPanel.add(periodInfoLabel, BorderLayout.CENTER);
        topPanel.add(periodPanel);

        JPanel targetCard = new JPanel(new BorderLayout());
        targetCard.setBackground(cardBg);
        targetCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel targetTitle = new JLabel("Staff Being Evaluated");
        targetTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        targetTitle.setForeground(new Color(67, 73, 79));
        targetCard.add(targetTitle, BorderLayout.NORTH);
        JPanel targetGrid = new JPanel(new GridLayout(1, 2, 8, 6));
        targetGrid.setOpaque(false);
        targetGrid.add(new JLabel("Name"));
        targetGrid.add(new JLabel(context.staffName));
        targetCard.add(targetGrid, BorderLayout.CENTER);
        targetCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(targetCard);

        JPanel assessorCard = new JPanel(new BorderLayout());
        assessorCard.setBackground(cardBg);
        assessorCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel assessorTitle = new JLabel("Assessor Information");
        assessorTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        assessorTitle.setForeground(new Color(67, 73, 79));
        assessorCard.add(assessorTitle, BorderLayout.NORTH);
        JPanel assessorGrid = new JPanel(new GridLayout(2, 4, 8, 6));
        assessorGrid.setOpaque(false);
        assessorGrid.add(new JLabel("Name"));
        assessorGrid.add(new JLabel(assessorName));
        assessorGrid.add(new JLabel("ID Number"));
        assessorGrid.add(new JLabel(assessorId));
        assessorGrid.add(new JLabel("Department"));
        assessorGrid.add(new JLabel(assessorDept));
        assessorGrid.add(new JLabel("Position"));
        assessorGrid.add(new JLabel(assessorPosition));
        assessorCard.add(assessorGrid, BorderLayout.CENTER);
        assessorCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(assessorCard);

        JPanel instructionCard = new JPanel(new BorderLayout());
        instructionCard.setBackground(cardBg);
        instructionCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel instructionTitle = new JLabel("Instruction");
        instructionTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        instructionTitle.setForeground(new Color(67, 73, 79));
        instructionCard.add(instructionTitle, BorderLayout.NORTH);
        JPanel instructionList = new JPanel();
        instructionList.setLayout(new BoxLayout(instructionList, BoxLayout.Y_AXIS));
        instructionList.setOpaque(false);
        instructionList.add(new JLabel("1) Enter a score for each evaluation criterion."));
        instructionList.add(new JLabel("2) Scores must be within the allowed range."));
        instructionList.add(new JLabel("3) Save when you are done."));
        instructionCard.add(instructionList, BorderLayout.CENTER);
        instructionCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        topPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        topPanel.add(instructionCard);

        topPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        panel.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"No", "Evaluation Criteria", "Score (" + context.staffName + ")"};
        DefaultTableModel scoreTableModel = new DefaultTableModel(columns, 0);
        final int targetColumnIndex = 2;

        // Load current scores
        Map<Long, ASEAssignScore> existingScores = new HashMap<>();
        List<ASEAssignScore> scores =
                evaluationService.getScoresByEvaluationListId(context.evaluatorListItem.getId());

        for (ASEAssignScore score : scores) {
            existingScores.put(score.getEvaluationPointId(), score);
        }

        // Populate table
        for (EvaluationPoint point : context.points) {
            ASEAssignScore existing = existingScores.get(point.getId());

            Object[] row = new Object[columns.length];
            row[0] = scoreTableModel.getRowCount() + 1;
            row[1] = point.getName();
            row[2] = "";
            if (existing != null) {
                row[targetColumnIndex] = existing.getScore().toString();
            }
            scoreTableModel.addRow(row);
        }

        // Make only staff columns editable
        JTable editableScoreTable = new JTable(scoreTableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 2;
            }
        };

        editableScoreTable.setFont(tableFont);
        editableScoreTable.setForeground(rowText);
        editableScoreTable.setRowHeight(28);
        editableScoreTable.setShowHorizontalLines(true);
        editableScoreTable.setShowVerticalLines(false);
        editableScoreTable.setGridColor(new Color(230, 233, 238));
        editableScoreTable.setSelectionBackground(selectionBg);
        editableScoreTable.setSelectionForeground(selectionText);
        editableScoreTable.setIntercellSpacing(new Dimension(0, 6));
        editableScoreTable.getTableHeader().setReorderingAllowed(false);
        editableScoreTable.getTableHeader().setFont(headerFont);
        editableScoreTable.getTableHeader().setBackground(headerBg);
        editableScoreTable.getTableHeader().setForeground(headerText);
        editableScoreTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        editableScoreTable.setFillsViewportHeight(true);
        editableScoreTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        editableScoreTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    if (column >= 2) {
                        c.setBackground(editableBg);
                    } else {
                        c.setBackground(row % 2 == 0 ? cardBg : rowAlt);
                    }
                }
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(editableScoreTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardBg);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardBg);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        panel.add(tableCard, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton viewDetailsButton = new JButton("View Details");
        JButton saveButton = new JButton("Save Scores");

        saveButton.addActionListener(e -> {

            // Force commit of the currently edited cell
            if (editableScoreTable.isEditing()) {
                editableScoreTable.getCellEditor().stopCellEditing();
            }

            int saveColumnIndex = targetColumnIndex;
            if (saveScoresFromTable(editableScoreTable, context.points,
                    context.evaluatorListItem, saveColumnIndex)) {
                loadEvaluations(context.evaluation.getId());
                JOptionPane.showMessageDialog(this,
                        "Scores saved successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        viewDetailsButton.addActionListener(e -> showEvaluationDetails(context));

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(saveButton);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(6, 8, 8, 8));
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private boolean saveScoresFromTable(JTable table, List<EvaluationPoint> points,
                                        AssignStaffEvaluationList evaluatorListItem,
                                        int targetColumnIndex) {
        try {
            int successCount = 0;
            int totalRows = points.size();

            for (int i = 0; i < totalRows; i++) {
                EvaluationPoint point = points.get(i);

                // Get new score from table
                Object newScoreObj = table.getValueAt(i, targetColumnIndex);
                if (newScoreObj == null || newScoreObj.toString().trim().isEmpty()) {
                    continue; // Skip if no score entered
                }

                String newScoreStr = newScoreObj.toString().trim();
                BigDecimal newScore;

                try {
                    newScore = new BigDecimal(newScoreStr);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid score for '" + point.getName() + "': " + newScoreStr,
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                // Validate range
                if (newScore.compareTo(point.getScoreRangeFrom()) < 0 ||
                        newScore.compareTo(point.getScoreRangeTo()) > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Score for '" + point.getName() + "' must be between " +
                                    point.getScoreRangeFrom() + " and " + point.getScoreRangeTo(),
                            "Invalid Range",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }

                String comment = "";

                // Check if score already exists
                ASEAssignScore existingScore =
                        evaluationService.getScore(evaluatorListItem.getId(), point.getId());

                boolean success;
                if (existingScore != null) {
                    // Update existing
                    existingScore.setScore(newScore);
                    existingScore.setComment(comment);
                    success = evaluationService.updateScore(existingScore);
                } else {
                    // Create new
                    ASEAssignScore newScoreObj2 = new ASEAssignScore();
                    newScoreObj2.setAssignStaffEvaluationListId(evaluatorListItem.getId());
                    newScoreObj2.setEvaluationPointId(point.getId());
                    newScoreObj2.setCompanyId(currentUser.getCompanyId());
                    newScoreObj2.setScore(newScore);
                    newScoreObj2.setComment(comment);
                    success = evaluationService.submitScore(newScoreObj2);
                }

                if (success) {
                    successCount++;
                }
            }

            if (successCount > 0) {
                return true;
            } else {
                JOptionPane.showMessageDialog(this,
                        "No scores were saved. Please enter at least one score.",
                        "No Data",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving scores: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
            return false;
        }
    }

    private void showEvaluationDetails(EvaluationContext context) {
        if (context == null) {
            return;
        }

        List<ASEAssignScore> scores =
                evaluationService.getScoresByEvaluationListId(context.evaluatorListItem.getId());
        List<EvaluationPoint> points = context.points;

        StringBuilder details = new StringBuilder();
        details.append("Evaluation for: ").append(context.staffName).append("\n\n");
        details.append("Your Submitted Scores:\n");
        details.append("=".repeat(50)).append("\n\n");

        Map<Long, ASEAssignScore> scoreMap = new HashMap<>();
        for (ASEAssignScore score : scores) {
            scoreMap.put(score.getEvaluationPointId(), score);
        }

        for (EvaluationPoint point : points) {
            details.append(point.getName()).append(":\n");
            ASEAssignScore score = scoreMap.get(point.getId());
            if (score != null) {
                details.append("  Score: ").append(score.getScore()).append("\n");
                if (score.getComment() != null && !score.getComment().isEmpty()) {
                    details.append("  Comment: ").append(score.getComment()).append("\n");
                }
            } else {
                details.append("  Not submitted yet\n");
            }
            details.append("\n");
        }

        JTextArea textArea = new JTextArea(details.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane,
                "Evaluation Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private Long getSelectedEvaluationId() {
        EvaluationContext context = (EvaluationContext) evaluationSelector.getSelectedItem();
        return context != null && context.evaluation != null ? context.evaluation.getId() : null;
    }

    private static class EvaluationContext {
        private final AssignStaffEvaluation evaluation;
        private final AssignStaffEvaluationList evaluatorListItem;
        private final String staffName;
        private final List<EvaluationPoint> points;
        private final int submittedCount;
        private final int totalCriteria;
        private final String progress;
        private final String status;

        private EvaluationContext(AssignStaffEvaluation evaluation,
                                  AssignStaffEvaluationList evaluatorListItem,
                                  String staffName,
                                  List<EvaluationPoint> points,
                                  int submittedCount,
                                  int totalCriteria,
                                  String progress,
                                  String status) {
            this.evaluation = evaluation;
            this.evaluatorListItem = evaluatorListItem;
            this.staffName = staffName != null ? staffName : "N/A";
            this.points = points;
            this.submittedCount = submittedCount;
            this.totalCriteria = totalCriteria;
            this.progress = progress;
            this.status = status;
        }

        @Override
        public String toString() {
            return staffName + "  (" + progress + ", " + status + ")";
        }
    }
}



