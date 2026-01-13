package view.admin;

import model.User;
import service.PositionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.Position;
import model.User;
import service.PositionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ManagePositionsPanel extends JPanel {

    private User currentUser;
    private PositionService positionService;
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

    public ManagePositionsPanel(User user) {
        this.currentUser = user;
        this.positionService = new PositionService();
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
        JLabel titleLabel = new JLabel("Manage Positions");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(44, 49, 55));
        titlePanel.add(titleLabel, BorderLayout.WEST);
        add(titlePanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Code", "Name", "Company ID", "Created At"};
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
        JButton addButton = new JButton("Add Position");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addPosition());
        editButton.addActionListener(e -> editPosition());
        deleteButton.addActionListener(e -> deletePosition());
        refreshButton.addActionListener(e -> loadData());

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Position> positions = positionService.getAllPositions();

        for (Position position : positions) {
            if (position.getCompanyId().equals(currentUser.getCompanyId())) {
                tableModel.addRow(new Object[]{
                        position.getId(),
                        position.getName(),
                        position.getCompanyId(),
                        position.getCreatedAt()
                });
            }
        }
    }

    private void addPosition() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Position", true);
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

            Position position = new Position();
            position.setName(name);
            position.setCompanyId(currentUser.getCompanyId());
            position.setCreatedBy(currentUser.getId());

            boolean success = positionService.addPosition(position);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Position added successfully");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to add position");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editPosition() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a position to edit");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String currentCode = (String) tableModel.getValueAt(selectedRow, 1);
        String currentName = (String) tableModel.getValueAt(selectedRow, 2);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Position", true);
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

            Position position = new Position();
            position.setId(id);
            position.setName(name);
            position.setCompanyId(currentUser.getCompanyId());

            boolean success = positionService.updatePosition(position);
            if (success) {
                JOptionPane.showMessageDialog(dialog, "Position updated successfully");
                dialog.dispose();
                loadData();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to update position");
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(saveButton);
        dialog.add(cancelButton);

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletePosition() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a position to delete");
            return;
        }

        Long id = (Long) tableModel.getValueAt(selectedRow, 0);
        String name = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete position: " + name + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = positionService.deletePosition(id);
            if (success) {
                JOptionPane.showMessageDialog(this, "Position deleted successfully");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete position");
            }
        }
    }
}
