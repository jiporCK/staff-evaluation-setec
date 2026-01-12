package view;

import model.User;
import view.staff.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Main dashboard window for Staff users
 * Contains side navigation menu and content panel
 */
public class StaffDashboard extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private JPanel sideMenuPanel;
    private final Color menuBg = new Color(246, 247, 250);
    private final Color menuCardBg = Color.WHITE;
    private final Color menuBorder = new Color(220, 224, 230);
    private final Color menuText = new Color(33, 37, 41);
    private final Color menuSubtle = new Color(110, 116, 122);
    private final Color hoverBg = new Color(229, 233, 238);
    private final Font menuFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font menuSectionFont = new Font("Segoe UI", Font.BOLD, 13);

    public StaffDashboard(User user) {
        this.currentUser = user;
        initComponents();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
        menuPanel.setBackground(menuBg);
        menuPanel.setPreferredSize(new Dimension(240, getHeight()));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(16, 14, 16, 14));

        JLabel menuLabel = new JLabel("NAVIGATION");
        menuLabel.setFont(menuSectionFont);
        menuLabel.setForeground(menuSubtle);
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(menuLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 10)));

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
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setPreferredSize(new Dimension(200, 40));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBackground(menuCardBg);
        button.setForeground(menuText);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(menuBorder, 1, true),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
        button.setFont(menuFont);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(menuCardBg);
            }
        });
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
