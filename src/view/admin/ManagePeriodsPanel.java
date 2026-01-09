package view.admin;

import model.User;
import service.PeriodService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManagePeriodsPanel extends JPanel {

    private User currentUser;
    private PeriodService departmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManagePeriodsPanel(User user) {
        this.currentUser = user;
        this.departmentService = new PeriodService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Same pattern as ManageCompaniesPanel
    }

    private void loadData() {
        // Call service.getAll[Entities]()
        departmentService.getAllPeriods();
    }

    private void addPeriod() {
        // Show dialog, collect data, call service.add[Entity]()
    }

    private void editPeriod() {
        // Show dialog with pre-filled data, call service.update[Entity]()
    }

    private void deletePeriod() {
        // Confirm and call service.delete[Entity]()
    }
    
}
