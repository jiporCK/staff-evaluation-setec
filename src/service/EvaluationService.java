package service;

import db.DbConnection;
import model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EvaluationService {

    /* ===================== Evaluation Point ===================== */

    public List<EvaluationPoint> getAllEvaluationPoints() {
        List<EvaluationPoint> list = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_points WHERE status='YES'";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapEvaluationPoint(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<EvaluationPoint> getEvaluationPointsByCompany(Long companyId) {
        List<EvaluationPoint> list = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_points WHERE company_id=? AND status='YES' ORDER BY name";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvaluationPoint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createEvaluationPoint(EvaluationPoint eval, Long createdBy) {
        String sql = """
            INSERT INTO evaluation_points
            (company_id, name, description, score_range_from, score_range_to, weight, created_by, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, eval.getCompanyId());
            ps.setString(2, eval.getName());
            ps.setString(3, eval.getDescription());
            ps.setBigDecimal(4, eval.getScoreRangeFrom());
            ps.setBigDecimal(5, eval.getScoreRangeTo());
            ps.setBigDecimal(6, eval.getWeight());
            ps.setLong(7, createdBy);
            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateEvaluationPoint(Long id, EvaluationPoint eval) {
        String sql = """
            UPDATE evaluation_points
            SET name=?, description=?, score_range_from=?, score_range_to=?, weight=?, updated_at=?
            WHERE id=?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, eval.getName());
            ps.setString(2, eval.getDescription());
            ps.setBigDecimal(3, eval.getScoreRangeFrom());
            ps.setBigDecimal(4, eval.getScoreRangeTo());
            ps.setBigDecimal(5, eval.getWeight());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(7, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvaluationPoint(Long id) {
        String sql = "UPDATE evaluation_points SET status='NO' WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private EvaluationPoint mapEvaluationPoint(ResultSet rs) throws SQLException {
        EvaluationPoint ep = new EvaluationPoint();
        ep.setId(rs.getLong("id"));
        ep.setCompanyId(rs.getLong("company_id"));
        ep.setName(rs.getString("name"));
        ep.setDescription(rs.getString("description"));
        ep.setScoreRangeFrom(rs.getBigDecimal("score_range_from"));
        ep.setScoreRangeTo(rs.getBigDecimal("score_range_to"));
        ep.setWeight(rs.getBigDecimal("weight"));
        ep.setStatus(rs.getString("status"));
        ep.setCreatedBy(rs.getLong("created_by"));
        ep.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            ep.setUpdatedAt(updated.toLocalDateTime());
        }
        return ep;
    }

    /* ===================== Assign Staff Evaluation ===================== */

    public List<AssignStaffEvaluation> getAllStaffEvaluations() {
        List<AssignStaffEvaluation> list = new ArrayList<>();
        String sql = "SELECT * FROM assign_staff_evaluations ORDER BY assign_date DESC";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapAssignStaffEvaluation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<AssignStaffEvaluation> getEvaluationsForStaff(Long staffId) {
        return getASEByCondition("for_staff_id", staffId);
    }

    public List<AssignStaffEvaluation> getEvaluationsAssignedBy(Long staffId) {
        return getASEByCondition("assign_by_staff_id", staffId);
    }

    private List<AssignStaffEvaluation> getASEByCondition(String column, Long value) {
        List<AssignStaffEvaluation> list = new ArrayList<>();
        String sql = "SELECT * FROM assign_staff_evaluations WHERE " + column + "=? ORDER BY assign_date DESC";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, value);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssignStaffEvaluation(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Long createStaffEvaluation(AssignStaffEvaluation e, List<Long> evaluationPointIds) {
        Connection con = null;
        try {
            con = DbConnection.getConnection();
            con.setAutoCommit(false);

            // Insert main evaluation
            String sql = """
                INSERT INTO assign_staff_evaluations
                (period_id, company_id, assign_by_staff_id, for_staff_id,
                 assign_date, description, created_at, created_by)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

            Long evaluationId;
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, e.getPeriodId());
                ps.setLong(2, e.getCompanyId());
                ps.setLong(3, e.getAssignByStaffId());
                ps.setLong(4, e.getForStaffId());
                ps.setTimestamp(5, Timestamp.valueOf(e.getAssignDate()));
                ps.setString(6, e.getDescription());
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(8, e.getCreatedBy());

                int affectedRows = ps.executeUpdate();
                if (affectedRows == 0) {
                    con.rollback();
                    return null;
                }

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        evaluationId = generatedKeys.getLong(1);
                    } else {
                        con.rollback();
                        return null;
                    }
                }
            }

            // Insert evaluation points
            if (evaluationPointIds != null && !evaluationPointIds.isEmpty()) {
                String pointSql = """
                    INSERT INTO assign_staff_evaluation_points
                    (assign_staff_evaluation_id, evaluation_point_id, company_id, created_at)
                    VALUES (?, ?, ?, ?)
                """;

                try (PreparedStatement ps = con.prepareStatement(pointSql)) {
                    for (Long pointId : evaluationPointIds) {
                        ps.setLong(1, evaluationId);
                        ps.setLong(2, pointId);
                        ps.setLong(3, e.getCompanyId());
                        ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }

            con.commit();
            return evaluationId;

        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            ex.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private AssignStaffEvaluation mapAssignStaffEvaluation(ResultSet rs) throws SQLException {
        AssignStaffEvaluation ase = new AssignStaffEvaluation();
        ase.setId(rs.getLong("id"));
        ase.setPeriodId(rs.getLong("period_id"));
        ase.setCompanyId(rs.getLong("company_id"));
        ase.setAssignByStaffId(rs.getLong("assign_by_staff_id"));
        ase.setForStaffId(rs.getLong("for_staff_id"));
        ase.setAssignDate(rs.getTimestamp("assign_date").toLocalDateTime());
        ase.setDescription(rs.getString("description"));
        ase.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        ase.setCreatedBy(rs.getLong("created_by"));
        return ase;
    }

    /* ===================== Evaluation Points Assignment ===================== */

    public List<EvaluationPoint> getAssignedEvaluationPoints(Long assignStaffEvaluationId) {
        List<EvaluationPoint> list = new ArrayList<>();
        String sql = """
            SELECT ep.*
            FROM evaluation_points ep
            JOIN assign_staff_evaluation_points asep ON ep.id = asep.evaluation_point_id
            WHERE asep.assign_staff_evaluation_id = ?
            ORDER BY ep.name
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, assignStaffEvaluationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvaluationPoint(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ===================== Evaluation List ===================== */

    public List<AssignStaffEvaluationList> getEvaluationListItems(Long aseId) {
        List<AssignStaffEvaluationList> list = new ArrayList<>();
        String sql = "SELECT * FROM assign_staff_evaluation_lists WHERE assign_staff_evaluation_id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, aseId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvaluationList(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addEvaluatorToEvaluation(AssignStaffEvaluationList l) {
        String sql = """
            INSERT INTO assign_staff_evaluation_lists
            (assign_staff_evaluation_id, evaluation_staff_id, company_id, created_at)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, l.getAssignStaffEvaluationId());
            ps.setLong(2, l.getEvaluationStaffId());
            ps.setLong(3, l.getCompanyId());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private AssignStaffEvaluationList mapEvaluationList(ResultSet rs) throws SQLException {
        AssignStaffEvaluationList l = new AssignStaffEvaluationList();
        l.setId(rs.getLong("id"));
        l.setAssignStaffEvaluationId(rs.getLong("assign_staff_evaluation_id"));
        l.setEvaluationStaffId(rs.getLong("evaluation_staff_id"));
        l.setCompanyId(rs.getLong("company_id"));
        l.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return l;
    }

    /* ===================== Score ===================== */

    public boolean submitScore(ASEAssignScore s) {
        String sql = """
            INSERT INTO ase_assign_scores
            (assign_staff_evaluation_list_id, evaluation_point_id, company_id, score, comment, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, s.getAssignStaffEvaluationListId());
            ps.setLong(2, s.getEvaluationPointId());
            ps.setLong(3, s.getCompanyId());
            ps.setBigDecimal(4, s.getScore());
            ps.setString(5, s.getComment());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateScore(ASEAssignScore s) {
        String sql = """
            UPDATE ase_assign_scores
            SET score=?, comment=?, updated_at=?
            WHERE assign_staff_evaluation_list_id=? AND evaluation_point_id=?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setBigDecimal(1, s.getScore());
            ps.setString(2, s.getComment());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(4, s.getAssignStaffEvaluationListId());
            ps.setLong(5, s.getEvaluationPointId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ASEAssignScore getScore(Long listId, Long evaluationPointId) {
        String sql = """
            SELECT * FROM ase_assign_scores 
            WHERE assign_staff_evaluation_list_id=? AND evaluation_point_id=?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, listId);
            ps.setLong(2, evaluationPointId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapScore(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ASEAssignScore> getScoresByEvaluationListId(Long listId) {
        List<ASEAssignScore> list = new ArrayList<>();
        String sql = "SELECT * FROM ase_assign_scores WHERE assign_staff_evaluation_list_id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, listId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapScore(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ASEAssignScore mapScore(ResultSet rs) throws SQLException {
        ASEAssignScore s = new ASEAssignScore();
        s.setId(rs.getLong("id"));
        s.setAssignStaffEvaluationListId(rs.getLong("assign_staff_evaluation_list_id"));
        s.setEvaluationPointId(rs.getLong("evaluation_point_id"));
        s.setCompanyId(rs.getLong("company_id"));
        s.setScore(rs.getBigDecimal("score"));
        s.setComment(rs.getString("comment"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            s.setUpdatedAt(updated.toLocalDateTime());
        }
        return s;
    }

    /* ===================== Calculations ===================== */

    /**
     * Calculate average score for a staff member in a period
     * This averages all evaluators' scores across all criteria
     */
    public BigDecimal calculateAverageScore(Long staffId, Long periodId) {
        String sql = """
            SELECT AVG(s.score) avg_score
            FROM ase_assign_scores s
            JOIN assign_staff_evaluation_lists l ON s.assign_staff_evaluation_list_id = l.id
            JOIN assign_staff_evaluations e ON l.assign_staff_evaluation_id = e.id
            WHERE e.for_staff_id=? AND e.period_id=?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, staffId);
            ps.setLong(2, periodId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal avg = rs.getBigDecimal("avg_score");
                    return avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Calculate weighted average score for a staff member in a period
     */
    public BigDecimal calculateWeightedAverageScore(Long staffId, Long periodId) {
        String sql = """
            SELECT 
                SUM(s.score * ep.weight) / SUM(ep.weight) as weighted_avg
            FROM ase_assign_scores s
            JOIN assign_staff_evaluation_lists l ON s.assign_staff_evaluation_list_id = l.id
            JOIN assign_staff_evaluations e ON l.assign_staff_evaluation_id = e.id
            JOIN evaluation_points ep ON s.evaluation_point_id = ep.id
            WHERE e.for_staff_id=? AND e.period_id=?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, staffId);
            ps.setLong(2, periodId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal avg = rs.getBigDecimal("weighted_avg");
                    return avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Get average score per evaluation criterion for a staff member
     */
    public Map<String, BigDecimal> getScoresByCriterion(Long staffId, Long periodId) {
        Map<String, BigDecimal> scores = new HashMap<>();
        String sql = """
            SELECT 
                ep.name as criterion_name,
                AVG(s.score) as avg_score
            FROM ase_assign_scores s
            JOIN assign_staff_evaluation_lists l ON s.assign_staff_evaluation_list_id = l.id
            JOIN assign_staff_evaluations e ON l.assign_staff_evaluation_id = e.id
            JOIN evaluation_points ep ON s.evaluation_point_id = ep.id
            WHERE e.for_staff_id=? AND e.period_id=?
            GROUP BY ep.id, ep.name
            ORDER BY ep.name
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, staffId);
            ps.setLong(2, periodId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String criterion = rs.getString("criterion_name");
                    BigDecimal avg = rs.getBigDecimal("avg_score");
                    scores.put(criterion, avg.setScale(2, RoundingMode.HALF_UP));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Get pending evaluations for an evaluator (where they haven't scored all criteria yet)
     */
    public List<AssignStaffEvaluationList> getPendingEvaluations(Long evaluatorStaffId) {
        List<AssignStaffEvaluationList> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT l.*
            FROM assign_staff_evaluation_lists l
            JOIN assign_staff_evaluation_points asep 
                ON l.assign_staff_evaluation_id = asep.assign_staff_evaluation_id
            LEFT JOIN ase_assign_scores s 
                ON l.id = s.assign_staff_evaluation_list_id 
                AND asep.evaluation_point_id = s.evaluation_point_id
            WHERE l.evaluation_staff_id=? AND s.id IS NULL
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, evaluatorStaffId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEvaluationList(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Check if an evaluator has completed scoring all criteria for an evaluation
     */
    public boolean isEvaluationComplete(Long evaluationListId) {
        String sql = """
            SELECT 
                COUNT(DISTINCT asep.evaluation_point_id) as total_points,
                COUNT(DISTINCT s.evaluation_point_id) as scored_points
            FROM assign_staff_evaluation_lists l
            JOIN assign_staff_evaluation_points asep 
                ON l.assign_staff_evaluation_id = asep.assign_staff_evaluation_id
            LEFT JOIN ase_assign_scores s 
                ON l.id = s.assign_staff_evaluation_list_id 
                AND asep.evaluation_point_id = s.evaluation_point_id
            WHERE l.id = ?
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, evaluationListId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_points");
                    int scored = rs.getInt("scored_points");
                    return total > 0 && total == scored;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}