package view.admin;

import model.User;
import service.PositionService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagePositionsPanel extends JPanel {

    private User currentUser;
    private PositionService departmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManagePositionsPanel(User user) {
        this.currentUser = user;
        this.departmentService = new PositionService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Same pattern as ManageCompaniesPanel
    }

    private void loadData() {
        // Call service.getAll[Entities]()
        departmentService.getAllPositions();
    }

    private void addPosition() {
        // Show dialog, collect data, call service.add[Entity]()
    }

    private void editPosition() {
        // Show dialog with pre-filled data, call service.update[Entity]()
    }

    private void deletePosition() {
        // Confirm and call service.delete[Entity]()
    }
    
}
