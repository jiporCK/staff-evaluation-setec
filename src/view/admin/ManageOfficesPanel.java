package view.admin;

import model.User;
import service.OfficeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Office;
import model.User;
import service.OfficeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManageOfficesPanel extends JPanel {
    private User currentUser;
    private OfficeService officeService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageOfficesPanel(User user) {
        this.currentUser = user;
        this.officeService = new OfficeService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Offices");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Code", "Name", "Company ID", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add Office");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addOffice());
        editButton.addActionListener(e -> editOffice());
        deleteButton.addActionListener(e -> deleteOffice());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Office> offices = officeService.getAllOffices();

        for (Office office : offices) {
            if (office.getCompanyId().equals(currentUser.getCompanyId())) {
                tableModel.addRow(new Object[]{
                        office.getId(),
                        office.getName(),
                        office.getCompanyId(),
                        office.getCreatedAt()
                });
            }
        }
    }

    private void addOffice() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Office", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField codeField = new JTextField();
        JTextField nameField = new JTextField();

        dialog.add(new JLabel("Code:"));
        dialog.add(codeField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required");
                return;
            }

            Office office = new Office();
            office.setName(name);
            office.setCompanyId(currentUser.getCompanyId());
            office.setCreatedBy(currentUser.getId());

            boolean success = officeService.addOffice(office);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Office added successfully");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add office");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editOffice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an office to edit");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentCode = (String) tableModel.getValueAt(selectedRow, 1);
        String currentName = (String) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Office", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(400, 200);

        JTextField codeField = new JTextField(currentCode);
        JTextField nameField = new JTextField(currentName);

        dialog.add(new JLabel("Code:"));
        dialog.add(codeField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();

            if (code.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required");
                return;
            }

            Office office = new Office();
            office.setId(id);
            office.setName(name);
            office.setCompanyId(currentUser.getCompanyId());

            boolean success = officeService.updateOffice(office);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Office updated successfully");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update office");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteOffice() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an office to delete");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete office: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = officeService.deleteOffice(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Office deleted successfully");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete office");
            }
        }
    }
}
