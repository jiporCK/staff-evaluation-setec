package service;

import db.DbConnection;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for User-related database operations
 */
public class UserService {

    /**
     * Authenticates a user with username and password
     */
    public User authenticate(String username, String password) {

        String sql = """
            SELECT * FROM users
            WHERE username = ? AND password = ? AND status = 'YES'
            """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Authentication error: " + e.getMessage());
        }

        return null;
    }

    /**
     * Retrieves all users
     */
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching users: " + e.getMessage());
        }

        return users;
    }

    /**
     * Retrieves user by ID
     */
    public User getUserById(Long id) {

        String sql = "SELECT * FROM users WHERE id = ?";
        User user = null;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = mapResultSetToUser(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching user by ID: " + e.getMessage());
        }

        return user;
    }

    /**
     * Retrieves users by company ID
     */
    public List<User> getUsersByCompanyId(Long companyId) {

        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE company_id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, companyId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching users by company: " + e.getMessage());
        }

        return users;
    }

    /**
     * Adds a new user
     */
    public boolean addUser(User user) {

        String sql = """
            INSERT INTO users (username, password, user_group, company_id, description, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getUserGroup());
            ps.setLong(4, user.getCompanyId());
            ps.setString(5, user.getDescription());
            ps.setString(6, user.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error adding user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Updates an existing user
     */
    public boolean updateUser(User user) {

        String sql = """
            UPDATE users
            SET username = ?, user_group = ?, company_id = ?, description = ?, status = ?
            WHERE id = ?
            """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getUserGroup());
            ps.setLong(3, user.getCompanyId());
            ps.setString(4, user.getDescription());
            ps.setString(5, user.getStatus());
            ps.setLong(6, user.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes user by ID
     */
    public boolean deleteUser(Long id) {

        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Changes user password
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {

        String sql = """
            UPDATE users
            SET password = ?
            WHERE id = ? AND password = ?
            """;

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newPassword);
            ps.setLong(2, userId);
            ps.setString(3, oldPassword);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error changing password: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if username exists
     */
    public boolean usernameExists(String username) {

        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.out.println("Error checking username: " + e.getMessage());
        }

        return false;
    }

    /**
     * Helper: ResultSet → User
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {

        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setUserGroup(rs.getString("user_group"));
        user.setCompanyId(rs.getLong("company_id"));
        user.setDescription(rs.getString("description"));
        user.setStatus(rs.getString("status"));

        return user;
    }
}
