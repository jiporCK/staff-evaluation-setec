package model;

import java.time.LocalDateTime;

/**
 * Model class representing the assign_staff_evaluations table
 */
public class AssignStaffEvaluation {
    private Long id;
    private Long periodId;
    private Long companyId;
    private Long assignByStaffId;
    private Long forStaffId;
    private LocalDateTime assignDate;
    private String description;
    private LocalDateTime createdAt;
    private Long createdBy;

    // Constructors
    public AssignStaffEvaluation() {}

    public AssignStaffEvaluation(Long id, Long periodId, Long companyId,
                                 Long assignByStaffId, Long forStaffId,
                                 LocalDateTime assignDate, String description, Long createdBy) {
        this.id = id;
        this.periodId = periodId;
        this.companyId = companyId;
        this.assignByStaffId = assignByStaffId;
        this.forStaffId = forStaffId;
        this.assignDate = assignDate;
        this.description = description;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(Long periodId) {
        this.periodId = periodId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getAssignByStaffId() {
        return assignByStaffId;
    }

    public void setAssignByStaffId(Long assignByStaffId) {
        this.assignByStaffId = assignByStaffId;
    }

    public Long getForStaffId() {
        return forStaffId;
    }

    public void setForStaffId(Long forStaffId) {
        this.forStaffId = forStaffId;
    }

    public LocalDateTime getAssignDate() {
        return assignDate;
    }

    public void setAssignDate(LocalDateTime assignDate) {
        this.assignDate = assignDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Evaluation ID: " + id;
    }
}
