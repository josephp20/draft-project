import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    // Instance of the service to test
    private final TaskService taskService = new TaskService();

    // Helper method to create a temporary file with initial content
    private String createTestFile(String content) throws IOException {
        File tempFile = File.createTempFile("tasks_test", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile.getAbsolutePath();
    }

    // ===== TEST ADD TASK =====
    @Test
    public void testAddTask() throws IOException {
        String filePath = createTestFile("");

        String result = taskService.addTask(
                filePath,
                "Test Task testing",
                "Test Description testing",
                "2026-03-25",
                "2026-03-30",
                "high"
        );

        List<String> tasks = taskService.listTasks(filePath);

        assertEquals("Task added successfully.", result);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).contains("Test Task"));
    }

    @Test
    public void testRemoveTask() throws IOException {
        String filePath = createTestFile(
                "1000001-Task One-Desc One-2026-03-20-2026-03-25-low\n" +
                        "1000002-Task Two-Desc Two-2026-03-21-2026-03-26-high\n"
        );

        boolean removed = taskService.removeTask(filePath, "1000001");
        List<String> tasks = taskService.listTasks(filePath);

        assertTrue(removed);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).startsWith("1000002"));
    }

    // ===== TEST UPDATE TASK =====
    @Test
    public void testUpdateTask() throws IOException {
        String filePath = createTestFile(
                "1000001-Old Title-Old Desc-2026-03-20-2026-03-25-low\n"
        );

        boolean updated = taskService.updateTask(
                filePath,
                "1000001",
                "New Title",
                "New Desc",
                "2026-03-22",
                "2026-03-28",
                "high"
        );

        List<String> tasks = taskService.listTasks(filePath);

        assertTrue(updated);
        assertEquals(1, tasks.size());
        assertTrue(tasks.get(0).contains("New Title"));
        assertTrue(tasks.get(0).endsWith("high"));
    }


    @Test
    public void testGeneratePriorityReportEmpty() throws IOException {
        String filePath = createTestFile("");

        TaskReport report = taskService.generatePriorityReport(filePath);

        assertEquals(0, report.getTotalTasks());
        assertEquals(0, report.getLowCount());
        assertEquals(0, report.getMediumCount());
        assertEquals(0, report.getHighCount());
    }


}