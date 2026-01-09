package model;

import java.time.LocalDateTime;

/**
 * Model class representing the assign_staff_evaluation_lists table
 */
public class AssignStaffEvaluationList {
    private Long id;
    private Long assignStaffEvaluationId;
    private Long evaluationStaffId;
    private Long companyId;
    private LocalDateTime createdAt;

    // Constructors
    public AssignStaffEvaluationList() {}

    public AssignStaffEvaluationList(Long id, Long assignStaffEvaluationId,
                                     Long evaluationStaffId, Long companyId) {
        this.id = id;
        this.assignStaffEvaluationId = assignStaffEvaluationId;
        this.evaluationStaffId = evaluationStaffId;
        this.companyId = companyId;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignStaffEvaluationId() {
        return assignStaffEvaluationId;
    }

    public void setAssignStaffEvaluationId(Long assignStaffEvaluationId) {
        this.assignStaffEvaluationId = assignStaffEvaluationId;
    }

    public Long getEvaluationStaffId() {
        return evaluationStaffId;
    }

    public void setEvaluationStaffId(Long evaluationStaffId) {
        this.evaluationStaffId = evaluationStaffId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Evaluation List ID: " + id;
    }
}
