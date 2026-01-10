package service;

import db.DbConnection;
import model.Company;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for Company-related database operations
 */
public class CompanyService {

    /**
     * Retrieves all companies from the database
     */
    public List<Company> getAllCompanies() {

        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                companies.add(mapResultSetToCompany(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching companies: " + e.getMessage());
        }

        return companies;
    }

    /**
     * Retrieves a company by ID
     */
    public Company getCompanyById(Long id) {

        String sql = "SELECT * FROM companies WHERE id = ?";
        Company company = null;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    company = mapResultSetToCompany(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching company by ID: " + e.getMessage());
        }

        return company;
    }

    /**
     * Adds a new company
     */
    public boolean addCompany(Company company) {

        String sql = """
            INSERT INTO companies (name, address, phone, email, status)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getPhone());
            ps.setString(4, company.getEmail());
            ps.setString(5, company.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding company: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing company
     */
    public boolean updateCompany(Company company) {

        String sql = """
            UPDATE companies
            SET name = ?, address = ?, phone = ?, email = ?, status = ?
            WHERE id = ?
            """;

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, company.getName());
            ps.setString(2, company.getAddress());
            ps.setString(3, company.getPhone());
            ps.setString(4, company.getEmail());
            ps.setString(5, company.getStatus());
            ps.setLong(6, company.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating company: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a company by ID
     */
    public boolean deleteCompany(Long id) {

        String sql = "DELETE FROM companies WHERE id = ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting company: " + e.getMessage());
            return false;
        }
    }

    /**
     * Searches companies by name (partial match)
     */
    public List<Company> searchCompaniesByName(String name) {

        List<Company> companies = new ArrayList<>();
        String sql = "SELECT * FROM companies WHERE name LIKE ?";

        try (Connection connection = DbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    companies.add(mapResultSetToCompany(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error searching companies: " + e.getMessage());
        }

        return companies;
    }

    /**
     * Helper method to map ResultSet → Company
     */
    private Company mapResultSetToCompany(ResultSet rs) throws SQLException {

        Company company = new Company();
        company.setId(rs.getLong("id"));
        company.setName(rs.getString("name"));
        company.setAddress(rs.getString("address"));
        company.setPhone(rs.getString("phone"));
        company.setEmail(rs.getString("email"));
        company.setStatus(rs.getString("status"));

        return company;
    }
}
