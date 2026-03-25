import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskService {

    public String addTask(String filePath, String title, String description,
                          String creationDate, String dueDate, String priority) {

        if (!isValidPriority(priority)) {
            return "Invalid priority.";
        }

        Random random = new Random();
        int id = 1000000 + random.nextInt(9000000);

        String record = id + "-" + title + "-" + description + "-" +
                creationDate + "-" + dueDate + "-" + priority.toLowerCase();

        FileLoader.addingData(filePath, record);
        return "Task added successfully.";
    }

    public boolean removeTask(String filePath, String idToRemove) {
        List<String> currentData = FileLoader.readFile(filePath);

        if (currentData.isEmpty()) {
            return false;
        }

        boolean found = false;
        List<String> updatedData = new ArrayList<>();

        for (String task : currentData) {
            String[] parts = task.split("-", 2);

            if (parts[0].equals(idToRemove)) {
                found = true;
            } else {
                updatedData.add(task);
            }
        }

        if (found) {
            FileLoader.updateData(filePath, updatedData);
        }

        return found;
    }

    public List<String> listTasks(String filePath) {
        return FileLoader.readFile(filePath);
    }

    public boolean updateTask(String filePath, String idToUpdate, String newTitle,
                              String newDescription, String newCreationDate,
                              String newDueDate, String newPriority) {

        if (!isValidPriority(newPriority)) {
            return false;
        }

        List<String> tasks = FileLoader.readFile(filePath);

        if (tasks.isEmpty()) {
            return false;
        }

        boolean taskFound = false;
        List<String> newData = new ArrayList<>();

        for (String task : tasks) {
            String[] parts = task.split("-", 6);

            if (parts[0].equals(idToUpdate)) {
                taskFound = true;

                String updatedRecord = idToUpdate + "-" +
                        newTitle + "-" +
                        newDescription + "-" +
                        newCreationDate + "-" +
                        newDueDate + "-" +
                        newPriority.toLowerCase();

                newData.add(updatedRecord);
            } else {
                newData.add(task);
            }
        }

        if (taskFound) {
            FileLoader.updateData(filePath, newData);
        }

        return taskFound;
    }

    public TaskReport generatePriorityReport(String filePath) {
        List<String> reportTasks = FileLoader.readFile(filePath);

        int totalTasks = 0;
        int lowCount = 0;
        int mediumCount = 0;
        int highCount = 0;

        for (String task : reportTasks) {
            String[] parts = task.split("-");

            if (parts.length > 0) {
                totalTasks++;

                String taskPriority = parts[parts.length - 1].trim().toLowerCase();

                if (taskPriority.equals("low")) {
                    lowCount++;
                } else if (taskPriority.equals("medium")) {
                    mediumCount++;
                } else if (taskPriority.equals("high")) {
                    highCount++;
                }
            }
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
}