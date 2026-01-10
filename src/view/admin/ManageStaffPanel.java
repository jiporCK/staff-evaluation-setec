package view.admin;

import model.Staff;
import model.User;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ManageStaffPanel extends JPanel {
    private User currentUser;
    private StaffService staffService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageStaffPanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Staff");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Sex", "DOB", "Phone", "Email", "Position ID", "Dept ID", "Office ID", "Leader ID", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Staff");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addStaff());
        editButton.addActionListener(e -> editStaff());
        deleteButton.addActionListener(e -> deleteStaff());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Staff> staffList = staffService.getAllStaffs();

        for (Staff staff : staffList) {
            if (staff.getCompanyId().equals(currentUser.getCompanyId())) {
                tableModel.addRow(new Object[]{
                        staff.getId(),
                        staff.getName(),
                        staff.getSex(),
                        staff.getDateOfBirth(),
                        staff.getPhone(),
                        staff.getEmail(),
                        staff.getPositionId(),
                        staff.getDepartmentId(),
                        staff.getOfficeId(),
                        staff.getLeaderId(),
                        staff.getStatus()
                });
            }
        }
    }

    private void addStaff() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Staff", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(550, 650);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(25);
        JComboBox<String> sexCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        JTextField dobField = new JTextField("2000-01-01", 25);
        JTextField pobField = new JTextField(25);
        JTextArea addressArea = new JTextArea(3, 25);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JTextField phoneField = new JTextField(25);
        JTextField emailField = new JTextField(25);
        JTextField leaderIdField = new JTextField(25);
        JTextField positionIdField = new JTextField(25);
        JTextField departmentIdField = new JTextField(25);
        JTextField officeIdField = new JTextField(25);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        dialog.add(sexCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dialog.add(dobField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Place of Birth:"), gbc);
        gbc.gridx = 1;
        dialog.add(pobField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        dialog.add(new JLabel("Current Address:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JScrollPane(addressArea), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Leader ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(leaderIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Position ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(positionIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Department ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Office ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(officeIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(statusCombo, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String sex = (String) sexCombo.getSelectedItem();
                String dobStr = dobField.getText().trim();
                String pob = pobField.getText().trim();
                String address = addressArea.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();

                if (name.isEmpty() || dobStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and Date of Birth are required");
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dob = LocalDate.parse(dobStr, formatter);
                Long leaderId = leaderIdField.getText().trim().isEmpty() ? null : Long.parseLong(leaderIdField.getText().trim());
                Long positionId = positionIdField.getText().trim().isEmpty() ? null : Long.parseLong(positionIdField.getText().trim());
                Long departmentId = departmentIdField.getText().trim().isEmpty() ? null : Long.parseLong(departmentIdField.getText().trim());
                Long officeId = officeIdField.getText().trim().isEmpty() ? null : Long.parseLong(officeIdField.getText().trim());

                Staff staff = new Staff();
                staff.setName(name);
                staff.setSex(sex);
                staff.setDateOfBirth(dob);
                staff.setPlaceOfBirth(pob);
                staff.setCurrentAddress(address);
                staff.setPhone(phone);
                staff.setEmail(email);
                staff.setLeaderId(leaderId);
                staff.setPositionId(positionId);
                staff.setDepartmentId(departmentId);
                staff.setOfficeId(officeId);
                staff.setStatus(status);
                staff.setCompanyId(currentUser.getCompanyId());
                staff.setCreatedBy(currentUser.getId());

                boolean success = staffService.addStaff(staff);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Staff added successfully");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add staff");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use: yyyy-MM-dd");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format for ID fields");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);

        // Fetch the full staff object
        Staff currentStaff = staffService.getStaffById(id);
        if (currentStaff == null) {
            JOptionPane.showMessageDialog(this, "Staff member not found");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Staff", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(550, 650);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField nameField = new JTextField(currentStaff.getName() != null ? currentStaff.getName() : "", 25);
        JComboBox<String> sexCombo = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        sexCombo.setSelectedItem(currentStaff.getSex());
        JTextField dobField = new JTextField(currentStaff.getDateOfBirth() != null ? currentStaff.getDateOfBirth().toString() : "", 25);
        JTextField pobField = new JTextField(currentStaff.getPlaceOfBirth() != null ? currentStaff.getPlaceOfBirth() : "", 25);
        JTextArea addressArea = new JTextArea(currentStaff.getCurrentAddress() != null ? currentStaff.getCurrentAddress() : "", 3, 25);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JTextField phoneField = new JTextField(currentStaff.getPhone() != null ? currentStaff.getPhone() : "", 25);
        JTextField emailField = new JTextField(currentStaff.getEmail() != null ? currentStaff.getEmail() : "", 25);
        JTextField leaderIdField = new JTextField(currentStaff.getLeaderId() != null ? currentStaff.getLeaderId().toString() : "", 25);
        JTextField positionIdField = new JTextField(currentStaff.getPositionId() != null ? currentStaff.getPositionId().toString() : "", 25);
        JTextField departmentIdField = new JTextField(currentStaff.getDepartmentId() != null ? currentStaff.getDepartmentId().toString() : "", 25);
        JTextField officeIdField = new JTextField(currentStaff.getOfficeId() != null ? currentStaff.getOfficeId().toString() : "", 25);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});
        statusCombo.setSelectedItem(currentStaff.getStatus());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        dialog.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Sex:"), gbc);
        gbc.gridx = 1;
        dialog.add(sexCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Date of Birth (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        dialog.add(dobField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Place of Birth:"), gbc);
        gbc.gridx = 1;
        dialog.add(pobField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTH;
        dialog.add(new JLabel("Current Address:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        dialog.add(new JScrollPane(addressArea), gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1;
        dialog.add(phoneField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        dialog.add(emailField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Leader ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(leaderIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Position ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(positionIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Department ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Office ID:"), gbc);
        gbc.gridx = 1;
        dialog.add(officeIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        dialog.add(statusCombo, gbc);

        row++;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String sex = (String) sexCombo.getSelectedItem();
                String dobStr = dobField.getText().trim();
                String pob = pobField.getText().trim();
                String address = addressArea.getText().trim();
                String phone = phoneField.getText().trim();
                String email = emailField.getText().trim();
                String status = (String) statusCombo.getSelectedItem();

                if (name.isEmpty() || dobStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and Date of Birth are required");
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dob = LocalDate.parse(dobStr, formatter);
                Long leaderId = leaderIdField.getText().trim().isEmpty() ? null : Long.parseLong(leaderIdField.getText().trim());
                Long positionId = positionIdField.getText().trim().isEmpty() ? null : Long.parseLong(positionIdField.getText().trim());
                Long departmentId = departmentIdField.getText().trim().isEmpty() ? null : Long.parseLong(departmentIdField.getText().trim());
                Long officeId = officeIdField.getText().trim().isEmpty() ? null : Long.parseLong(officeIdField.getText().trim());

                Staff staff = new Staff();
                staff.setId(id);
                staff.setName(name);
                staff.setSex(sex);
                staff.setDateOfBirth(dob);
                staff.setPlaceOfBirth(pob);
                staff.setCurrentAddress(address);
                staff.setPhone(phone);
                staff.setEmail(email);
                staff.setLeaderId(leaderId);
                staff.setPositionId(positionId);
                staff.setDepartmentId(departmentId);
                staff.setOfficeId(officeId);
                staff.setStatus(status);
                staff.setCompanyId(currentUser.getCompanyId());

                boolean success = staffService.updateStaff(staff);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Staff updated successfully");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update staff");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use: yyyy-MM-dd");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid number format for ID fields");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        dialog.add(buttonPanel, gbc);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteStaff() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete staff: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = staffService.deleteStaff(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Staff deleted successfully");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete staff");
            }
        }
    }
}