package view.admin;

import model.Company;
import model.Staff;
import model.User;
import service.CompanyService;
import service.DepartmentService;
import service.OfficeService;
import service.PositionService;
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
    private final Color panelBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color headerBg = new Color(233, 236, 241);
    private final Color headerText = new Color(33, 37, 41);
    private final Color rowAlt = new Color(248, 249, 252);
    private final Color rowText = new Color(40, 45, 50);
    private final Color selectionBg = new Color(214, 220, 230);
    private final Color selectionText = new Color(20, 24, 28);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
    private final Font tableFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font headerFont = new Font("Segoe UI", Font.BOLD, 13);

    public ManageStaffPanel(User user) {
        this.currentUser = user;
        this.staffService = new StaffService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JLabel titleLabel = new JLabel("Manage Staff");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Sex", "DOB", "Phone", "Email", "Company", "Position", "Department", "Office", "Leader", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setFont(tableFont);
        table.setForeground(rowText);
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 233, 238));
        table.setSelectionBackground(selectionBg);
        table.setSelectionForeground(selectionText);
        table.setIntercellSpacing(new Dimension(0, 6));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setBackground(headerBg);
        table.getTableHeader().setForeground(headerText);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? cardBg : rowAlt);
                }
                return c;
            }
        });
        int[] columnWidths = {60, 160, 70, 110, 120, 180, 160, 140, 140, 140, 160, 80};
        for (int i = 0; i < columnWidths.length && i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(columnWidths[i]);
        }
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(cardBg);
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardBg);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.setOpaque(false);
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
        CompanyService companyService = new CompanyService();
        DepartmentService departmentService = new DepartmentService();
        PositionService positionService = new PositionService();
        OfficeService officeService = new OfficeService();

        java.util.Map<Long, String> companyNames = new java.util.HashMap<>();
        for (Company company : companyService.getAllCompanies()) {
            companyNames.put(company.getId(), company.getName());
        }

        java.util.Map<Long, String> positionNames = new java.util.HashMap<>();
        for (model.Position position : positionService.getAllPositions()) {
            positionNames.put(position.getId(), position.getName());
        }

        java.util.Map<Long, String> departmentNames = new java.util.HashMap<>();
        for (model.Department department : departmentService.getAllDepartments()) {
            departmentNames.put(department.getId(), department.getName());
        }

        java.util.Map<Long, String> officeNames = new java.util.HashMap<>();
        for (model.Office office : officeService.getAllOffices()) {
            officeNames.put(office.getId(), office.getName());
        }

        java.util.Map<Long, String> staffNames = new java.util.HashMap<>();
        for (Staff staff : staffList) {
            staffNames.put(staff.getId(), staff.getName());
        }

        for (Staff staff : staffList) {
            if (staff.getCompanyId().equals(currentUser.getCompanyId())) {
                tableModel.addRow(new Object[]{
                        staff.getId(),
                        staff.getName(),
                        staff.getSex(),
                        staff.getDateOfBirth(),
                        staff.getPhone(),
                        staff.getEmail(),
                        companyNames.getOrDefault(staff.getCompanyId(), "N/A"),
                        positionNames.getOrDefault(staff.getPositionId(), "N/A"),
                        departmentNames.getOrDefault(staff.getDepartmentId(), "N/A"),
                        officeNames.getOrDefault(staff.getOfficeId(), "N/A"),
                        staffNames.getOrDefault(staff.getLeaderId(), "None"),
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
        CompanyService companyService = new CompanyService();
        DepartmentService departmentService = new DepartmentService();
        PositionService positionService = new PositionService();
        OfficeService officeService = new OfficeService();

        JComboBox<ComboItem> companyCombo = new JComboBox<>();
        companyCombo.addItem(new ComboItem(null, "Select Company"));
        List<Company> companies = companyService.getAllCompanies();
        for (Company company : companies) {
            companyCombo.addItem(new ComboItem(company.getId(), company.getName()));
        }

        JComboBox<ComboItem> leaderCombo = new JComboBox<>();
        JComboBox<ComboItem> positionCombo = new JComboBox<>();
        JComboBox<ComboItem> departmentCombo = new JComboBox<>();
        JComboBox<ComboItem> officeCombo = new JComboBox<>();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});

        Runnable reloadCompanyOptions = () -> {
            Long companyId = getSelectedId(companyCombo);

            leaderCombo.removeAllItems();
            leaderCombo.addItem(new ComboItem(null, "None"));
            positionCombo.removeAllItems();
            departmentCombo.removeAllItems();
            officeCombo.removeAllItems();

            if (companyId == null) {
                return;
            }

            List<Staff> leaders = staffService.getStaffByCompany(companyId);
            for (Staff leader : leaders) {
                leaderCombo.addItem(new ComboItem(leader.getId(),
                        leader.getName() + " (ID: " + leader.getId() + ")"));
            }

            for (model.Position position : positionService.getPositionsByCompanyId(companyId)) {
                positionCombo.addItem(new ComboItem(position.getId(), position.getName()));
            }

            for (model.Department department : departmentService.getDepartmentsByCompanyId(companyId)) {
                departmentCombo.addItem(new ComboItem(department.getId(), department.getName()));
            }

            for (model.Office office : officeService.getOfficesByCompanyId(companyId)) {
                officeCombo.addItem(new ComboItem(office.getId(), office.getName()));
            }
        };

        companyCombo.addActionListener(e -> reloadCompanyOptions.run());
        reloadCompanyOptions.run();

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Company:"), gbc);
        gbc.gridx = 1;
        dialog.add(companyCombo, gbc);

        row++;
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
        dialog.add(new JLabel("Leader:"), gbc);
        gbc.gridx = 1;
        dialog.add(leaderCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        dialog.add(positionCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Office:"), gbc);
        gbc.gridx = 1;
        dialog.add(officeCombo, gbc);

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

                Long companyId = getSelectedId(companyCombo);
                if (companyId == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a company.");
                    return;
                }

                if (name.isEmpty() || dobStr.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name and Date of Birth are required");
                    return;
                }

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dob = LocalDate.parse(dobStr, formatter);
                ComboItem leaderItem = (ComboItem) leaderCombo.getSelectedItem();
                ComboItem positionItem = (ComboItem) positionCombo.getSelectedItem();
                ComboItem departmentItem = (ComboItem) departmentCombo.getSelectedItem();
                ComboItem officeItem = (ComboItem) officeCombo.getSelectedItem();

                if (positionItem == null || positionItem.id == null ||
                        departmentItem == null || departmentItem.id == null ||
                        officeItem == null || officeItem.id == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select Position, Department, and Office before saving.");
                    return;
                }

                Long leaderId = leaderItem != null ? leaderItem.id : null;
                Long positionId = positionItem.id;
                Long departmentId = departmentItem.id;
                Long officeId = officeItem.id;

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
                staff.setCompanyId(companyId);
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
                JOptionPane.showMessageDialog(dialog, "Invalid number format.");
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

    private static class ComboItem {
        private final Long id;
        private final String label;

        private ComboItem(Long id, String label) {
            this.id = id;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
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
        DepartmentService departmentService = new DepartmentService();
        PositionService positionService = new PositionService();
        OfficeService officeService = new OfficeService();

        JComboBox<ComboItem> leaderCombo = new JComboBox<>();
        leaderCombo.addItem(new ComboItem(null, "None"));
        List<Staff> leaders = staffService.getStaffByCompany(currentUser.getCompanyId());
        for (Staff leader : leaders) {
            leaderCombo.addItem(new ComboItem(leader.getId(), leader.getName() + " (ID: " + leader.getId() + ")"));
        }

        JComboBox<ComboItem> positionCombo = new JComboBox<>();
        for (model.Position position : positionService.getPositionsByCompanyId(currentUser.getCompanyId())) {
            positionCombo.addItem(new ComboItem(position.getId(), position.getName()));
        }

        JComboBox<ComboItem> departmentCombo = new JComboBox<>();
        for (model.Department department : departmentService.getDepartmentsByCompanyId(currentUser.getCompanyId())) {
            departmentCombo.addItem(new ComboItem(department.getId(), department.getName()));
        }

        JComboBox<ComboItem> officeCombo = new JComboBox<>();
        for (model.Office office : officeService.getOfficesByCompanyId(currentUser.getCompanyId())) {
            officeCombo.addItem(new ComboItem(office.getId(), office.getName()));
        }
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});
        statusCombo.setSelectedItem(currentStaff.getStatus());
        selectComboItem(leaderCombo, currentStaff.getLeaderId());
        selectComboItem(positionCombo, currentStaff.getPositionId());
        selectComboItem(departmentCombo, currentStaff.getDepartmentId());
        selectComboItem(officeCombo, currentStaff.getOfficeId());

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
        dialog.add(new JLabel("Leader:"), gbc);
        gbc.gridx = 1;
        dialog.add(leaderCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Position:"), gbc);
        gbc.gridx = 1;
        dialog.add(positionCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1;
        dialog.add(departmentCombo, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        dialog.add(new JLabel("Office:"), gbc);
        gbc.gridx = 1;
        dialog.add(officeCombo, gbc);

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
                ComboItem leaderItem = (ComboItem) leaderCombo.getSelectedItem();
                ComboItem positionItem = (ComboItem) positionCombo.getSelectedItem();
                ComboItem departmentItem = (ComboItem) departmentCombo.getSelectedItem();
                ComboItem officeItem = (ComboItem) officeCombo.getSelectedItem();

                if (positionItem == null || positionItem.id == null ||
                        departmentItem == null || departmentItem.id == null ||
                        officeItem == null || officeItem.id == null) {
                    JOptionPane.showMessageDialog(dialog,
                            "Please select Position, Department, and Office before saving.");
                    return;
                }

                Long leaderId = leaderItem != null ? leaderItem.id : null;
                Long positionId = positionItem.id;
                Long departmentId = departmentItem.id;
                Long officeId = officeItem.id;

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
                JOptionPane.showMessageDialog(dialog, "Invalid number format.");
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

    private void selectComboItem(JComboBox<ComboItem> comboBox, Long id) {
        if (comboBox == null || id == null) {
            return;
        }
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            ComboItem item = comboBox.getItemAt(i);
            if (item != null && id.equals(item.id)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private Long getSelectedId(JComboBox<ComboItem> comboBox) {
        if (comboBox == null) {
            return null;
        }
        ComboItem item = (ComboItem) comboBox.getSelectedItem();
        return item != null ? item.id : null;
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
