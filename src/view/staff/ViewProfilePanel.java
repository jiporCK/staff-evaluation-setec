package view.staff;

import model.Staff;
import model.User;
import service.LookupService;
import service.StaffService;

import javax.swing.*;
import java.awt.*;

public class ViewProfilePanel extends JPanel {
    private User currentUser;
    private StaffService staffService;
    private LookupService lookupService;
    private Staff staff;

    public ViewProfilePanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        this.lookupService = new LookupService();
        this.staff = staffService.getStaffByUserId(currentUser.getId());
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        if (staff == null) {
            JLabel errorLabel = new JLabel("Staff profile not found", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Get lookup names
        String departmentName = lookupService.getDepartmentNameById(staff.getDepartmentId());
        String positionName = lookupService.getPositionNameById(staff.getPositionId());
        String officeName = lookupService.getOfficeNameById(staff.getOfficeId());
        String leaderName = staffService.getStaffNameById(staff.getLeaderId());

        formPanel.add(new JLabel("Name:"));
        formPanel.add(new JLabel(staff.getName()));

        formPanel.add(new JLabel("Sex:"));
        formPanel.add(new JLabel(staff.getSex() != null ? staff.getSex() : "N/A"));

        formPanel.add(new JLabel("Date of Birth:"));
        formPanel.add(new JLabel(staff.getDateOfBirth() != null
                ? staff.getDateOfBirth().toString() : "N/A"));

        formPanel.add(new JLabel("Place of Birth:"));
        formPanel.add(new JLabel(staff.getPlaceOfBirth() != null ? staff.getPlaceOfBirth() : "N/A"));

        formPanel.add(new JLabel("Current Address:"));
        formPanel.add(new JLabel(staff.getCurrentAddress() != null ? staff.getCurrentAddress() : "N/A"));

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(new JLabel(staff.getPhone() != null ? staff.getPhone() : "N/A"));

        formPanel.add(new JLabel("Email:"));
        formPanel.add(new JLabel(staff.getEmail() != null ? staff.getEmail() : "N/A"));

        formPanel.add(new JLabel("Department:"));
        formPanel.add(new JLabel(departmentName));

        formPanel.add(new JLabel("Office:"));
        formPanel.add(new JLabel(officeName));

        formPanel.add(new JLabel("Position:"));
        formPanel.add(new JLabel(positionName));

        formPanel.add(new JLabel("Leader:"));
        formPanel.add(new JLabel(leaderName));

        formPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(staff.getStatus() != null ? staff.getStatus() : "N/A");
        if ("YES".equals(staff.getStatus())) {
            statusLabel.setForeground(new Color(0, 150, 0)); // Green for active
            statusLabel.setText("Active");
        } else {
            statusLabel.setForeground(Color.RED);
            statusLabel.setText("Inactive");
        }
        formPanel.add(statusLabel);

        add(formPanel, BorderLayout.CENTER);

        // Add a panel with edit button at the bottom
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton editButton = new JButton("Edit Profile");
        editButton.setEnabled(false); // Disabled for now
        editButton.setToolTipText("Contact admin to update your profile");
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}