package view;

import model.User;
import view.staff.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard window for Staff users
 * Contains side navigation menu and content panel
 */
public class StaffDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private JPanel sideMenuPanel;

    public StaffDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Staff Evaluation System - Staff Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        // Top panel with user info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(41, 128, 185));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Staff)");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Side menu panel
        sideMenuPanel = createSideMenu();
        add(sideMenuPanel, BorderLayout.WEST);

        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        showWelcomePanel();
        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSideMenu() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(52, 152, 219));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Menu buttons
        addMenuButton(menuPanel, "Dashboard", e -> showWelcomePanel());
        addMenuButton(menuPanel, "My Profile", e -> showProfilePanel());
//        addMenuButton(menuPanel, "Pending Evaluations", e -> showPendingEvaluationsPanel());
        addMenuButton(menuPanel, "My Results", e -> showMyResultsPanel());
        addMenuButton(menuPanel, "Submit Scores", e -> showSubmitScoresPanel());

        // Add glue to push buttons to top
        menuPanel.add(Box.createVerticalGlue());

        return menuPanel;
    }

    private void addMenuButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(action);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();
        JLabel welcomeLabel = new JLabel("Welcome to Staff Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showProfilePanel() {
        contentPanel.removeAll();
        contentPanel.add(new ViewProfilePanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

//    private void showPendingEvaluationsPanel() {
//        contentPanel.removeAll();
//        // Assuming currentUser has getStaffId() method, otherwise use getId()
//        Long staffId = currentUser.getId(); // Or currentUser.getStaffId() if available
//        contentPanel.add(new MyPendingEvaluationsPanel(staffId, currentUser.getCompanyId()), BorderLayout.CENTER);
//        contentPanel.revalidate();
//        contentPanel.repaint();
//    }

    private void showMyResultsPanel() {
        contentPanel.removeAll();
        Long staffId = currentUser.getId(); // Or currentUser.getStaffId() if available
        contentPanel.add(new MyEvaluationResultsPanel(staffId, currentUser.getCompanyId()), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showMyEvaluationsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ViewMyEvaluationsPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showSubmitScoresPanel() {
        contentPanel.removeAll();
        contentPanel.add(new SubmitScoresPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        }
    }
}