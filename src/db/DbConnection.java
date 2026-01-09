package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database connection handler for PostgreSQL
 * This class provides a singleton connection to the database
 */
public class DbConnection {

    public static Connection getConnection() {

        try {
            return DriverManager.getConnection(
                    "jdbc:postgresql://localhost:5432/staff_evaluation_db",
                    "admin",
                    "admin123"
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
