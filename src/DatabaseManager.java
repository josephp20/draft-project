import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

public class DatabaseManager {
     //stablish connection variables
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    public static boolean databaseExists(String dbName) {
        String sql = "SHOW DATABASES LIKE '" + dbName + "'";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            return rs.next();

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            return false;
        }
    }
    //IMPORTANT
    public static Connection getConnection(String dbName) throws Exception {
        return DriverManager.getConnection(URL + dbName, USER, PASSWORD);
    }

    public static void initializeDatabase(String dbName) {
        String sql = """
                CREATE TABLE IF NOT EXISTS task (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    title VARCHAR(100) NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    creation_date DATE NOT NULL,
                    due_date DATE NOT NULL,
                    priority VARCHAR(20) NOT NULL
                )
                """;

        try (Connection conn = getConnection(dbName);
             Statement st = conn.createStatement()) {

            st.execute(sql);
            System.out.println("Table checked/created successfully.");

        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }
}