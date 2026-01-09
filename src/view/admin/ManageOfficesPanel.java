package view.admin;

import model.User;
import service.OfficeService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageOfficesPanel extends JPanel {
    private User currentUser;
    private OfficeService departmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageOfficesPanel(User user) {
        this.currentUser = user;
        this.departmentService = new OfficeService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Same pattern as ManageCompaniesPanel
    }

    private void loadData() {
        // Call service.getAll[Entities]()
        departmentService.getAllOffices();
    }

    private void addOffice() {
        // Show dialog, collect data, call service.add[Entity]()
    }

    private void editOffice() {
        // Show dialog with pre-filled data, call service.update[Entity]()
    }

    private void deleteOffice() {
        // Confirm and call service.delete[Entity]()
    }
}
