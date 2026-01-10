package service;

import db.DbConnection;
import model.Staff;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffService {

    public List<Staff> getAllStaffs() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    public List<Staff> getStaffsByCompanyId(Long companyId) {
        return getBy("company_id", companyId);
    }

    public List<Staff> getStaffsByDepartmentId(Long departmentId) {
        return getBy("department_id", departmentId);
    }

    public List<Staff> getStaffsByOfficeId(Long officeId) {
        return getBy("office_id", officeId);
    }

    public List<Staff> getStaffsByPositionId(Long positionId) {
        return getBy("position_id", positionId);
    }

    public boolean addStaff(Staff s) {
        String sql = """
            INSERT INTO staffs (
              company_id, name, sex, date_of_birth, place_of_birth,
              current_address, phone, email, leader_id, department_id,
              office_id, position_id, created_by, created_at, status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            fill(ps, s);
            ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(15, s.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean updateStaff(Staff s) {
        String sql = """
            UPDATE staffs SET
              company_id=?, name=?, sex=?, date_of_birth=?, place_of_birth=?,
              current_address=?, phone=?, email=?, leader_id=?, department_id=?,
              office_id=?, position_id=?, updated_at=?, status=?
            WHERE id=?
        """;

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            fill(ps, s);
            ps.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(15, s.getStatus());
            ps.setLong(16, s.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean deleteStaff(Long id) {
        String sql = "DELETE FROM staffs WHERE id=?";
        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public List<Staff> searchStaffByName(String name) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE name LIKE ?";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }

    /* ---------------- helpers ---------------- */

    private List<Staff> getBy(String column, Long id) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE " + column + "=?";

        try (Connection c = DbConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {}
        return list;
    }

    private Staff map(ResultSet rs) throws SQLException {
        Staff s = new Staff();
        s.setId(rs.getLong("id"));
        s.setCompanyId(rs.getLong("company_id"));
        s.setName(rs.getString("name"));
        s.setSex(rs.getString("sex"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            s.setDateOfBirth(dob.toLocalDate());
        }

        s.setPlaceOfBirth(rs.getString("place_of_birth"));
        s.setCurrentAddress(rs.getString("current_address"));
        s.setPhone(rs.getString("phone"));
        s.setEmail(rs.getString("email"));
        s.setLeaderId(rs.getLong("leader_id"));
        s.setDepartmentId(rs.getLong("department_id"));
        s.setOfficeId(rs.getLong("office_id"));
        s.setPositionId(rs.getLong("position_id"));
        s.setCreatedBy(rs.getLong("created_by"));
        s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        s.setUpdatedAt(rs.getTimestamp("updated_at") != null
                ? rs.getTimestamp("updated_at").toLocalDateTime()
                : null);
        s.setStatus(rs.getString("status"));
        return s;
    }

    private void fill(PreparedStatement ps, Staff s) throws SQLException {
        ps.setLong(1, s.getCompanyId());
        ps.setString(2, s.getName());
        ps.setString(3, s.getSex());
        ps.setDate(4, s.getDateOfBirth() != null ? Date.valueOf(s.getDateOfBirth()) : null);
        ps.setString(5, s.getPlaceOfBirth());
        ps.setString(6, s.getCurrentAddress());
        ps.setString(7, s.getPhone());
        ps.setString(8, s.getEmail());
        ps.setLong(9, s.getLeaderId());
        ps.setLong(10, s.getDepartmentId());
        ps.setLong(11, s.getOfficeId());
        ps.setLong(12, s.getPositionId());
        ps.setLong(13, s.getCreatedBy());
    }

    public Staff getStaffByUserId(Long userId) {
        String sql = "SELECT * FROM staffs WHERE user_id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get all active staffs for a company
     */
    public List<Staff> getStaffByCompany(Long companyId) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE company_id=? AND status='YES' ORDER BY name";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStaff(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get staff by ID
     */
    public Staff getStaffById(Long id) {
        String sql = "SELECT * FROM staffs WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStaff(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Search staffs by name (for autocomplete)
     */
    public List<Staff> searchStaffByName(Long companyId, String searchTerm) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT * FROM staffs WHERE company_id=? AND status='YES' AND LOWER(name) LIKE ? ORDER BY name LIMIT 20";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, companyId);
            ps.setString(2, "%" + searchTerm.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStaff(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get staff name by ID
     */
    public String getStaffNameById(Long id) {
        String sql = "SELECT name FROM staffs WHERE id=?";

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown";
    }

    /**
     * Get staffs with position and department names (using JOINs)
     */
    public List<StaffWithDetails> getStaffWithDetails(Long companyId) {
        List<StaffWithDetails> list = new ArrayList<>();
        String sql = """
            SELECT s.*, 
                   p.name as position_name, 
                   d.name as department_name,
                   o.name as office_name
            FROM staffs s
            LEFT JOIN positions p ON s.position_id = p.id
            LEFT JOIN departments d ON s.department_id = d.id
            LEFT JOIN offices o ON s.office_id = o.id
            WHERE s.company_id=? AND s.status='YES'
            ORDER BY s.name
        """;

        try (Connection con = DbConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StaffWithDetails swd = new StaffWithDetails();
                    swd.staff = mapResultSetToStaff(rs);
                    swd.positionName = rs.getString("position_name");
                    swd.departmentName = rs.getString("department_name");
                    swd.officeName = rs.getString("office_name");
                    list.add(swd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Helper method to map ResultSet to Staff object
     */
    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setId(rs.getLong("id"));
        staff.setCompanyId(rs.getLong("company_id"));
        staff.setName(rs.getString("name"));
        staff.setSex(rs.getString("sex"));

        Date dob = rs.getDate("date_of_birth");
        if (dob != null) {
            staff.setDateOfBirth(dob.toLocalDate());
        }

        staff.setPlaceOfBirth(rs.getString("place_of_birth"));
        staff.setCurrentAddress(rs.getString("current_address"));
        staff.setPhone(rs.getString("phone"));
        staff.setEmail(rs.getString("email"));
        staff.setLeaderId(rs.getLong("leader_id"));
        staff.setDepartmentId(rs.getLong("department_id"));
        staff.setOfficeId(rs.getLong("office_id"));
        staff.setPositionId(rs.getLong("position_id"));
        staff.setCreatedBy(rs.getLong("created_by"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            staff.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            staff.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        staff.setStatus(rs.getString("status"));

        return staff;
    }

    /**
     * Helper class to hold staff with related details
     */
    public static class StaffWithDetails {
        public Staff staff;
        public String positionName;
        public String departmentName;
        public String officeName;

        public String getDisplayName() {
            StringBuilder display = new StringBuilder(staff.getName());

            if (positionName != null && !positionName.isEmpty()) {
                display.append(" - ").append(positionName);
            }

            if (departmentName != null && !departmentName.isEmpty()) {
                display.append(" (").append(departmentName).append(")");
            }

            return display.toString();
        }
    }
}