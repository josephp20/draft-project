import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileLoader {

    // Read my file
    public static List<String> readFile(String filePath) {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }

        } catch (IOException e) {
            System.out.println("Error in read your File: " + e.getMessage());
        }

        return lines;
    }

    //add Data
    public static void addingData(String filePath, String record) {
        try (FileWriter writer = new FileWriter(filePath, true)) {
            writer.write(record + System.lineSeparator());
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    // Update the File
    public static void updateData(String filePath, List<String> records) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            for (String record : records) {
                writer.write(record + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error al actualizar el archivo: " + e.getMessage());
        }
    }


}

