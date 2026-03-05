import java.sql.*;

public class ResultService {

    // Defaults (can be overridden by env vars in Docker/Jenkins)
    private static final String DEFAULT_DB_NAME = "calc_data";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "Test12";

    static {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static String envOrDefault(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.isBlank()) ? def : v;
    }

    private static String getDatabaseHost() {
        return envOrDefault("DB_HOST", "db"); // docker-compose service name by default
    }

    private static String getDbName() {
        return envOrDefault("DB_NAME", DEFAULT_DB_NAME);
    }

    private static String getDbUser() {
        return envOrDefault("DB_USER", DEFAULT_DB_USER);
    }

    private static String getDbPassword() {
        return envOrDefault("DB_PASSWORD", DEFAULT_DB_PASSWORD);
    }

    private static String getServerUrlNoDb() {
        return "jdbc:mariadb://" + getDatabaseHost() + ":3306" +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static String getDatabaseUrl() {
        return "jdbc:mariadb://" + getDatabaseHost() + ":3306/" + getDbName() +
                "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    }

    private static void ensureDatabaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(getServerUrlNoDb(), getDbUser(), getDbPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + getDbName());
        }
    }

    private static void ensureTableExists(Connection conn) throws SQLException {
        String createTable = """
            CREATE TABLE IF NOT EXISTS calc_results (
                id INT AUTO_INCREMENT PRIMARY KEY,
                number1 DOUBLE NOT NULL,
                number2 DOUBLE NOT NULL,
                sum_result DOUBLE NOT NULL,
                product_result DOUBLE NOT NULL,
                subtract_result DOUBLE NOT NULL,
                division_result DOUBLE NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTable);
        }
    }

    public static void saveResult(double n1, double n2,
                                  double sum, double product,
                                  double subtract, Double division) {

        String dbUrl = getDatabaseUrl();

        try {
            ensureDatabaseExists();

            try (Connection conn = DriverManager.getConnection(dbUrl, getDbUser(), getDbPassword())) {
                ensureTableExists(conn);

                String insert = """
                    INSERT INTO calc_results
                    (number1, number2, sum_result, product_result, subtract_result, division_result)
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;

                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    ps.setDouble(1, n1);
                    ps.setDouble(2, n2);
                    ps.setDouble(3, sum);
                    ps.setDouble(4, product);
                    ps.setDouble(5, subtract);

                    if (division == null) ps.setNull(6, Types.DOUBLE);
                    else ps.setDouble(6, division);

                    ps.executeUpdate();
                }
            }

            System.out.println("✅ Result saved: " + n1 + ", " + n2 +
                    " → Sum=" + sum +
                    ", Product=" + product +
                    ", Subtract=" + subtract +
                    ", Division=" + (division == null ? "NULL" : division));

        } catch (SQLException e) {
            System.err.println("❌ Failed to save result to DB: " + dbUrl);
            e.printStackTrace();
        }
    }
}