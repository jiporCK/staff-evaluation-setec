package service;

import model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service class for Evaluation-related database operations
 */
public class EvaluationService {

    /**
     * Retrieves all evaluation points
     * @return List of all evaluation points
     */
    public List<EvaluationPoint> getAllEvaluationPoints() {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves all staff evaluations
     * @return List of all assign staff evaluations
     */
    public List<AssignStaffEvaluation> getAllStaffEvaluations() {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves evaluations assigned to a specific staff member
     * @param staffId Staff ID
     * @return List of evaluations for this staff
     */
    public List<AssignStaffEvaluation> getEvaluationsForStaff(Long staffId) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves evaluations assigned by a specific staff member
     * @param staffId Staff ID
     * @return List of evaluations assigned by this staff
     */
    public List<AssignStaffEvaluation> getEvaluationsAssignedBy(Long staffId) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves evaluation list items for a specific evaluation
     * @param assignStaffEvaluationId Evaluation ID
     * @return List of evaluation list items
     */
    public List<AssignStaffEvaluationList> getEvaluationListItems(Long assignStaffEvaluationId) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Creates a new staff evaluation assignment
     * @param evaluation AssignStaffEvaluation object
     * @return true if successful
     */
    public boolean createStaffEvaluation(AssignStaffEvaluation evaluation) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Adds an evaluator to an evaluation
     * @param evaluationList AssignStaffEvaluationList object
     * @return true if successful
     */
    public boolean addEvaluatorToEvaluation(AssignStaffEvaluationList evaluationList) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Submits a score for an evaluation
     * @param score ASEAssignScore object
     * @return true if successful
     */
    public boolean submitScore(ASEAssignScore score) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Updates an existing score
     * @param score ASEAssignScore object
     * @return true if successful
     */
    public boolean updateScore(ASEAssignScore score) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Retrieves the score for a specific evaluation list item
     * @param evaluationListId Evaluation list ID
     * @return ASEAssignScore object or null
     */
    public ASEAssignScore getScoreByEvaluationListId(Long evaluationListId) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Calculates average score for a staff member in a period
     * @param staffId Staff ID
     * @param periodId Period ID
     * @return Average score as BigDecimal
     */
    public BigDecimal calculateAverageScore(Long staffId, Long periodId) {
        // TODO: Implement JDBC logic with aggregate function
        return null;
    }

    /**
     * Retrieves evaluations where a staff member needs to submit scores
     * @param evaluatorStaffId Evaluator staff ID
     * @return List of pending evaluations
     */
    public List<AssignStaffEvaluationList> getPendingEvaluations(Long evaluatorStaffId) {
        // TODO: Implement JDBC logic
        return null;
    }
}
