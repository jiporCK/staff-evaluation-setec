package service;

import db.DbConnection;
import model.Period;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PeriodService {

    public List<Period> getAllPeriods() {
        List<Period> list = new ArrayList<>();
        String sql = "SELECT * FROM periods";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {}
        return list;
    }

    public Period getPeriodById(Long id) {
        String sql = "SELECT * FROM periods WHERE id=?";
        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return map(rs);

        } catch (SQLException e) {}
        return null;
    }

    public List<Period> getPeriodsByCompanyId(Long companyId) {
        List<Period> list = new ArrayList<>();
        String sql = "SELECT * FROM periods WHERE company_id=?";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, companyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {}
        return list;
    }

    public List<Period> getActivePeriods() {
        List<Period> list = new ArrayList<>();
        String sql = "SELECT * FROM periods WHERE status='YES'";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {}
        return list;
    }

    public boolean addPeriod(Period p) {
        String sql = """
            INSERT INTO periods
            (company_id, code, from_date, to_date, created_by, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, p.getCompanyId());
            ps.setString(2, p.getCode());
            ps.setTimestamp(3, Timestamp.valueOf(p.getFromDate()));
            ps.setTimestamp(4, Timestamp.valueOf(p.getToDate()));
            ps.setLong(5, p.getCreatedBy());
            ps.setString(6, p.getStatus());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean updatePeriod(Period p) {
        String sql = """
            UPDATE periods SET
            company_id=?, code=?, from_date=?, to_date=?, status=?, updated_at=?
            WHERE id=?
        """;

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, p.getCompanyId());
            ps.setString(2, p.getCode());
            ps.setTimestamp(3, Timestamp.valueOf(p.getFromDate()));
            ps.setTimestamp(4, Timestamp.valueOf(p.getToDate()));
            ps.setString(5, p.getStatus());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(7, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deletePeriod(Long id) {
        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM periods WHERE id=?")) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private Period map(ResultSet rs) throws SQLException {
        Period p = new Period();
        p.setId(rs.getLong("id"));
        p.setCompanyId(rs.getLong("company_id"));
        p.setCode(rs.getString("code"));
        p.setFromDate(rs.getTimestamp("from_date").toLocalDateTime());
        p.setToDate(rs.getTimestamp("to_date").toLocalDateTime());
        p.setCreatedBy(rs.getLong("created_by"));
        p.setStatus(rs.getString("status"));
        p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        p.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime()
                : null);
        return p;
    }
}

