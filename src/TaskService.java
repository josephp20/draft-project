import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    private final String databaseName;

    public TaskService(String databaseName) {
        this.databaseName = databaseName;
    }

    private Connection getConnection() throws SQLException {
        return DatabaseManager.getConnection(databaseName);
    }
    //validate the inputs

    public String addTask(String title, String description, String creationDate, String dueDate, String priority) {
        if (title == null || title.trim().isEmpty()) {
            return "Title cannot be empty.";
        }

        if (description == null || description.trim().isEmpty()) {
            return "Description cannot be empty.";
        }

        String sql = "INSERT INTO task (title, description, creation_date, due_date, priority) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, creationDate);
            ps.setString(4, dueDate);
            ps.setString(5, priority);

            ps.executeUpdate();
            return "Task added successfully.";

        } catch (SQLException e) {
            return "Add error: " + e.getMessage();
        }
    }

    public boolean updateTask(String id, String title, String description, String creationDate, String dueDate, String priority) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        String sql = """
                UPDATE task
                SET title = ?, description = ?, creation_date = ?, due_date = ?, priority = ?
                WHERE id = ?
                """;

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            //connection with the fields

            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, creationDate);
            ps.setString(4, dueDate);
            ps.setString(5, priority);
            ps.setInt(6, Integer.parseInt(id));

            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Update error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeTask(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        //delete by ID

        String sql = "DELETE FROM task WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, Integer.parseInt(id));
            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    public List<String> listTasks() {
        List<String> tasks = new ArrayList<>();

        String sql = "SELECT id, title, description, creation_date, due_date, priority FROM task";
        //list automatic when I run it
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                String task =
                        "ID: " + rs.getInt("id") +
                                " | Title: " + rs.getString("title") +
                                " | Description: " + rs.getString("description") +
                                " | Creation Date: " + rs.getDate("creation_date") +
                                " | Due Date: " + rs.getDate("due_date") +
                                " | Priority: " + rs.getString("priority");

                tasks.add(task);
            }

        } catch (SQLException e) {
            System.out.println("List error: " + e.getMessage());
        }

        return tasks;
    }

    public List<Object[]> listTasksForTable() {
        List<Object[]> tasks = new ArrayList<>();
        //list on the JTable
        String sql = "SELECT id, title, description, creation_date, due_date, priority FROM task";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDate("creation_date"),
                        rs.getDate("due_date"),
                        rs.getString("priority")
                };

                tasks.add(row);
            }

        } catch (SQLException e) {
            System.out.println("Table list error: " + e.getMessage());
        }

        return tasks;
    }

    public TaskReport generatePriorityReport() {
        int totalTasks = 0;
        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;

        //count each priority
        //execute the query

        String sql = "SELECT priority FROM task";

        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            //priority while count
            while (rs.next()) {
                totalTasks++;

                String priority = rs.getString("priority");

                if (priority.equalsIgnoreCase("low")) {
                    lowCount++;
                } else if (priority.equalsIgnoreCase("medium")) {
                    mediumCount++;
                } else if (priority.equalsIgnoreCase("high")) {
                    highCount++;
                }
            }

        } catch (SQLException e) {
            System.out.println("Report error: " + e.getMessage());
        }

        return new TaskReport(totalTasks, lowCount, mediumCount, highCount);
    }
}