package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EvaluationPoint {
    private Long id;
    private Long companyId;
    private String name;
    private String description;
    private BigDecimal scoreRangeFrom;
    private BigDecimal scoreRangeTo;
    private BigDecimal weight;
    private String status;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public EvaluationPoint() {
        this.weight = BigDecimal.ONE;
        this.status = "YES";
    }

    public EvaluationPoint(Long id, Long companyId, String name, String description,
                           BigDecimal scoreRangeFrom, BigDecimal scoreRangeTo,
                           BigDecimal weight, Long createdBy) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.scoreRangeFrom = scoreRangeFrom;
        this.scoreRangeTo = scoreRangeTo;
        this.weight = weight != null ? weight : BigDecimal.ONE;
        this.createdBy = createdBy;
        this.status = "YES";
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getScoreRangeFrom() {
        return scoreRangeFrom;
    }

    public void setScoreRangeFrom(BigDecimal scoreRangeFrom) {
        this.scoreRangeFrom = scoreRangeFrom;
    }

    public BigDecimal getScoreRangeTo() {
        return scoreRangeTo;
    }

    public void setScoreRangeTo(BigDecimal scoreRangeTo) {
        this.scoreRangeTo = scoreRangeTo;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
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
        return name + " (" + scoreRangeFrom + "-" + scoreRangeTo + ")" +
                (weight != null && weight.compareTo(BigDecimal.ONE) != 0 ? " [weight: " + weight + "]" : "");
    }
}