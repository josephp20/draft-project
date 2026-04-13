import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Utility class for connection and setup.
 */
public class DatabaseManager {

    /** Database URL (without DB name) */
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/";

    /** DB username */
    private static final String USER = "root";

    /** DB password */
    private static final String PASSWORD = "root";

    /**
     * Checks if a database exists.
     * @param dbName database name
     * @return true if exists, false otherwise
     */
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

    /**
     * Gets a connection to a database.
     * @param dbName database name
     * @return Connection object
     */
    public static Connection getConnection(String dbName) throws Exception {
        return DriverManager.getConnection(URL + dbName, USER, PASSWORD);
    }

    /**
     * Creates task table if not exists.
     * @param dbName database name
     */
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

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}