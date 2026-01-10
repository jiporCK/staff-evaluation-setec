package service;

import db.DbConnection;
import model.Office;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OfficeService {

    public List<Office> getAllOffices() {
        List<Office> list = new ArrayList<>();
        String sql = "SELECT * FROM offices";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Office getOfficeById(Long id) {
        String sql = "SELECT * FROM offices WHERE id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Office> getOfficesByCompanyId(Long companyId) {
        List<Office> list = new ArrayList<>();
        String sql = "SELECT * FROM offices WHERE company_id = ?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addOffice(Office o) {
        String sql = """
            INSERT INTO offices (company_id, name, created_by, created_at)
            VALUES (?, ?, ?, ?)
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, o.getCompanyId());
            ps.setString(2, o.getName());
            ps.setLong(3, o.getCreatedBy());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOffice(Office o) {
        String sql = "UPDATE offices SET name=?, updated_at=? WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, o.getName());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, o.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteOffice(Long id) {
        String sql = "DELETE FROM offices WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Office map(ResultSet rs) throws SQLException {
        Office o = new Office();
        o.setId(rs.getLong("id"));
        o.setCompanyId(rs.getLong("company_id"));
        o.setName(rs.getString("name"));
        o.setCreatedBy(rs.getLong("created_by"));
        o.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) o.setUpdatedAt(ua.toLocalDateTime());

        return o;
    }
}
