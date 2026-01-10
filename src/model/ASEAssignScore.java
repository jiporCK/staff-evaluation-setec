package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class representing the ase_assign_scores table
 * Now with evaluation_point_id to link scores to specific criteria
 */
public class ASEAssignScore {
    private Long id;
    private Long assignStaffEvaluationListId;
    private Long evaluationPointId;
    private Long companyId;
    private BigDecimal score;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ASEAssignScore() {}

    public ASEAssignScore(Long id, Long assignStaffEvaluationListId,
                          Long evaluationPointId, Long companyId,
                          BigDecimal score, String comment) {
        this.id = id;
        this.assignStaffEvaluationListId = assignStaffEvaluationListId;
        this.evaluationPointId = evaluationPointId;
        this.companyId = companyId;
        this.score = score;
        this.comment = comment;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignStaffEvaluationListId() {
        return assignStaffEvaluationListId;
    }

    public void setAssignStaffEvaluationListId(Long assignStaffEvaluationListId) {
        this.assignStaffEvaluationListId = assignStaffEvaluationListId;
    }

    public Long getEvaluationPointId() {
        return evaluationPointId;
    }

    public void setEvaluationPointId(Long evaluationPointId) {
        this.evaluationPointId = evaluationPointId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Score: " + score + (comment != null && !comment.isEmpty() ? " (" + comment + ")" : "");
    }
}