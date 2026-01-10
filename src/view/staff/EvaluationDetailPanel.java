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

    public EvaluationDetailPanel(Long evaluationId, Long companyId) {
        this.evaluationService = new EvaluationService();
        this.periodService = new PeriodService();
        this.staffService = new StaffService();
        this.evaluationId = evaluationId;
        this.companyId = companyId;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
        scoresPanel.setBorder(BorderFactory.createTitledBorder("Evaluator Scores by Criterion"));

        String[] columns = {"Evaluator", "Criterion", "Score", "Comment", "Submitted At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoresTable = new JTable(tableModel);
        scoresTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(scoresTable);
        scoresPanel.add(scrollPane, BorderLayout.CENTER);

        add(scoresPanel, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = createSummaryPanel(evaluation);
        add(summaryPanel, BorderLayout.SOUTH);

        loadScores();
    }

    private JPanel createHeaderPanel(AssignStaffEvaluation evaluation) {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Evaluation Information"));

        Period period = periodService.getPeriodById(evaluation.getPeriodId());
        String periodInfo = period != null ?
                period.getCode() + " (" + period.getFromDate() + " to " + period.getToDate() + ")" :
                "N/A";

        String staffName = staffService.getStaffNameById(evaluation.getForStaffId());
        String assignedByName = staffService.getStaffNameById(evaluation.getAssignByStaffId());

        List<EvaluationPoint> assignedPoints =
                evaluationService.getAssignedEvaluationPoints(evaluationId);

        panel.add(new JLabel("Evaluation ID:"));
        panel.add(new JLabel(evaluation.getId().toString()));

        panel.add(new JLabel("Period:"));
        panel.add(new JLabel(periodInfo));

        panel.add(new JLabel("Staff Being Evaluated:"));
        panel.add(new JLabel(staffName));

        panel.add(new JLabel("Assigned By:"));
        panel.add(new JLabel(assignedByName));

        panel.add(new JLabel("Assign Date:"));
        panel.add(new JLabel(evaluation.getAssignDate().toString()));

        panel.add(new JLabel("Evaluation Criteria:"));
        panel.add(new JLabel(assignedPoints.size() + " criteria"));

        panel.add(new JLabel("Description:"));
        JTextArea descArea = new JTextArea(evaluation.getDescription());
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(panel.getBackground());
        panel.add(new JScrollPane(descArea));

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
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Summary"));

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

        panel.add(new JLabel("Total Evaluators:"));
        panel.add(new JLabel(String.valueOf(totalEvaluators)));

        panel.add(new JLabel("Evaluation Criteria:"));
        panel.add(new JLabel(String.valueOf(totalCriteria)));

        panel.add(new JLabel("Total Possible Scores:"));
        panel.add(new JLabel(String.valueOf(totalPossibleScores)));

        panel.add(new JLabel("Scores Submitted:"));
        panel.add(new JLabel(submittedScores + " / " + totalPossibleScores +
                String.format(" (%.1f%%)", completionRate)));

        panel.add(new JLabel("Average Score:"));
        JLabel avgLabel = new JLabel(String.format("%.2f", averageScore));
        avgLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Color code the average (assuming 1-5 scale, adjust if needed)
        if (averageScore.compareTo(BigDecimal.valueOf(4.0)) >= 0) {
            avgLabel.setForeground(new Color(0, 150, 0)); // Green
        } else if (averageScore.compareTo(BigDecimal.valueOf(3.0)) >= 0) {
            avgLabel.setForeground(new Color(200, 150, 0)); // Orange
        } else if (averageScore.compareTo(BigDecimal.ZERO) > 0) {
            avgLabel.setForeground(Color.RED);
        }

        panel.add(avgLabel);

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