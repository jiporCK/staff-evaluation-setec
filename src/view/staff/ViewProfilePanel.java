package view.staff;

import javax.swing.*;
import java.awt.*;
import model.User;
import service.StaffService;

public class ViewProfilePanel extends JPanel {
    private User currentUser;
    private StaffService staffService;

    public ViewProfilePanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        initComponents();
        loadProfile();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Create form panel with labels to display staff info
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel.add(new JLabel("Name:"));
        formPanel.add(new JLabel()); // Will be filled with data

        formPanel.add(new JLabel("Sex:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Date of Birth:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Email:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Department:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Office:"));
        formPanel.add(new JLabel());

        formPanel.add(new JLabel("Position:"));
        formPanel.add(new JLabel());

        add(formPanel, BorderLayout.CENTER);
    }

    private void loadProfile() {
        // Note: Need to link User to Staff (add staffId to User or query by user info)
        // Staff staff = staffService.getStaffByUserId(currentUser.getId());
        // Then populate the labels with staff data
    }
}
