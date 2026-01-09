package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Model class representing the ase_assign_scores table
 */
public class ASEAssignScore {
    private Long id;
    private Long assignStaffEvaluationListId;
    private Long companyId;
    private BigDecimal score;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public ASEAssignScore() {}

    public ASEAssignScore(Long id, Long assignStaffEvaluationListId,
                          Long companyId, BigDecimal score) {
        this.id = id;
        this.assignStaffEvaluationListId = assignStaffEvaluationListId;
        this.companyId = companyId;
        this.score = score;
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
        return "Score: " + score;
    }
}
