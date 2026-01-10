package service;

import db.DbConnection;
import java.sql.*;

/**
 * Service for looking up department, position, and office names
 */
public class LookupService {

    public String getDepartmentNameById(Long departmentId) {
        if (departmentId == null || departmentId == 0) {
            return "N/A";
        }

        try (Connection connection = DbConnection.getConnection()) {
            String sql = "SELECT name FROM departments WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, departmentId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting department name: " + e.getMessage());
        }
        return "Unknown";
    }

    public String getPositionNameById(Long positionId) {
        if (positionId == null || positionId == 0) {
            return "N/A";
        }

        try (Connection connection = DbConnection.getConnection()) {
            String sql = "SELECT name FROM positions WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, positionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting position name: " + e.getMessage());
        }
        return "Unknown";
    }

    public String getOfficeNameById(Long officeId) {
        if (officeId == null || officeId == 0) {
            return "N/A";
        }

        try (Connection connection = DbConnection.getConnection()) {
            String sql = "SELECT name FROM offices WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setLong(1, officeId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting office name: " + e.getMessage());
        }
        return "Unknown";
    }
}