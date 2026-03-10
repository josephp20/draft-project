import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {


        //*******IMPORTANT******
        //try use the file route inside the project for prevent any problem with restricted access
        // Testing file data route (Jose Computer)
        // C:\Users\jose2\Desktop\Software1\sdlc\src\data.txt


        //Call my class FileLoader- and starting read the file
        // Create scanner
        Scanner scanner = new Scanner(System.in);

        // file path
        System.out.print("Type the route of your file: ");
        String filePath = scanner.nextLine();

        // Load
        List<String> taskData = FileLoader.readFile(filePath);

        // Check if file
        if (taskData.isEmpty()) {
            System.out.println("File loaded but empty.");
        } else {
            System.out.println("File loaded successfully.\n");
        }

        int option = 0;

        // Bucle principal
        while (option != 6) {

            System.out.println("===== TASK MANAGER =====");
            System.out.println("1. Add new task");
            System.out.println("2. Remove task");
            System.out.println("3. List all tasks");
            System.out.println("4. Update task");
            System.out.println("5. Generate priority report");
            System.out.println("6. Exit");
            System.out.print("Select an option: ");

            if (scanner.hasNextInt()) {
                option = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer
            } else {
                System.out.println("Type a valid number.\n");
                scanner.nextLine();
                continue;
            }

            switch (option) {

                //  ADD TASK
                case 1:

                    // Generate random task ID
                    Random random = new Random();
                    int id = 1000000 + random.nextInt(9000000);

                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();

                    System.out.print("Enter creation date (YYYY-MM-DD): ");
                    String creationDate = scanner.nextLine();

                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dueDate = scanner.nextLine();

                    String priority;

                    // Validate
                    while (true) {
                        System.out.print("Enter priority (low / medium / high): ");
                        priority = scanner.nextLine().toLowerCase();

                        if (priority.equals("low") ||
                                priority.equals("medium") ||
                                priority.equals("high")) {
                            break;
                        }

                        System.out.println("Invalid priority.");
                    }

                    // Build the task
                    // guardarlo
                    String record = id + "-" + title + "-" + description + "-" +
                            creationDate + "-" + dueDate + "-" + priority;

                    FileLoader.addingData(filePath, record);

                    System.out.println("Task added successfully.\n");

                    break;

                //  REMOVE
                case 2:

                    List<String> currentData = FileLoader.readFile(filePath);

                    if (currentData.isEmpty()) {
                        System.out.println("No tasks to delete.\n");
                        break;
                    }

                    System.out.println("\n=== Task List ===");

                    // current tasks
                    for (String task : currentData) {
                        System.out.println(task);
                    }

                    System.out.print("\nType the ID of the task to delete: ");
                    String idToRemove = scanner.nextLine();

                    boolean found = false;
                    List<String> updatedData = new ArrayList<>();

                    // Keep all tasks
                    for (String task : currentData) {

                        String[] parts = task.split("-", 2);

                        if (parts[0].equals(idToRemove)) {
                            found = true;
                        } else {
                            updatedData.add(task);
                        }
                    }

                    if (!found) {
                        System.out.println("Task ID not found.\n");
                    } else {
                        FileLoader.updateData(filePath, updatedData);
                        System.out.println("Task deleted.\n");
                    }

                    break;

                //  LIST
                case 3:

                    List<String> currentTasks = FileLoader.readFile(filePath);

                    if (currentTasks.isEmpty()) {
                        System.out.println("No tasks available.\n");
                        break;
                    }

                    System.out.println("\n=== All Tasks ===");

                    // Imprimir cada tarea
                    for (String task : currentTasks) {
                        System.out.println(task);
                    }

                    break;

                //UPDATE TASK
                case 4:

                    List<String> tasks = FileLoader.readFile(filePath);

                    if (tasks.isEmpty()) {
                        System.out.println("No tasks available.\n");
                        break;
                    }

                    System.out.println("\n=== Task List ===");

                    for (String task : tasks) {
                        System.out.println(task);
                    }

                    System.out.print("\nType the ID of the task to update: ");
                    String idToUpdate = scanner.nextLine();

                    boolean taskFound = false;
                    List<String> newData = new ArrayList<>();

                    for (String task : tasks) {

                        String[] parts = task.split("-", 6);

                        if (parts[0].equals(idToUpdate)) {

                            taskFound = true;

                            System.out.print("New title: ");
                            String newTitle = scanner.nextLine();

                            System.out.print("New description: ");
                            String newDescription = scanner.nextLine();

                            System.out.print("New creation date (YYYY-MM-DD): ");
                            String newCreationDate = scanner.nextLine();

                            System.out.print("New due date (YYYY-MM-DD): ");
                            String newDueDate = scanner.nextLine();

                            String newPriority;

                            //  Validate
                            while (true) {

                                System.out.print("New priority (low / medium / high): ");
                                newPriority = scanner.nextLine().toLowerCase();

                                if (newPriority.equals("low") ||
                                        newPriority.equals("medium") ||
                                        newPriority.equals("high")) {
                                    break;
                                }

                                System.out.println("Invalid priority.");
                            }

                            // Reemplazar la tarea anterior
                            String updatedRecord = idToUpdate + "-" +
                                    newTitle + "-" +
                                    newDescription + "-" +
                                    newCreationDate + "-" +
                                    newDueDate + "-" +
                                    newPriority;

                            newData.add(updatedRecord);

                        } else {

                            newData.add(task);

                        }
                    }

                    if (!taskFound) {

                        System.out.println("Task ID not found.\n");

                    } else {

                        FileLoader.updateData(filePath, newData);
                        System.out.println("Task updated successfully.\n");

                    }

                    break;

                // GENERATE REPORT
                case 5:

                    List<String> reportTasks = FileLoader.readFile(filePath);

                    if (reportTasks.isEmpty()) {
                        System.out.println("No tasks available.\n");
                        break;
                    }

                    int totalTasks = 0;
                    int lowCount = 0;
                    int mediumCount = 0;
                    int highCount = 0;

                    // Contar tareas
                    for (String task : reportTasks) {

                        String[] parts = task.split("-");

                        if (parts.length > 0) {

                            totalTasks++;

                            // value in the record
                            String taskPriority = parts[parts.length - 1].trim().toLowerCase();

                            if (taskPriority.equals("low")) {
                                lowCount++;
                            }
                            else if (taskPriority.equals("medium")) {
                                mediumCount++;
                            }
                            else if (taskPriority.equals("high")) {
                                highCount++;
                            }
                        }
                    }

                    System.out.println("\n===== TASK REPORT =====");
                    System.out.println("Total Tasks: " + totalTasks);
                    System.out.println("High Priority Tasks: " + highCount);
                    System.out.println("Medium Priority Tasks: " + mediumCount);
                    System.out.println("Low Priority Tasks: " + lowCount);
                    System.out.println();

                    break;

                // OPTION 6: EXIT
                case 6:
                    System.out.println("Exiting Task Manager...");
                    break;

                default:
                    System.out.println("Invalid option.\n");
            }
        }

        // Close scanner at the end / Cerrar
        scanner.close();
    }
}