package view;

import view.admin.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.*;

/**
 * Main dashboard window for Admin users
 * Contains side navigation menu and content panel
 */
public class AdminDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private JPanel sideMenuPanel;
    private JButton activeMenuButton;
    private final Color menuBg = new Color(246, 247, 250);
    private final Color menuCardBg = Color.WHITE;
    private final Color menuBorder = new Color(220, 224, 230);
    private final Color menuText = new Color(33, 37, 41);
    private final Color menuSubtle = new Color(110, 116, 122);
    private final Color hoverBg = new Color(229, 233, 238);
    private final Color activeBg = new Color(214, 220, 230);
    private final Color activeBorder = new Color(200, 204, 210);
    private final Font menuFont = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font menuFontActive = new Font("Segoe UI", Font.BOLD, 14);
    private final Font menuSectionFont = new Font("Segoe UI", Font.BOLD, 11);

    public AdminDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
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
        menuPanel.setBackground(menuBg);
        menuPanel.setPreferredSize(new Dimension(260, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

        menuPanel.add(createSectionLabel("Navigation"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Menu buttons
        JButton dashboardButton = addMenuButton(menuPanel, "Dashboard", e -> showWelcomePanel());
        addMenuButton(menuPanel, "Manage Companies", e -> showCompaniesPanel());
        addMenuButton(menuPanel, "Manage Users", e -> showUsersPanel());
        addMenuButton(menuPanel, "Manage Departments", e -> showDepartmentsPanel());
        addMenuButton(menuPanel, "Manage Offices", e -> showOfficesPanel());
        addMenuButton(menuPanel, "Manage Positions", e -> showPositionsPanel());
        addMenuButton(menuPanel, "Manage Periods", e -> showPeriodsPanel());
        addMenuButton(menuPanel, "Manage Staff", e -> showStaffPanel());

        menuPanel.add(Box.createRigidArea(new Dimension(0, 12)));
        menuPanel.add(createSectionLabel("Evaluations"));
        menuPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        addMenuButton(menuPanel, "Evaluation Points", e -> showEvaluationPointsPanel());
        addMenuButton(menuPanel, "Assign Evaluations", e -> showAssignEvaluationsPanel());
        addMenuButton(menuPanel, "View Evaluations", e -> showEvaluationsPanel());
        addMenuButton(menuPanel, "Manage Reports", e -> showReportsPanel());

        menuPanel.add(Box.createVerticalGlue());

        setActiveMenuButton(dashboardButton);
        return menuPanel;
    }

    private JLabel createSectionLabel(String text) {
        JLabel menuLabel = new JLabel(text);
        menuLabel.setFont(menuSectionFont);
        menuLabel.setForeground(menuSubtle);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return menuLabel;
    }

    private JButton addMenuButton(JPanel panel, String text, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(220, 46));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(menuCardBg);
        button.setForeground(menuText);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(defaultMenuBorder());
        button.setFont(menuFont);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            setActiveMenuButton(button);
            action.actionPerformed(e);
        });
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeMenuButton) {
                    button.setBackground(hoverBg);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeMenuButton) {
                    button.setBackground(menuCardBg);
                }
            }
        });
        panel.add(button);
        panel.add(Box.createRigidArea(new Dimension(0, 6)));
        return button;
    }

    private Border defaultMenuBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(menuBorder, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );
    }

    private Border activeMenuBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 4, 1, 1, activeBorder),
                BorderFactory.createEmptyBorder(10, 10, 10, 14)
        );
    }

    private void setActiveMenuButton(JButton button) {
        if (activeMenuButton != null) {
            activeMenuButton.setBackground(menuCardBg);
            activeMenuButton.setBorder(defaultMenuBorder());
            activeMenuButton.setFont(menuFont);
        }
        activeMenuButton = button;
        activeMenuButton.setBackground(activeBg);
        activeMenuButton.setBorder(activeMenuBorder());
        activeMenuButton.setFont(menuFontActive);
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
