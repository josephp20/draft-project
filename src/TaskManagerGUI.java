import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskManagerGUI extends JFrame {

    private TaskService taskService;

    private JTextField txtTitle;
    private JTextField txtDescription;

    private JSpinner creationDateSpinner;
    private JSpinner dueDateSpinner;

    private JRadioButton rbLow, rbMedium, rbHigh;
    private ButtonGroup priorityGroup;

    private JTable taskTable;
    private DefaultTableModel tableModel;

    private JButton btnAdd, btnUpdate, btnDelete, btnList, btnReport, btnClear;

    public TaskManagerGUI() {
        initializeDatabaseConnection();
        initializeUI();
        listTasks();
    }

    //database Connection with restriction

    private void initializeDatabaseConnection() {
        String databaseName;

        while (true) {
            databaseName = JOptionPane.showInputDialog(
                    this,
                    "Enter database name:",
                    "Database Connection",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (databaseName == null) {
                JOptionPane.showMessageDialog(this, "Program closed.");
                System.exit(0);
            }

            databaseName = databaseName.trim();

            if (databaseName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Database name cannot be empty.");
                continue;
            }

            if (DatabaseManager.databaseExists(databaseName)) {
                DatabaseManager.initializeDatabase(databaseName);
                taskService = new TaskService(databaseName);

                JOptionPane.showMessageDialog(this, "Database connected successfully.");
                break;
            } else {
                JOptionPane.showMessageDialog(this, "Database does not exist or connection failed.");
            }
        }
    }

    private void initializeUI() {
        setTitle("Task Manager GUI - MySQL");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 8, 8));

        formPanel.add(new JLabel("Title:"));
        txtTitle = new JTextField();
        formPanel.add(txtTitle);

        formPanel.add(new JLabel("Description:"));
        txtDescription = new JTextField();
        formPanel.add(txtDescription);

        formPanel.add(new JLabel("Creation Date (MM-dd-yyyy):"));
        creationDateSpinner = createDateSpinner();
        formPanel.add(creationDateSpinner);

        formPanel.add(new JLabel("Due Date (MM-dd-yyyy):"));
        dueDateSpinner = createDateSpinner();
        formPanel.add(dueDateSpinner);

        add(formPanel, BorderLayout.NORTH);

        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        priorityPanel.setBorder(BorderFactory.createTitledBorder("Priority"));

        rbLow = new JRadioButton("low");
        rbMedium = new JRadioButton("medium");
        rbHigh = new JRadioButton("high");

        priorityGroup = new ButtonGroup();
        priorityGroup.add(rbLow);
        priorityGroup.add(rbMedium);
        priorityGroup.add(rbHigh);

        rbLow.setSelected(true);

        priorityPanel.add(rbLow);
        priorityPanel.add(rbMedium);
        priorityPanel.add(rbHigh);

        add(priorityPanel, BorderLayout.WEST);

        String[] columns = {"ID", "Title", "Description", "Creation Date", "Due Date", "Priority"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        taskTable = new JTable(tableModel);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        taskTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && taskTable.getSelectedRow() != -1) {
                fillFieldsFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(taskTable);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));

        btnAdd = new JButton("Add Task");
        btnUpdate = new JButton("Update Selected Task");
        btnDelete = new JButton("Delete Selected Task");
        btnList = new JButton("List Tasks");
        btnReport = new JButton("Generate Report");
        btnClear = new JButton("Clear Fields");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnList);
        buttonPanel.add(btnReport);
        buttonPanel.add(btnClear);

        add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addTask());
        btnUpdate.addActionListener(e -> updateTask());
        btnDelete.addActionListener(e -> deleteTask());
        btnList.addActionListener(e -> listTasks());
        btnReport.addActionListener(e -> generateReport());
        btnClear.addActionListener(e -> clearFields());
    }

    private JSpinner createDateSpinner() {
        SpinnerDateModel model = new SpinnerDateModel();
        JSpinner spinner = new JSpinner(model);
        JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "MM-dd-yyyy");
        spinner.setEditor(editor);
        spinner.setValue(new Date());
        return spinner;
    }

    private void fillFieldsFromSelectedRow() {
        int row = taskTable.getSelectedRow();

        txtTitle.setText(tableModel.getValueAt(row, 1).toString());
        txtDescription.setText(tableModel.getValueAt(row, 2).toString());

        String priority = tableModel.getValueAt(row, 5).toString();

        if (priority.equalsIgnoreCase("low")) {
            rbLow.setSelected(true);
        } else if (priority.equalsIgnoreCase("medium")) {
            rbMedium.setSelected(true);
        } else {
            rbHigh.setSelected(true);
        }
    }

    private String getSelectedPriority() {
        if (rbLow.isSelected()) {
            return "low";
        } else if (rbMedium.isSelected()) {
            return "medium";
        } else {
            return "high";
        }
    }

    private String formatDateForDatabase(JSpinner spinner) {
        Date date = (Date) spinner.getValue();
        SimpleDateFormat dbFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dbFormat.format(date);
    }

    private void addTask() {
        String title = txtTitle.getText().trim();
        String description = txtDescription.getText().trim();
        String creationDate = formatDateForDatabase(creationDateSpinner);
        String dueDate = formatDateForDatabase(dueDateSpinner);
        String priority = getSelectedPriority();

        String result = taskService.addTask(title, description, creationDate, dueDate, priority);

        JOptionPane.showMessageDialog(this, result);

        if (result.equalsIgnoreCase("Task added successfully.")) {
            listTasks();
            clearFields();
        }
    }

    private void updateTask() {
        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task from the table first.");
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();

        String title = txtTitle.getText().trim();
        String description = txtDescription.getText().trim();
        String creationDate = formatDateForDatabase(creationDateSpinner);
        String dueDate = formatDateForDatabase(dueDateSpinner);
        String priority = getSelectedPriority();

        boolean updated = taskService.updateTask(id, title, description, creationDate, dueDate, priority);

        if (updated) {
            JOptionPane.showMessageDialog(this, "Task updated successfully.");
            listTasks();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Task not found or update failed.");
        }
    }

    private void deleteTask() {
        int selectedRow = taskTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a task from the table first.");
            return;
        }

        String id = tableModel.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete task ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            boolean removed = taskService.removeTask(id);

            if (removed) {
                JOptionPane.showMessageDialog(this, "Task removed successfully.");
                listTasks();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Task not found or delete failed.");
            }
        }
    }

    private void listTasks() {
        tableModel.setRowCount(0);

        List<Object[]> tasks = taskService.listTasksForTable();

        for (Object[] task : tasks) {
            tableModel.addRow(task);
        }

    }

    private void generateReport() {
        TaskReport report = taskService.generatePriorityReport();

        String message =
                "Report:\n\n" +
                        "Total tasks: " + report.getTotalTasks() + "\n" +
                        "Low priority: " + report.getLowCount() + "\n" +
                        "Medium priority: " + report.getMediumCount() + "\n" +
                        "High priority: " + report.getHighCount();

        JOptionPane.showMessageDialog(this, message);
    }


    //clear the fields

    private void clearFields() {
        txtTitle.setText("");
        txtDescription.setText("");
        creationDateSpinner.setValue(new Date());
        dueDateSpinner.setValue(new Date());
        rbLow.setSelected(true);
        taskTable.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManagerGUI gui = new TaskManagerGUI();
            gui.setVisible(true);
        });
    }
}