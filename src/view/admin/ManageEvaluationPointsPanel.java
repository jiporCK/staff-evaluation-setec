package view.admin;

import model.EvaluationPoint;
import service.EvaluationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ManageEvaluationPointsPanel extends JPanel {

    private EvaluationService evaluationService;
    private JTable table;
    private DefaultTableModel tableModel;
    private Long currentCompanyId;
    private Long currentUserId;
    private final Color pageBg = new Color(246, 247, 250);
    private final Color cardBg = Color.WHITE;
    private final Color borderColor = new Color(220, 224, 230);
    private final Color textPrimary = new Color(33, 37, 41);
    private final Color textMuted = new Color(110, 116, 122);
    private final Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private final Font subtitleFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);
    private final Font buttonFont = new Font("Segoe UI", Font.BOLD, 13);

    public ManageEvaluationPointsPanel(Long companyId, Long userId) {
        this.evaluationService = new EvaluationService();
        this.currentCompanyId = companyId;
        this.currentUserId = userId;

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        setBackground(pageBg);

        // Title
        JPanel headerPanel = new JPanel(new BorderLayout(0, 6));
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Manage Evaluation Points");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(textPrimary);
        JLabel subtitleLabel = new JLabel("Define the criteria and scoring ranges used in staff evaluations.");
        subtitleLabel.setFont(subtitleFont);
        subtitleLabel.setForeground(textMuted);
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Name", "Score From", "Score To", "Created At"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.setBackground(cardBg);
        tableCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        JLabel tableTitle = new JLabel("Evaluation Points");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTitle.setForeground(textPrimary);
        tableCard.add(tableTitle, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);
        add(tableCard, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        buttonPanel.setBackground(cardBg);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        JButton addButton = new JButton("Add New");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        styleButton(addButton);
        styleButton(editButton);
        styleButton(deleteButton);
        styleButton(refreshButton);

        addButton.addActionListener(e -> showAddDialog());
        editButton.addActionListener(e -> showEditDialog());
        deleteButton.addActionListener(e -> deleteSelected());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadData();
    }

    private void styleButton(JButton button) {
        button.setFont(buttonFont);
        button.setBackground(new Color(229, 233, 238));
        button.setForeground(textPrimary);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 204, 210), 1, true),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<EvaluationPoint> points = evaluationService.getAllEvaluationPoints();

        for (EvaluationPoint ep : points) {
            if (ep.getCompanyId().equals(currentCompanyId)) {
                tableModel.addRow(new Object[]{
                        ep.getId(),
                        ep.getName(),
                        ep.getScoreRangeFrom(),
                        ep.getScoreRangeTo(),
                        ep.getCreatedAt()
                });
            }
        }
    }

    private void showAddDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Evaluation Point", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField();
        JTextField fromField = new JTextField();
        JTextField toField = new JTextField();

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Score From:"));
        dialog.add(fromField);
        dialog.add(new JLabel("Score To:"));
        dialog.add(toField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String fromText = fromField.getText().trim();
                String toText = toField.getText().trim();

                if (name.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }

                EvaluationPoint ep = new EvaluationPoint();
                ep.setCompanyId(currentCompanyId);
                ep.setName(name);
                ep.setScoreRangeFrom(new BigDecimal(fromText));
                ep.setScoreRangeTo(new BigDecimal(toText));

                boolean success = evaluationService.createEvaluationPoint(ep, currentUserId);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Evaluation point added successfully");
                    loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add evaluation point");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Score values must be valid numbers");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showEditDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentName = (String) tableModel.getValueAt(selectedRow, 1);
        BigDecimal currentFrom = (BigDecimal) tableModel.getValueAt(selectedRow, 2);
        BigDecimal currentTo = (BigDecimal) tableModel.getValueAt(selectedRow, 3);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Evaluation Point", true);
        dialog.setLayout(new GridLayout(5, 2, 10, 10));
        dialog.setSize(400, 250);

        JTextField nameField = new JTextField(currentName);
        JTextField fromField = new JTextField(currentFrom.toString());
        JTextField toField = new JTextField(currentTo.toString());

        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Score From:"));
        dialog.add(fromField);
        dialog.add(new JLabel("Score To:"));
        dialog.add(toField);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String fromText = fromField.getText().trim();
                String toText = toField.getText().trim();

                if (name.isEmpty() || fromText.isEmpty() || toText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill all fields");
                    return;
                }

                EvaluationPoint ep = new EvaluationPoint();
                ep.setName(name);
                ep.setScoreRangeFrom(new BigDecimal(fromText));
                ep.setScoreRangeTo(new BigDecimal(toText));

                boolean success = evaluationService.updateEvaluationPoint(id, ep);

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Evaluation point updated successfully");
                    loadData();
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update evaluation point");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Score values must be valid numbers");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deleteSelected() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the evaluation point: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = evaluationService.deleteEvaluationPoint(id);

            if (success) {
                JOptionPane.showMessageDialog(this, "Evaluation point deleted successfully");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete evaluation point");
            }
        }
    }
}
