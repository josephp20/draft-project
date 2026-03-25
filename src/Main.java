import java.io.File;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        //*******IMPORTANT******
        //try use the file route inside the project for prevent any problem with restricted access
        // Testing file data route (Jose Computer)
        // C:\Users\jose2\Desktop\Software1\sdlc\src\data.txt


        //Call my class FileLoader- and starting read the file
        // Create scanner
        Scanner scanner = new Scanner(System.in);

        System.out.print("Type the route of your file: ");
        String filePath = scanner.nextLine();

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Error: File path is incorrect. Program will close.");
            scanner.close();
            return;
        }

        List<String> taskData = FileLoader.readFile(filePath);

        if (taskData.isEmpty()) {
            System.out.println("File loaded but empty.");
        } else {
            System.out.println("File loaded successfully.\n");
        }

        TaskService taskService = new TaskService();
        int option = 0;

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
                scanner.nextLine();
            } else {
                System.out.println("Type a valid number.\n");
                scanner.nextLine();
                continue;
            }

            switch (option) {
                case 1:
                    System.out.print("Enter task title: ");
                    String title = scanner.nextLine();

                    System.out.print("Enter task description: ");
                    String description = scanner.nextLine();

                    //------------------------------------------------

                    String creationDate;

                    // VALIDATE creation date
                    while (true) {
                        System.out.print("Enter creation date (MM-DD-YYYY): ");
                        creationDate = scanner.nextLine();

                        if (isValidDate(creationDate)) {
                            break;
                        }

                        System.out.println("Invalid date format. Use MM-DD-YYYY and only numbers.");
                    }

                    String dueDate;

                    // VALIDATE due date
                    while (true) {
                        System.out.print("Enter due date (MM-DD-YYYY): ");
                        dueDate = scanner.nextLine();

                        if (isValidDate(dueDate)) {
                            break;
                        }

                        System.out.println("Invalid date format. Use MM-DD-YYYY and only numbers.");
                    }
                    //------------------------------------------------



                    System.out.print("Enter priority (low / medium / high): ");
                    String priority = scanner.nextLine();

                    String addResult = taskService.addTask(
                            filePath, title, description, creationDate, dueDate, priority
                    );

                    System.out.println(addResult);
                    break;

                case 2:
                    System.out.print("Type the ID of the task to delete: ");
                    String idToRemove = scanner.nextLine();

                    boolean removed = taskService.removeTask(filePath, idToRemove);

                    if (removed) {
                        System.out.println("Task deleted.\n");
                    } else {
                        System.out.println("Task ID not found.\n");
                    }
                    break;

                case 3:
                    List<String> tasks = taskService.listTasks(filePath);

                    if (tasks.isEmpty()) {
                        System.out.println("No tasks available.\n");
                    } else {
                        System.out.println("\n=== All Tasks ===");
                        for (String task : tasks) {
                            System.out.println(task);
                        }
                        System.out.println();
                    }
                    break;

                case 4:
                    System.out.print("Type the ID of the task to update: ");
                    String idToUpdate = scanner.nextLine();

                    System.out.print("New title: ");
                    String newTitle = scanner.nextLine();

                    System.out.print("New description: ");
                    String newDescription = scanner.nextLine();

                    //---------------------------------------------------
                    //verify the valida date
                    String newCreationDate;

                    while (true) {
                        System.out.print("New creation date (MM-DD-YYYY): ");
                        newCreationDate = scanner.nextLine();

                        if (isValidDate(newCreationDate)) {
                            break;
                        }

                        System.out.println("Invalid date format. Use MM-DD-YYYY and only numbers.");
                    }

                    String newDueDate;

                    while (true) {
                        System.out.print("New due date (MM-DD-YYYY): ");
                        newDueDate = scanner.nextLine();

                        if (isValidDate(newDueDate)) {
                            break;
                        }

                        System.out.println("Invalid date format. Use MM-DD-YYYY and only numbers.");
                    }
                    //---------------------------------------------------

                    System.out.print("New priority (low / medium / high): ");
                    String newPriority = scanner.nextLine();

                    boolean updated = taskService.updateTask(
                            filePath,
                            idToUpdate,
                            newTitle,
                            newDescription,
                            newCreationDate,
                            newDueDate,
                            newPriority
                    );

                    if (updated) {
                        System.out.println("Task updated successfully.\n");
                    } else {
                        System.out.println("Task ID not found or invalid priority.\n");
                    }
                    break;

                case 5:
                    TaskReport report = taskService.generatePriorityReport(filePath);

                    if (report.getTotalTasks() == 0) {
                        System.out.println("No tasks available.\n");
                    } else {
                        System.out.println("\n===== TASK REPORT =====");
                        System.out.println("Total Tasks: " + report.getTotalTasks());
                        System.out.println("High Priority Tasks: " + report.getHighCount());
                        System.out.println("Medium Priority Tasks: " + report.getMediumCount());
                        System.out.println("Low Priority Tasks: " + report.getLowCount());
                        System.out.println();
                    }
                    break;

                case 6:
                    System.out.println("Exiting Task Manager...");
                    break;

                default:
                    System.out.println("Invalid option.\n");
            }
        }

        scanner.close();
    }


    public static boolean isValidDate(String date) {

        // format MM-DD-YYYY
        if (!date.matches("\\d{2}-\\d{2}-\\d{4}")) {
            return false;
        }

        String[] parts = date.split("-");
        int month = Integer.parseInt(parts[0]);
        int day = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        // validation
        if (month < 1 || month > 12) return false;
        if (day < 1 || day > 31) return false;
        if (year < 1900 || year > 2100) return false;

        return true;
    }

}