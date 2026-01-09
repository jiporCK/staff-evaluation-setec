package view.staff;

import model.User;
import service.EvaluationService;

import javax.swing.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;


public class ViewMyEvaluationsPanel extends JPanel {
    private User currentUser;
    private EvaluationService evaluationService;
    private JTable evaluationsTable;
    private DefaultTableModel tableModel;

    public ViewMyEvaluationsPanel(User user) {
        this.currentUser = user;
        this.evaluationService = new EvaluationService();
        initComponents();
        loadMyEvaluations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("My Evaluations");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"ID", "Period", "Assigned By", "Assign Date", "Average Score"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        evaluationsTable = new JTable(tableModel);
        add(new JScrollPane(evaluationsTable), BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadMyEvaluations());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMyEvaluations() {
        // Note: Need to get Staff ID from current user
        // Long staffId = getStaffIdFromUser(currentUser);
        // List<AssignStaffEvaluation> evaluations = evaluationService.getEvaluationsForStaff(staffId);

        tableModel.setRowCount(0);

        // Populate table with evaluations
        // Calculate and display average scores
    }
}
