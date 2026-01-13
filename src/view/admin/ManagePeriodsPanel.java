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

    public ManagePeriodsPanel(User user) {
        this.currentUser = user;
        this.periodService = new PeriodService();
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
        JLabel titleLabel = new JLabel("Manage Periods");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Code", "From Date", "To Date", "Status", "Created At"};
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
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
