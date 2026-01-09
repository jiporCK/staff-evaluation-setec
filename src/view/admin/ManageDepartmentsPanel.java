package view.admin;

import model.User;
import service.DepartmentService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ManageDepartmentsPanel extends JPanel {
    private User currentUser;
    private DepartmentService departmentService;
    private JTable table;
    private DefaultTableModel tableModel;

    public ManageDepartmentsPanel(User user) {
        this.currentUser = user;
        this.departmentService = new DepartmentService();
        initComponents();
        loadData();
    }

    private void initComponents() {
        // Same pattern as ManageCompaniesPanel
    }

    private void loadData() {
        // Call service.getAll[Entities]()
        departmentService.getAllDepartments();
    }

    private void addDepartment() {
        // Show dialog, collect data, call service.add[Entity]()
    }

    private void editDepartment() {
        // Show dialog with pre-filled data, call service.update[Entity]()
    }

    private void deleteDepartment() {
        // Confirm and call service.delete[Entity]()
    }

}
