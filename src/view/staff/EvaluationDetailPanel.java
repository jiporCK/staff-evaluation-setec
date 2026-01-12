package view.staff;

import model.*;
import service.EvaluationService;
import service.PeriodService;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationDetailPanel extends JPanel {

    private EvaluationService evaluationService;
    private PeriodService periodService;
    private StaffService staffService;
    private Long evaluationId;
    private Long companyId;
    private JTable scoresTable;
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
    private final Font sectionTitleFont = new Font("Segoe UI", Font.BOLD, 13);
    private final Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 13);

    public EvaluationDetailPanel(Long evaluationId, Long companyId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.evaluationId = evaluationId;
        this.companyId = companyId;

        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        // Get evaluation details
        AssignStaffEvaluation evaluation = getEvaluationById(evaluationId);

        if (evaluation == null) {
            add(new JLabel("Evaluation not found", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        // Header Panel with evaluation info
        JPanel headerPanel = createHeaderPanel(evaluation);
        add(headerPanel, BorderLayout.NORTH);

        // Scores Table - Now shows breakdown by criterion
        JPanel scoresPanel = new JPanel(new BorderLayout());
        scoresPanel.setBackground(cardBg);
        scoresPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel scoresLabel = new JLabel("Evaluator Scores by Criterion");
        scoresLabel.setFont(sectionTitleFont);
        scoresLabel.setForeground(new Color(67, 73, 79));
        scoresPanel.add(scoresLabel, BorderLayout.NORTH);

        String[] columns = {"Evaluator", "Criterion", "Score", "Comment", "Submitted At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoresTable = new JTable(tableModel);
        scoresTable.setFont(tableFont);
        scoresTable.setForeground(rowText);
        scoresTable.setRowHeight(28);
        scoresTable.setShowHorizontalLines(true);
        scoresTable.setShowVerticalLines(false);
        scoresTable.setGridColor(new Color(230, 233, 238));
        scoresTable.setSelectionBackground(selectionBg);
        scoresTable.setSelectionForeground(selectionText);
        scoresTable.setIntercellSpacing(new Dimension(0, 6));
        scoresTable.getTableHeader().setReorderingAllowed(false);
        scoresTable.getTableHeader().setFont(headerFont);
        scoresTable.getTableHeader().setBackground(headerBg);
        scoresTable.getTableHeader().setForeground(headerText);
        scoresTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        scoresTable.setFillsViewportHeight(true);
        scoresTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        scoresTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardBg);
        scoresPanel.add(scrollPane, BorderLayout.CENTER);

        add(scoresPanel, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel(evaluation);
        add(summaryPanel, BorderLayout.SOUTH);

        loadScores();
    }

    private JPanel createHeaderPanel(AssignStaffEvaluation evaluation) {
        JPanel contentPanel = new JPanel(new GridLayout(7, 2, 10, 5));
        contentPanel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel sectionLabel = new JLabel("Evaluation Information");
        sectionLabel.setFont(sectionTitleFont);
        sectionLabel.setForeground(new Color(67, 73, 79));
        panel.add(sectionLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        Period period = periodService.getPeriodById(evaluation.getPeriodId());
        String periodInfo = period != null ?
                period.getCode() + " (" + period.getFromDate() + " to " + period.getToDate() + ")" :
                "N/A";

        String staffName = staffService.getStaffNameById(evaluation.getForStaffId());
        String assignedByName = staffService.getStaffNameById(evaluation.getAssignByStaffId());

        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evaluationId);

        contentPanel.add(new JLabel("Evaluation ID:"));
        contentPanel.add(new JLabel(evaluation.getId().toString()));

        contentPanel.add(new JLabel("Period:"));
        contentPanel.add(new JLabel(periodInfo));

        contentPanel.add(new JLabel("Staff Being Evaluated:"));
        contentPanel.add(new JLabel(staffName));

        contentPanel.add(new JLabel("Assigned By:"));
        contentPanel.add(new JLabel(assignedByName));

        contentPanel.add(new JLabel("Assign Date:"));
        contentPanel.add(new JLabel(evaluation.getAssignDate().toString()));

        contentPanel.add(new JLabel("Evaluation Criteria:"));
        contentPanel.add(new JLabel(assignedPoints.size() + " criteria"));

        contentPanel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(evaluation.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(cardBg);
        contentPanel.add(new JScrollPane(descArea));

        return panel;
    }

    private void loadScores() {
        tableModel.setRowCount(0);

        // Get all evaluators for this evaluation
        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evaluationId);
        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evaluationId);

        for (AssignStaffEvaluationList evaluator : evaluators) {
            String evaluatorName = staffService.getStaffNameById(evaluator.getEvaluationStaffId());

            // Get all scores for this evaluator
            List<ASEAssignScore> scores =
                    evaluationService.getScoresByEvaluationListId(evaluator.getId());

            // Create a map for quick lookup
            Map<Long, ASEAssignScore> scoreMap = new HashMap<>();
            for (ASEAssignScore score : scores) {
                scoreMap.put(score.getEvaluationPointId(), score);
            }

            // Display score for each criterion
            for (EvaluationPoint point : assignedPoints) {
                ASEAssignScore score = scoreMap.get(point.getId());

                if (score != null) {
                    tableModel.addRow(new Object[]{
                            evaluatorName,
                            point.getName(),
                            score.getScore(),
                            score.getComment() != null ? score.getComment() : "",
                            score.getCreatedAt()
                    });
                } else {
                    tableModel.addRow(new Object[]{
                            evaluatorName,
                            point.getName(),
                            "Not submitted",
                            "",
                            "N/A"
                    });
                }
            }
        }
    }

    private JPanel createSummaryPanel(AssignStaffEvaluation evaluation) {
        JPanel contentPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        contentPanel.setOpaque(false);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel sectionLabel = new JLabel("Summary");
        sectionLabel.setFont(sectionTitleFont);
        sectionLabel.setForeground(new Color(67, 73, 79));
        panel.add(sectionLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        // Get all evaluators and criteria
        List<AssignStaffEvaluationList> evaluators =
                evaluationService.getEvaluationListItems(evaluationId);
        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evaluationId);

        int totalEvaluators = evaluators.size();
        int totalCriteria = assignedPoints.size();
        int totalPossibleScores = totalEvaluators * totalCriteria;
        int submittedScores = 0;
        BigDecimal totalScore = BigDecimal.ZERO;

        for (AssignStaffEvaluationList evaluator : evaluators) {
            List<ASEAssignScore> scores =
                    evaluationService.getScoresByEvaluationListId(evaluator.getId());

            for (ASEAssignScore score : scores) {
                submittedScores++;
                totalScore = totalScore.add(score.getScore());
            }
        }

        BigDecimal averageScore = submittedScores > 0 ?
                totalScore.divide(BigDecimal.valueOf(submittedScores), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        // Calculate completion percentage
        double completionRate = totalPossibleScores > 0 ?
                (submittedScores * 100.0 / totalPossibleScores) : 0;

        contentPanel.add(new JLabel("Total Evaluators:"));
        contentPanel.add(new JLabel(String.valueOf(totalEvaluators)));

        contentPanel.add(new JLabel("Evaluation Criteria:"));
        contentPanel.add(new JLabel(String.valueOf(totalCriteria)));

        contentPanel.add(new JLabel("Total Possible Scores:"));
        contentPanel.add(new JLabel(String.valueOf(totalPossibleScores)));

        contentPanel.add(new JLabel("Scores Submitted:"));
        contentPanel.add(new JLabel(submittedScores + " / " + totalPossibleScores +
                String.format(" (%.1f%%)", completionRate)));

        contentPanel.add(new JLabel("Average Score:"));
        JLabel avgLabel = new JLabel(String.format("%.2f", averageScore));
        avgLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Color code the average (assuming 1-5 scale, adjust if needed)
        if (averageScore.compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            avgLabel.setForeground(new Color(0, 150, 0)); // Green
        } else if (averageScore.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
            avgLabel.setForeground(new Color(200, 150, 0)); // Orange
        } else if (averageScore.compareTo(BigDecimal.ZERO) > 0) {
            avgLabel.setForeground(Color.RED);
        }

        contentPanel.add(avgLabel);

        return panel;
    }

    private AssignStaffEvaluation getEvaluationById(Long id) {
        List<AssignStaffEvaluation> all = evaluationService.getAllStaffEvaluations();
        for (AssignStaffEvaluation ase : all) {
            if (ase.getId().equals(id)) {
                return ase;
            }
        }
        return null;
    }
}
