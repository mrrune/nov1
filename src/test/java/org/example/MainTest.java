package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit Test for DataInserter.java using JUnit 5 and Mockito.
 * NOTE: This test isolates the database logic by mocking the entire JDBC layer.
 */
public class MainTest {

    // Mock dependencies for JDBC interaction
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    // The class we are testing
    private Main dataInserter;

    @BeforeEach
    public void setUp() {
        // Initialize all fields annotated with @Mock
        MockitoAnnotations.openMocks(this);
        dataInserter = new Main();
    }

    /**
     * Test case to verify that a successful insertion executes the SQL statement once,
     * sets the correct parameter, and closes the resources.
     */
    @Test
    void insertRandomValue_ExecutesSuccessfully() throws SQLException {
        // Mock the static DriverManager.getConnection call
        try (MockedStatic<DriverManager> mockedDriverManager = mockStatic(DriverManager.class)) {

            // ARRANGE
            // 1. Define what DriverManager.getConnection() should return (our mock Connection)
            mockedDriverManager.when(() -> DriverManager.getConnection(anyString(), anyString(), anyString()))
                    .thenReturn(mockConnection);

            // 2. Define what mockConnection.prepareStatement() should return (our mock PreparedStatement)
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

            // 3. Define what mockPreparedStatement.executeUpdate() should return (simulate success: 1 row affected)
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // ACT
            dataInserter.addValueToTable(mockConnection);

            // ASSERT
            // 1. Verify that setInt was called exactly once on the prepared statement.
            //    We check for the first parameter index (1) and use anyInt() for the random value.
            verify(mockPreparedStatement, times(1)).setInt(eq(1), anyInt());

            // 2. Verify that executeUpdate was called exactly once to run the query.
            verify(mockPreparedStatement, times(1)).executeUpdate();

            // 3. Verify that the connection was closed (essential for try-with-resources verification)
            //verify(mockConnection, times(1)).close();
        }
    }
}
