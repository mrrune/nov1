package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Random;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Hello and welcome!");

        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String password = System.getenv("DB_PASS");

//        url = "jdbc:mariadb://localhost:3306/testdb";
//        user = "root";
//        password = "root";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            logger.info("Connected to database");

            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS data_records (
                            -- id: Auto-incrementing primary key
                            id INT NOT NULL AUTO_INCREMENT,
                        
                            -- value: Simple integer field (using INT)
                            value INT NOT NULL,
                        
                            -- insertDate: Timestamp, automatically set to the current time upon insertion
                            insertDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        
                            -- Define 'id' as the primary key
                            PRIMARY KEY (id)
                        );
                        """);
            }

            addValueToTable(conn);

            try (PreparedStatement ps = conn.prepareStatement("SELECT value " +
                    "FROM data_records " +
                    "ORDER BY insertDate " +
                    "DESC LIMIT 10")) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int v = rs.getInt("value");
                    logger.info("Read value: {}", v);
                }
            }
        } catch (SQLException e) {
            logger.error("Database error", e);
            throw new RuntimeException(e);
        }
    }

    public static void addValueToTable(Connection conn) throws SQLException {
        final var INSERT_SQL = "INSERT INTO data_records (value) VALUES (?)";

        var random = new Random();
        // Generate a random number between min and max (inclusive)
        var randomValue = random.nextInt(999999);
        var pstmt = conn.prepareStatement(INSERT_SQL);
        pstmt.setInt(1, randomValue);
        int affectedRows = pstmt.executeUpdate();

        logger.info("The number of affected rows: {}", affectedRows);
    }
}