package view.admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import model.*;
import service.*;

/**
 * Panel for managing users (Admin)
 */
public class ManageUsersPanel extends JPanel {
    private User currentUser;
    private UserService userService;
    private JTable usersTable;
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

    public ManageUsersPanel(User user) {
        this.currentUser = user;
        this.userService = new UserService();
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JLabel titleLabel = new JLabel("Manage Users");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Username", "User Group", "Description", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usersTable = new JTable(tableModel);
        usersTable.setFont(tableFont);
        usersTable.setForeground(rowText);
        usersTable.setRowHeight(28);
        usersTable.setShowHorizontalLines(true);
        usersTable.setShowVerticalLines(false);
        usersTable.setGridColor(new Color(230, 233, 238));
        usersTable.setSelectionBackground(selectionBg);
        usersTable.setSelectionForeground(selectionText);
        usersTable.setIntercellSpacing(new Dimension(0, 6));
        usersTable.getTableHeader().setReorderingAllowed(false);
        usersTable.getTableHeader().setFont(headerFont);
        usersTable.getTableHeader().setBackground(headerBg);
        usersTable.getTableHeader().setForeground(headerText);
        usersTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        usersTable.setFillsViewportHeight(true);
        usersTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        usersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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
        JScrollPane scrollPane = new JScrollPane(usersTable);
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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.setOpaque(false);
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton deleteButton = new JButton("Delete User");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        deleteButton.addActionListener(e -> deleteUser());
        refreshButton.addActionListener(e -> loadUsers());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUsers() {
        List<User> users = userService.getAllUsers();
        tableModel.setRowCount(0);

        if (users != null) {
            for (User user : users) {
                Object[] row = {
                        user.getId(),
                        user.getUsername(),
                        user.getUserGroup(),
                        user.getDescription(),
                        user.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addUser() {
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JTextField descriptionField = new JTextField(20);
        JComboBox<String> userGroupCombo = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        panel.add(new JLabel("User Group:"));
        panel.add(userGroupCombo);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add User",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            User user = new User();
            user.setCompanyId(currentUser.getCompanyId());
            user.setUsername(usernameField.getText());
            user.setPassword(new String(passwordField.getPassword()));
            user.setDescription(descriptionField.getText());
            user.setUserGroup((String) userGroupCombo.getSelectedItem());
            user.setStatus((String) statusCombo.getSelectedItem());

            boolean success = userService.addUser(user);

            if (success) {
                JOptionPane.showMessageDialog(this, "User added successfully!");
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add user",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit");
            return;
        }

        Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
        User user = userService.getUserById(userId);

        if (user != null) {
            JTextField usernameField = new JTextField(user.getUsername(), 20);
            JTextField descriptionField = new JTextField(user.getDescription(), 20);
            JComboBox<String> userGroupCombo = new JComboBox<>(new String[]{"ADMIN", "STAFF"});
            userGroupCombo.setSelectedItem(user.getUserGroup());
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});
            statusCombo.setSelectedItem(user.getStatus());

            JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Description:"));
            panel.add(descriptionField);
            panel.add(new JLabel("User Group:"));
            panel.add(userGroupCombo);
            panel.add(new JLabel("Status:"));
            panel.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit User",
                    JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                user.setUsername(usernameField.getText());
                user.setDescription(descriptionField.getText());
                user.setUserGroup((String) userGroupCombo.getSelectedItem());
                user.setStatus((String) statusCombo.getSelectedItem());

                boolean success = userService.updateUser(user);

                if (success) {
                    JOptionPane.showMessageDialog(this, "User updated successfully!");
                    loadUsers();
                }
            }
        }
    }

    private void deleteUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this user?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long userId = (Long) tableModel.getValueAt(selectedRow, 0);
            boolean success = userService.deleteUser(userId);

            if (success) {
                JOptionPane.showMessageDialog(this, "User deleted successfully!");
                loadUsers();
            }
        }
    }
}
