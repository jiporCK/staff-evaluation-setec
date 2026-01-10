package view;

import view.admin.*;

import javax.swing.*;
import java.awt.*;
import model.*;

/**
 * Main dashboard window for Admin users
 * Contains side navigation menu and content panel
 */
public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private JPanel sideMenuPanel;

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setTitle("Staff Evaluation System - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLayout(new BorderLayout());

        // Top panel with user info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(52, 73, 94));

        JLabel welcomeLabel = new JLabel("Welcome, " + currentUser.getUsername() + " (Admin)");
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
        menuPanel.setBackground(new Color(44, 62, 80));
        menuPanel.setPreferredSize(new Dimension(200, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Menu buttons
        addMenuButton(menuPanel, "Dashboard", e -> showWelcomePanel());
        addMenuButton(menuPanel, "Manage Companies", e -> showCompaniesPanel());
        addMenuButton(menuPanel, "Manage Users", e -> showUsersPanel());
        addMenuButton(menuPanel, "Manage Departments", e -> showDepartmentsPanel());
        addMenuButton(menuPanel, "Manage Offices", e -> showOfficesPanel());
        addMenuButton(menuPanel, "Manage Positions", e -> showPositionsPanel());
        addMenuButton(menuPanel, "Manage Periods", e -> showPeriodsPanel());
        addMenuButton(menuPanel, "Manage Staff", e -> showStaffPanel());
        addMenuButton(menuPanel, "Evaluation Points", e -> showEvaluationPointsPanel());
        addMenuButton(menuPanel, "Assign Evaluations", e -> showAssignEvaluationsPanel());
        addMenuButton(menuPanel, "View Evaluations", e -> showEvaluationsPanel());
        addMenuButton(menuPanel, "Manage Reports", e -> showReportsPanel());  // <-- ADD THIS LINE

        menuPanel.add(Box.createVerticalGlue());
        return menuPanel;
    }

    private void addMenuButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBackground(new Color(52, 73, 94));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(action);
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();
        JLabel welcomeLabel = new JLabel("Welcome to Admin Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showCompaniesPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageCompaniesPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUsersPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageUsersPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDepartmentsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageDepartmentsPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showOfficesPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageOfficesPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPositionsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManagePositionsPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPeriodsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManagePeriodsPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showStaffPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageStaffPanel(currentUser), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReportsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageReportsPanel(currentUser.getCompanyId()), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvaluationPointsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ManageEvaluationPointsPanel(currentUser.getCompanyId(), currentUser.getId()), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAssignEvaluationsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new AssignEvaluationsPanel(currentUser.getCompanyId(), currentUser.getId()), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showEvaluationsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ViewEvaluationsPanel(currentUser), BorderLayout.CENTER);
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