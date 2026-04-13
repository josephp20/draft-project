import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
/**
 * Service class to handle task operations (CRUD and reports).
 */
public class TaskService {

    // Database name
    private String databaseName;

    /**
     * Constructor
     * @param databaseName name of the database
     */
    public TaskService(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Adds a new task after validating input fields.
     * @return message indicating result
     */
    public String addTask(String title, String description,
                          String creationDate, String dueDate, String priority) {

        // Validate empty fields
        if (isEmpty(title) || isEmpty(description) || isEmpty(creationDate)
                || isEmpty(dueDate) || isEmpty(priority)) {
            return "All fields are required.";
        }

        // Validate priority (low, medium, high)
        if (!isValidPriority(priority)) {
            return "Invalid priority.";
        }

        String sql = "INSERT INTO task (title, description, creation_date, due_date, priority) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set values
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(creationDate));
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setString(5, priority.toLowerCase());

            int rows = ps.executeUpdate();

            return rows > 0 ? "Task added successfully." : "Task could not be added.";

        } catch (IllegalArgumentException e) {
            return "Invalid date format. Use yyyy-mm-dd.";
        } catch (Exception e) {
            return "Error adding task: " + e.getMessage();
        }
    }

    /**
     * Deletes a task by ID.
     * @param idToRemove task ID
     * @return true if deleted successfully
     */
    public boolean removeTask(String idToRemove) {

        if (isEmpty(idToRemove)) return false;

        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(idToRemove));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }

    /**
     * @return list of task records as strings
     */
    public List<String> listTasks() {
        List<String> tasks = new ArrayList<>();

        String sql = "SELECT * FROM task ORDER BY id";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            // Build task string
            while (rs.next()) {
                String record = rs.getInt("id") + " - " +
                        rs.getString("title") + " - " +
                        rs.getString("description") + " - " +
                        rs.getDate("creation_date") + " - " +
                        rs.getDate("due_date") + " - " +
                        rs.getString("priority");

                tasks.add(record);
            }

        } catch (Exception e) {
            System.out.println("Error listing tasks: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Updates an existing task.
     * @return true if update was successful
     */
    public boolean updateTask(String idToUpdate, String newTitle,
                              String newDescription, String newCreationDate,
                              String newDueDate, String newPriority) {

        // Validate input
        if (isEmpty(idToUpdate) || isEmpty(newTitle) || isEmpty(newDescription)
                || isEmpty(newCreationDate) || isEmpty(newDueDate) || isEmpty(newPriority)) {
            return false;
        }

        if (!isValidPriority(newPriority)) return false;

        String sql = "UPDATE task SET title=?, description=?, creation_date=?, due_date=?, priority=? WHERE id=?";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set updated values
            ps.setString(1, newTitle);
            ps.setString(2, newDescription);
            ps.setDate(3, Date.valueOf(newCreationDate));
            ps.setDate(4, Date.valueOf(newDueDate));
            ps.setString(5, newPriority.toLowerCase());
            ps.setInt(6, Integer.parseInt(idToUpdate));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    /**
     * Generates a report of tasks grouped by priority.
     * @return TaskReport object
     */
    public TaskReport generatePriorityReport() {

        int totalTasks = 0, low = 0, medium = 0, high = 0;

        String sql = "SELECT priority, COUNT(*) AS total FROM task GROUP BY priority";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String p = rs.getString("priority").toLowerCase();
                int count = rs.getInt("total");

                totalTasks += count;

                // Assign counts by priority
                if (p.equals("low")) low = count;
                else if (p.equals("medium")) medium = count;
                else if (p.equals("high")) high = count;
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }

        return new TaskReport(totalTasks, low, medium, high);
    }

    /**
     * Validates priority value.
     */
    private boolean isValidPriority(String priority) {
        if (priority == null) return false;
        String v = priority.toLowerCase();
        return v.equals("low") || v.equals("medium") || v.equals("high");
    }

    /**
     * Checks if a string is empty.
     */
    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}