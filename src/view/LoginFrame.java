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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initComponents() {
        setTitle("Staff Evaluation System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 360);
        setMinimumSize(new Dimension(480, 330));
        setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(246, 247, 250));
        root.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("Staff Evaluation");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(new Color(28, 31, 35));

        JLabel subtitle = new JLabel("Sign in to manage evaluations and results");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(96, 102, 108));

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(82, 88, 94));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        passLabel.setForeground(new Color(82, 88, 94));

        usernameField = new JTextField(28);
        passwordField = new JPasswordField(28);
        styleTextField(usernameField);
        styleTextField(passwordField);

        loginButton = new JButton("Login");
        stylePrimaryButton(loginButton);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(title, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        formPanel.add(subtitle, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 6, 0);
        formPanel.add(userLabel, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 12, 0);
        formPanel.add(usernameField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(passLabel, gbc);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 18, 0);
        formPanel.add(passwordField, gbc);

        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 6, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(loginButton, gbc);

        card.add(formPanel, BorderLayout.CENTER);
        root.add(card, BorderLayout.CENTER);

        setContentPane(root);

        loginButton.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
    }

    private void styleTextField(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 214, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(250, 251, 252));
        field.setPreferredSize(new Dimension(320, 38));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(Color.BLACK);
        button.setBackground(new Color(229, 233, 238));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 204, 210), 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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

