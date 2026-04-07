import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {

    // Instance of the service to test
    private final TaskService taskService = new TaskService("tasks");

    // Helper method to create a temporary file with initial content
    private String createTestFile(String content) throws IOException {
        File tempFile = File.createTempFile("tasks_test", ".txt");
        FileWriter writer = new FileWriter(tempFile);
        writer.write(content);
        writer.close();
        return tempFile.getAbsolutePath();
    }



}