import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TaskManagerGUI extends JFrame {

    private JTextField filePathField;
    private JTextField titleField;
    private JTextField descriptionField;

    private JSpinner creationDateSpinner;
    private JSpinner dueDateSpinner;

    private JRadioButton lowRadio;
    private JRadioButton mediumRadio;
    private JRadioButton highRadio;
    private ButtonGroup priorityGroup;

    private JTextArea outputArea;

    private String filePath = "";
    private TaskService taskService;

    public TaskManagerGUI() {
        taskService = new TaskService();

        setTitle("Task Manager");
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel fileLabel = new JLabel("Selected File:");
        filePathField = new JTextField(40);
        filePathField.setEditable(false);

        JButton browseFileButton = new JButton("Browse File");
        browseFileButton.addActionListener(e -> browseFile());

        filePanel.add(fileLabel);
        filePanel.add(filePathField);
        filePanel.add(browseFileButton);

        topPanel.add(filePanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Task Form"));

        titleField = new JTextField();
        descriptionField = new JTextField();

        creationDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor creationEditor = new JSpinner.DateEditor(creationDateSpinner, "MM-dd-yyyy");
        creationDateSpinner.setEditor(creationEditor);

        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dueEditor = new JSpinner.DateEditor(dueDateSpinner, "MM-dd-yyyy");
        dueDateSpinner.setEditor(dueEditor);

        JPanel priorityPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lowRadio = new JRadioButton("low");
        mediumRadio = new JRadioButton("medium");
        highRadio = new JRadioButton("high");

        priorityGroup = new ButtonGroup();
        priorityGroup.add(lowRadio);
        priorityGroup.add(mediumRadio);
        priorityGroup.add(highRadio);

        lowRadio.setSelected(true);

        priorityPanel.add(lowRadio);
        priorityPanel.add(mediumRadio);
        priorityPanel.add(highRadio);

        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);

        formPanel.add(new JLabel("Description:"));
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Creation Date:"));
        formPanel.add(creationDateSpinner);

        formPanel.add(new JLabel("Due Date:"));
        formPanel.add(dueDateSpinner);

        formPanel.add(new JLabel("Priority:"));
        formPanel.add(priorityPanel);

        JButton clearButton = new JButton("Clear Fields");
        clearButton.addActionListener(e -> clearFields());
        clearButton.setPreferredSize(new Dimension(120, 25));



        JPanel buttonContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonContainer.add(clearButton);

        formPanel.add(new JLabel(""));
        formPanel.add(buttonContainer);

        centerPanel.add(formPanel);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Output"));

        centerPanel.add(scrollPane);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 10));

        JButton addButton = new JButton("Add Task");
        JButton removeButton = new JButton("Delete Task");
        JButton listButton = new JButton("List Tasks");
        JButton updateButton = new JButton("Update Task");
        JButton reportButton = new JButton("Generate Report");

        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        listButton.addActionListener(e -> listTasks());
        updateButton.addActionListener(e -> updateTask());
        reportButton.addActionListener(e -> generateReport());

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(listButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(reportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void browseFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            filePath = selectedFile.getAbsolutePath();
            filePathField.setText(filePath);
            outputArea.setText("File loaded successfully.\n");
        }
    }

    private boolean isFileLoaded() {
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a valid file first.");
            return false;
        }
        return true;
    }

    private String getSelectedPriority() {
        if (lowRadio.isSelected()) return "low";
        if (mediumRadio.isSelected()) return "medium";
        if (highRadio.isSelected()) return "high";
        return "";
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        return sdf.format(date);
    }

    private void addTask() {
        if (!isFileLoaded()) return;

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String creationDate = formatDate((Date) creationDateSpinner.getValue());
        String dueDate = formatDate((Date) dueDateSpinner.getValue());
        String priority = getSelectedPriority();

        if (title.isEmpty() || description.isEmpty() || priority.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        String result = taskService.addTask(filePath, title, description, creationDate, dueDate, priority);
        outputArea.setText(result + "\n");
        clearFields();
        listTasks();
    }

    private void removeTask() {
        if (!isFileLoaded()) return;

        String id = JOptionPane.showInputDialog(this, "Enter the Task ID to delete:");

        if (id == null) {
            return;
        }

        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task ID cannot be empty.");
            return;
        }

        boolean removed = taskService.removeTask(filePath, id);

        if (removed) {
            outputArea.setText("Task deleted successfully.\n");
        } else {
            outputArea.setText("Task ID not found.\n");
        }

        listTasks();
    }

    private void listTasks() {
        if (!isFileLoaded()) return;

        List<String> tasks = taskService.listTasks(filePath);

        if (tasks.isEmpty()) {
            outputArea.setText("No tasks available.\n");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL TASKS ===\n");

        for (String task : tasks) {
            sb.append(task).append("\n");
        }

        outputArea.setText(sb.toString());
    }

    private void updateTask() {
        if (!isFileLoaded()) return;

        String id = JOptionPane.showInputDialog(this, "Enter the Task ID to update:");

        if (id == null) {
            return;
        }

        id = id.trim();

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Task ID cannot be empty.");
            return;
        }

        String title = titleField.getText().trim();
        String description = descriptionField.getText().trim();
        String creationDate = formatDate((Date) creationDateSpinner.getValue());
        String dueDate = formatDate((Date) dueDateSpinner.getValue());
        String priority = getSelectedPriority();

        if (title.isEmpty() || description.isEmpty() || priority.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields before updating.");
            return;
        }

        boolean updated = taskService.updateTask(filePath, id, title, description, creationDate, dueDate, priority);

        if (updated) {
            outputArea.setText("Task updated successfully.\n");
        } else {
            outputArea.setText("Task ID not found or invalid priority.\n");
        }

        clearFields();
        listTasks();
    }

    private void generateReport() {
        if (!isFileLoaded()) return;

        TaskReport report = taskService.generatePriorityReport(filePath);

        StringBuilder sb = new StringBuilder();
        sb.append("===== TASK REPORT =====\n");
        sb.append("Total Tasks: ").append(report.getTotalTasks()).append("\n");
        sb.append("High Priority Tasks: ").append(report.getHighCount()).append("\n");
        sb.append("Medium Priority Tasks: ").append(report.getMediumCount()).append("\n");
        sb.append("Low Priority Tasks: ").append(report.getLowCount()).append("\n");

        outputArea.setText(sb.toString());
    }

    private void clearFields() {
        titleField.setText("");
        descriptionField.setText("");
        creationDateSpinner.setValue(new Date());
        dueDateSpinner.setValue(new Date());
        lowRadio.setSelected(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TaskManagerGUI().setVisible(true);
        });
    }
}