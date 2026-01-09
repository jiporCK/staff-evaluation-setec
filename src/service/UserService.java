package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import db.DbConnection;
import model.*;

/**
 * Service class for User-related database operations
 * Methods are placeholders - implement JDBC logic here
 */
public class UserService {

    /**
     * Authenticates a user with username and password
     * @param username Username
     * @param password Password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticate(String username, String password) {
        // TODO: Implement JDBC logic
        // Example:
         String sql = "SELECT * FROM users WHERE username = ? AND password = ? AND status = 'YES'";
         try (Connection conn = DbConnection.getConnection();
              PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, username);
             pstmt.setString(2, password);
             ResultSet rs = pstmt.executeQuery();
             if (rs.next()) {
                 User user = new User();
                 user.setId(rs.getLong("id"));
                 user.setUsername(rs.getString("username"));
                 user.setUserGroup(rs.getString("user_group"));
                 user.setCompanyId(rs.getLong("company_id"));
                 user.setDescription(rs.getString("description"));
                 user.setStatus(rs.getString("status"));

                 return user;
             }
         } catch (SQLException e) {
             System.out.println("Error: " + e.getMessage());
         }
        return null;
    }

    /**
     * Retrieves all users from the database
     * @return List of all users
     */
    public List<User> getAllUsers() {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves a user by ID
     * @param id User ID
     * @return User object or null if not found
     */
    public User getUserById(Long id) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Retrieves users by company ID
     * @param companyId Company ID
     * @return List of users in the company
     */
    public List<User> getUsersByCompanyId(Long companyId) {
        // TODO: Implement JDBC logic
        return null;
    }

    /**
     * Adds a new user to the database
     * @param user User object to add
     * @return true if successful, false otherwise
     */
    public boolean addUser(User user) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Updates an existing user
     * @param user User object with updated data
     * @return true if successful, false otherwise
     */
    public boolean updateUser(User user) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Deletes a user by ID
     * @param id User ID
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(Long id) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Changes user password
     * @param userId User ID
     * @param oldPassword Old password
     * @param newPassword New password
     * @return true if successful, false otherwise
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        // TODO: Implement JDBC logic
        return false;
    }

    /**
     * Checks if a username already exists
     * @param username Username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        // TODO: Implement JDBC logic
        return false;
    }
}
