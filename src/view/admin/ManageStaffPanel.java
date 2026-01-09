package view.admin;

import model.User;
import service.StaffService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageStaffPanel extends JPanel {
    private User currentUser;
    private StaffService departmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageStaffPanel(User user) {
        this.currentUser = user;
        this.departmentService = new StaffService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Same pattern as ManageCompaniesPanel
    }

    private void loadData() {
        // Call service.getAll[Entities]()
        departmentService.getAllStaffs();
    }

    private void addStaff() {
        // Show dialog, collect data, call service.add[Entity]()
    }

    private void editStaff() {
        // Show dialog with pre-filled data, call service.update[Entity]()
    }

    private void deleteStaff() {
        // Confirm and call service.delete[Entity]()
    }
}
