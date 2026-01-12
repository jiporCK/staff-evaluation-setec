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
    private final Color panelBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font valueFont = new Font("Segoe UI", Font.PLAIN, 13);

    public ViewProfilePanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        this.lookupService = new LookupService();
        this.staff = staffService.getStaffByUserId(currentUser.getId());
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        JLabel titleLabel = new JLabel("My Profile");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        add(titleLabel, BorderLayout.NORTH);

        if (staff == null) {
            JLabel errorLabel = new JLabel("Staff profile not found", SwingConstants.CENTER);
            errorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            errorLabel.setForeground(Color.RED);
            add(errorLabel, BorderLayout.CENTER);
            return;
        }

        JPanel formPanel = new JPanel(new GridLayout(12, 2, 10, 10));
        formPanel.setBackground(cardBg);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        // Get lookup names
        String departmentName = lookupService.getDepartmentNameById(staff.getDepartmentId());
        String positionName = lookupService.getPositionNameById(staff.getPositionId());
        String officeName = lookupService.getOfficeNameById(staff.getOfficeId());
        String leaderName = staffService.getStaffNameById(staff.getLeaderId());

        formPanel.add(createLabel("Name:"));
        formPanel.add(createValueLabel(staff.getName()));

        formPanel.add(createLabel("Sex:"));
        formPanel.add(createValueLabel(staff.getSex() != null ? staff.getSex() : "N/A"));

        formPanel.add(createLabel("Date of Birth:"));
        formPanel.add(createValueLabel(staff.getDateOfBirth() != null
                ? staff.getDateOfBirth().toString() : "N/A"));

        formPanel.add(createLabel("Place of Birth:"));
        formPanel.add(createValueLabel(staff.getPlaceOfBirth() != null ? staff.getPlaceOfBirth() : "N/A"));

        formPanel.add(createLabel("Current Address:"));
        formPanel.add(createValueLabel(staff.getCurrentAddress() != null ? staff.getCurrentAddress() : "N/A"));

        formPanel.add(createLabel("Phone:"));
        formPanel.add(createValueLabel(staff.getPhone() != null ? staff.getPhone() : "N/A"));

        formPanel.add(createLabel("Email:"));
        formPanel.add(createValueLabel(staff.getEmail() != null ? staff.getEmail() : "N/A"));

        formPanel.add(createLabel("Department:"));
        formPanel.add(createValueLabel(departmentName));

        formPanel.add(createLabel("Office:"));
        formPanel.add(createValueLabel(officeName));

        formPanel.add(createLabel("Position:"));
        formPanel.add(createValueLabel(positionName));

        formPanel.add(createLabel("Leader:"));
        formPanel.add(createValueLabel(leaderName));

        formPanel.add(createLabel("Status:"));
        JLabel statusLabel = createValueLabel(staff.getStatus() != null ? staff.getStatus() : "N/A");
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
        bottomPanel.setOpaque(false);
        JButton editButton = new JButton("Edit Profile");
        editButton.setEnabled(false); // Disabled for now
        editButton.setToolTipText("Contact admin to update your profile");
        bottomPanel.add(editButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(new Color(72, 77, 82));
        return label;
    }

    private JLabel createValueLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(valueFont);
        label.setForeground(new Color(45, 50, 55));
        return label;
    }
}
