package view.admin;

import model.*;
import service.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing companies (Admin)
 */
public class ManageCompaniesPanel extends JPanel {
    private User currentUser;
    private CompanyService companyService;
    private JTable companiesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton, refreshButton;
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

    public ManageCompaniesPanel(User user) {
        this.currentUser = user;
        this.companyService = new CompanyService();
        initComponents();
        loadCompanies();
    }

    private void initComponents() {
        setLayout(new BorderLayout(12, 12));
        setBackground(panelBg);

        // Title
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JLabel titleLabel = new JLabel("Manage Companies");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Address", "Phone", "Email", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        companiesTable = new JTable(tableModel);
        companiesTable.setFont(tableFont);
        companiesTable.setForeground(rowText);
        companiesTable.setRowHeight(28);
        companiesTable.setShowHorizontalLines(true);
        companiesTable.setShowVerticalLines(false);
        companiesTable.setGridColor(new Color(230, 233, 238));
        companiesTable.setSelectionBackground(selectionBg);
        companiesTable.setSelectionForeground(selectionText);
        companiesTable.setIntercellSpacing(new Dimension(0, 6));
        companiesTable.getTableHeader().setReorderingAllowed(false);
        companiesTable.getTableHeader().setFont(headerFont);
        companiesTable.getTableHeader().setBackground(headerBg);
        companiesTable.getTableHeader().setForeground(headerText);
        companiesTable.getTableHeader().setPreferredSize(new Dimension(0, 34));
        companiesTable.setFillsViewportHeight(true);
        companiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        companiesTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
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

        JScrollPane scrollPane = new JScrollPane(companiesTable);
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

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        buttonPanel.setOpaque(false);
        addButton = new JButton("Add Company");
        editButton = new JButton("Edit Company");
        deleteButton = new JButton("Delete Company");
        refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addCompany());
        editButton.addActionListener(e -> editCompany());
        deleteButton.addActionListener(e -> deleteCompany());
        refreshButton.addActionListener(e -> loadCompanies());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCompanies() {
        // Call service method to load companies
        List<Company> companies = companyService.getAllCompanies();

        // Clear existing rows
        tableModel.setRowCount(0);

        // Add companies to table (will work after JDBC implementation)
        if (companies != null) {
            for (Company company : companies) {
                Object[] row = {
                        company.getId(),
                        company.getName(),
                        company.getAddress(),
                        company.getPhone(),
                        company.getEmail(),
                        company.getStatus()
                };
                tableModel.addRow(row);
            }
        }
    }

    private void addCompany() {
        // Show dialog to add new company
        JTextField nameField = new JTextField(20);
        JTextField addressField = new JTextField(20);
        JTextField phoneField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});

        JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Status:"));
        panel.add(statusCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Company",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Company company = new Company();
            company.setName(nameField.getText());
            company.setAddress(addressField.getText());
            company.setPhone(phoneField.getText());
            company.setEmail(emailField.getText());
            company.setStatus((String) statusCombo.getSelectedItem());

            // Call service method to add company
            boolean success = companyService.addCompany(company);

            if (success) {
                JOptionPane.showMessageDialog(this, "Company added successfully!");
                loadCompanies();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add company",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editCompany() {
        int selectedRow = companiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a company to edit");
            return;
        }

        // Get selected company ID
        Long companyId = (Long) tableModel.getValueAt(selectedRow, 0);
        Company company = companyService.getCompanyById(companyId);

        if (company != null) {
            // Show edit dialog with pre-filled data
            JTextField nameField = new JTextField(company.getName(), 20);
            JTextField addressField = new JTextField(company.getAddress(), 20);
            JTextField phoneField = new JTextField(company.getPhone(), 20);
            JTextField emailField = new JTextField(company.getEmail(), 20);
            JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});
            statusCombo.setSelectedItem(company.getStatus());

            JPanel panel = new JPanel(new GridLayout(5, 2, 5, 5));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Email:"));
            panel.add(emailField);
            panel.add(new JLabel("Status:"));
            panel.add(statusCombo);

            int result = JOptionPane.showConfirmDialog(this, panel, "Edit Company",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                company.setName(nameField.getText());
                company.setAddress(addressField.getText());
                company.setPhone(phoneField.getText());
                company.setEmail(emailField.getText());
                company.setStatus((String) statusCombo.getSelectedItem());

                // Call service method to update company
                boolean success = companyService.updateCompany(company);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Company updated successfully!");
                    loadCompanies();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update company",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteCompany() {
        int selectedRow = companiesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a company to delete");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this company?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Long companyId = (Long) tableModel.getValueAt(selectedRow, 0);

            // Call service method to delete company
            boolean success = companyService.deleteCompany(companyId);

            if (success) {
                JOptionPane.showMessageDialog(this, "Company deleted successfully!");
                loadCompanies();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete company",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
