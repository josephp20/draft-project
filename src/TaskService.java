import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private String databaseName;

    public TaskService(String databaseName) {
        this.databaseName = databaseName;
    }

    public String addTask(String title, String description,
                          String creationDate, String dueDate, String priority) {

        if (isEmpty(title) || isEmpty(description) || isEmpty(creationDate)
                || isEmpty(dueDate) || isEmpty(priority)) {
            return "All fields are required.";
        }

        if (!isValidPriority(priority)) {
            return "Invalid priority.";
        }

        String sql = "INSERT INTO task (title, description, creation_date, due_date, priority) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setDate(3, Date.valueOf(creationDate));
            ps.setDate(4, Date.valueOf(dueDate));
            ps.setString(5, priority.toLowerCase());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                return "Task added successfully.";
            } else {
                return "Task could not be added.";
            }

        } catch (IllegalArgumentException e) {
            return "Invalid date format. Use yyyy-mm-dd.";
        } catch (Exception e) {
            return "Error adding task: " + e.getMessage();
        }
    }

    public boolean removeTask(String idToRemove) {
        if (isEmpty(idToRemove)) {
            return false;
        }

        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(idToRemove));

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error deleting task: " + e.getMessage());
            return false;
        }
    }

    public List<String> listTasks() {
        List<String> tasks = new ArrayList<>();

        String sql = "SELECT id, title, description, creation_date, due_date, priority FROM task ORDER BY id";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

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

    public boolean updateTask(String idToUpdate, String newTitle,
                              String newDescription, String newCreationDate,
                              String newDueDate, String newPriority) {

        if (isEmpty(idToUpdate) || isEmpty(newTitle) || isEmpty(newDescription)
                || isEmpty(newCreationDate) || isEmpty(newDueDate) || isEmpty(newPriority)) {
            return false;
        }

        if (!isValidPriority(newPriority)) {
            return false;
        }

        String sql = "UPDATE task SET title = ?, description = ?, creation_date = ?, due_date = ?, priority = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newTitle);
            ps.setString(2, newDescription);
            ps.setDate(3, Date.valueOf(newCreationDate));
            ps.setDate(4, Date.valueOf(newDueDate));
            ps.setString(5, newPriority.toLowerCase());
            ps.setInt(6, Integer.parseInt(idToUpdate));

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (Exception e) {
            System.out.println("Error updating task: " + e.getMessage());
            return false;
        }
    }

    public TaskReport generatePriorityReport() {
        int totalTasks = 0;
        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;

        String sql = "SELECT priority, COUNT(*) AS total FROM task GROUP BY priority";

        try (Connection conn = DatabaseManager.getConnection(databaseName);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String priority = rs.getString("priority").toLowerCase();
                int count = rs.getInt("total");

                totalTasks += count;

                if (priority.equals("low")) {
                    lowCount = count;
                } else if (priority.equals("medium")) {
                    mediumCount = count;
                } else if (priority.equals("high")) {
                    highCount = count;
                }
            }

        } catch (Exception e) {
            System.out.println("Error generating report: " + e.getMessage());
        }

        return new TaskReport(totalTasks, lowCount, mediumCount, highCount);
    }

    private boolean isValidPriority(String priority) {
        if (priority == null) {
            return false;
        }

        String value = priority.toLowerCase();
        return value.equals("low") || value.equals("medium") || value.equals("high");
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}