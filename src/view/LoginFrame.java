package view;
import model.User;
import service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * Login window for Staff Evaluation System
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserService userService;

    public LoginFrame() {
        userService = new UserService();
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Staff Evaluation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Staff Evaluation System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        // Username label
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Username:"), gbc);

        // Username field
        usernameField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);

        // Password label
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Password:"), gbc);

        // Password field
        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);

        // Login button
        loginButton = new JButton("Login");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(loginButton, gbc);

        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());

        add(mainPanel, BorderLayout.CENTER);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter username and password",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Call service method to authenticate
        User user = userService.authenticate(username, password);

        if (user != null) {
            // Authentication successful
            this.dispose();

            // Open appropriate dashboard based on user role
            if (user.isAdmin()) {
                AdminDashboard adminDashboard = new AdminDashboard(user);
                adminDashboard.setVisible(true);
            } else if (user.isStaff()) {
                StaffDashboard staffDashboard = new StaffDashboard(user);
                staffDashboard.setVisible(true);
            }
        } else {
            // Authentication failed
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}

