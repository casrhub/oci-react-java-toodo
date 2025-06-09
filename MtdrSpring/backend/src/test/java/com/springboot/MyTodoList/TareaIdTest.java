package com.springboot.MyTodoList;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TareaByIdTest extends BaseOracleIntegrationTest {

    @Test
    void getTareaById() throws SQLException {
        try (Connection conn = ds.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM TAREAS WHERE TAREA_ID = 268")) {

            Assertions.assertTrue(rs.next(), "No se encontró la tarea con ID = 268");
            Assertions.assertEquals("Data Model Validation", rs.getString("TITULO"));
        }
    }
}