package view.admin;

import model.User;
import service.PeriodService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Period;
import model.User;
import service.PeriodService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class ManagePeriodsPanel extends JPanel {

    private User currentUser;
    private PeriodService periodService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManagePeriodsPanel(User user) {
        this.currentUser = user;
        this.periodService = new PeriodService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Manage Periods");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Code", "From Date", "To Date", "Status", "Created At"};
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
        JButton addButton = new JButton("Add Period");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addPeriod());
        editButton.addActionListener(e -> editPeriod());
        deleteButton.addActionListener(e -> deletePeriod());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Period> periods = periodService.getAllPeriods();

        for (Period period : periods) {
            if (period.getCompanyId().equals(currentUser.getCompanyId())) {
                tableModel.addRow(new Object[]{
                        period.getId(),
                        period.getCode(),
                        period.getFromDate(),
                        period.getToDate(),
                        period.getStatus(),
                        period.getCreatedAt()
                });
            }
        }
    }

    private void addPeriod() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Period", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 300);

        JTextField codeField = new JTextField();
        JTextField fromDateField = new JTextField("2024-01-01 00:00:00");
        JTextField toDateField = new JTextField("2024-12-31 23:59:59");
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});

        dialog.add(new JLabel("Code:"));
        dialog.add(codeField);
        dialog.add(new JLabel("From Date (yyyy-MM-dd HH:mm:ss):"));
        dialog.add(fromDateField);
        dialog.add(new JLabel("To Date (yyyy-MM-dd HH:mm:ss):"));
        dialog.add(toDateField);
        dialog.add(new JLabel("Status:"));
        dialog.add(statusCombo);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String fromDateStr = fromDateField.getText().trim();
            String toDateStr = toDateField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            if (code.isEmpty() || fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required");
                return;
            }

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime fromDate = LocalDateTime.parse(fromDateStr, formatter);
                LocalDateTime toDate = LocalDateTime.parse(toDateStr, formatter);

                Period period = new Period();
                period.setCode(code);
                period.setFromDate(fromDate);
                period.setToDate(toDate);
                period.setStatus(status);
                period.setCompanyId(currentUser.getCompanyId());
                period.setCreatedBy(currentUser.getId());

                boolean success = periodService.addPeriod(period);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Period added successfully");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add period");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use: yyyy-MM-dd HH:mm:ss");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editPeriod() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a period to edit");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentCode = (String) tableModel.getValueAt(selectedRow, 1);
        LocalDateTime currentFromDate = (LocalDateTime) tableModel.getValueAt(selectedRow, 2);
        LocalDateTime currentToDate = (LocalDateTime) tableModel.getValueAt(selectedRow, 3);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 4);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Period", true);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));
        dialog.setSize(450, 300);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        JTextField codeField = new JTextField(currentCode);
        JTextField fromDateField = new JTextField(currentFromDate.format(formatter));
        JTextField toDateField = new JTextField(currentToDate.format(formatter));
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"YES", "NO"});
        statusCombo.setSelectedItem(currentStatus);

        dialog.add(new JLabel("Code:"));
        dialog.add(codeField);
        dialog.add(new JLabel("From Date (yyyy-MM-dd HH:mm:ss):"));
        dialog.add(fromDateField);
        dialog.add(new JLabel("To Date (yyyy-MM-dd HH:mm:ss):"));
        dialog.add(toDateField);
        dialog.add(new JLabel("Status:"));
        dialog.add(statusCombo);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String code = codeField.getText().trim();
            String fromDateStr = fromDateField.getText().trim();
            String toDateStr = toDateField.getText().trim();
            String status = (String) statusCombo.getSelectedItem();

            if (code.isEmpty() || fromDateStr.isEmpty() || toDateStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required");
                return;
            }

            try {
                LocalDateTime fromDate = LocalDateTime.parse(fromDateStr, formatter);
                LocalDateTime toDate = LocalDateTime.parse(toDateStr, formatter);

                Period period = new Period();
                period.setId(id);
                period.setCode(code);
                period.setFromDate(fromDate);
                period.setToDate(toDate);
                period.setStatus(status);
                period.setCompanyId(currentUser.getCompanyId());

                boolean success = periodService.updatePeriod(period);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Period updated successfully");
                    dialog.dispose();
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update period");
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid date format. Use: yyyy-MM-dd HH:mm:ss");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletePeriod() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a period to delete");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String code = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete period: " + code + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = periodService.deletePeriod(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Period deleted successfully");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete period");
            }
        }
    }
}
