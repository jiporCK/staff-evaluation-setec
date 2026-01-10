package model;

import java.time.LocalDateTime;

/**
 * Model class representing the assign_staff_evaluation_points table
 * Links evaluations to specific criteria/evaluation points
 */
public class AssignStaffEvaluationPoint {
    private Long id;
    private Long assignStaffEvaluationId;
    private Long evaluationPointId;
    private Long companyId;
    private LocalDateTime createdAt;

    // Constructors
    public AssignStaffEvaluationPoint() {}

    public AssignStaffEvaluationPoint(Long id, Long assignStaffEvaluationId,
                                      Long evaluationPointId, Long companyId) {
        this.id = id;
        this.assignStaffEvaluationId = assignStaffEvaluationId;
        this.evaluationPointId = evaluationPointId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Assign Evaluation Point ID: " + id;
    }
}