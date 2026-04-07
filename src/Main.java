import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        // call my GUI
        SwingUtilities.invokeLater(() -> {
            TaskManagerGUI gui = new TaskManagerGUI();
            gui.setVisible(true);
        });

    }
}