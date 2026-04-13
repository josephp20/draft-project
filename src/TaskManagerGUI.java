import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * GUI application for managing tasks using Swing.
 * Allows user to add, update, delete, list tasks and generate reports.
 */
public class TaskManagerGUI extends JFrame {

    // Service to handle database operations
    private TaskService taskService;

    // Input fields
    private JTextField txtTitle;
    private JTextField txtDescription;

    // Date selectors
    private JSpinner creationDateSpinner;
    private JSpinner dueDateSpinner;

    // Priority options
    private JRadioButton rbLow, rbMedium, rbHigh;
    private ButtonGroup priorityGroup;

    // Output area
    private JTextArea textArea;

    // Buttons
    private JButton btnAdd, btnUpdate, btnDelete, btnList, btnReport, btnClear;

    /**
     * Constructor - initializes DB and UI
     */
    public TaskManagerGUI() {
        initializeDatabaseConnection();
        initializeUI();
    }

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
                JOptionPane.showMessageDialog(
                        this,
                        "Program closed.",
                        "Exit",
                        JOptionPane.INFORMATION_MESSAGE
                );
                System.exit(0);
            }

            databaseName = databaseName.trim();

            if (databaseName.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Database name cannot be empty.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                continue;
            }
            //call init database
            if (DatabaseManager.databaseExists(databaseName)) {
                DatabaseManager.initializeDatabase(databaseName);
                taskService = new TaskService(databaseName);

                JOptionPane.showMessageDialog(
                        this,
                        "Database connected successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );
                break;
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Database does not exist or connection failed.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    private void initializeUI() {
        setTitle("Task Manager GUI - MySQL");
        setSize(800, 600);
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

        textArea = new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 8, 8));

        btnAdd = new JButton("Add Task");
        btnUpdate = new JButton("Update Task");
        btnDelete = new JButton("Delete Task");
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
        String id = JOptionPane.showInputDialog(
                this,
                "Enter Task ID to update:",
                "Update Task",
                JOptionPane.QUESTION_MESSAGE
        );

        if (id == null) {
            return;
        }

        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task ID is required.");
            return;
        }

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
        String id = JOptionPane.showInputDialog(
                this,
                "Enter Task ID to delete:",
                "Delete Task",
                JOptionPane.QUESTION_MESSAGE
        );

        if (id == null) {
            return;
        }

        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task ID is required.");
            return;
        }

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
        List<String> tasks = taskService.listTasks();
        textArea.setText("");

        if (tasks.isEmpty()) {
            textArea.setText("No tasks found.");
            return;
        }

        textArea.append("Task List: \n\n");
        for (String task : tasks) {
            textArea.append(task + "\n");
        }
    }

    private void generateReport() {
        TaskReport report = taskService.generatePriorityReport();

        StringBuilder sb = new StringBuilder();
        sb.append("Report: \n\n");
        sb.append("Total tasks: ").append(report.getTotalTasks()).append("\n");
        sb.append("Low priority: ").append(report.getLowCount()).append("\n");
        sb.append("Medium priority: ").append(report.getMediumCount()).append("\n");
        sb.append("High priority: ").append(report.getHighCount()).append("\n");

        textArea.setText(sb.toString());
    }

    private void clearFields() {
        txtTitle.setText("");
        txtDescription.setText("");
        creationDateSpinner.setValue(new Date());
        dueDateSpinner.setValue(new Date());
        rbLow.setSelected(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManagerGUI gui = new TaskManagerGUI();
            gui.setVisible(true);
        });
    }
}