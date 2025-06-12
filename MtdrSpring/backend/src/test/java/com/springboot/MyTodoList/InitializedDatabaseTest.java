package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import oracle.jdbc.pool.OracleDataSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.oracle.OracleContainer;

public class InitializedDatabaseTest {

  static OracleContainer oracleContainer =
      new OracleContainer("gvenzl/oracle-free:23.6-slim-faststart")
          .withStartupTimeout(Duration.ofMinutes(5))
          .withUsername("testuser")
          .withPassword("testpwd")
          .withInitScript("springSightDB_script.sql")
          .withCreateContainerCmdModifier(cmd -> cmd.withPlatform("linux/amd64"));

  static OracleDataSource ds;

  @BeforeAll
  static void setUp() throws SQLException {
    System.setProperty("testcontainers.ryuk.disabled", "true");

    oracleContainer.start();

    ds = new OracleDataSource();
    ds.setURL(oracleContainer.getJdbcUrl());
    ds.setUser(oracleContainer.getUsername());
    ds.setPassword(oracleContainer.getPassword());
  }

  @Test
  void getUsuario() throws SQLException {
    try (Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM USUARIOS WHERE USUARIO_ID = 102")) {

      Assertions.assertTrue(rs.next(), "No se encontró el usuario con ID = 102");
      assertThat(rs.getString("NOMBRE")).isEqualTo("Cesar Alan Silva Ramos");
    }
  }

  @Test
  void getTareaById() throws SQLException {
    try (Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM TAREAS WHERE TAREA_ID = 268")) {

      Assertions.assertTrue(rs.next(), "No se encontró la tarea con ID = 268");
      Assertions.assertEquals("Data Model Validation", rs.getString("TITULO"));
    }
  }

  @Test
  void getTareasForUsuario() throws SQLException {
    try (Connection conn = ds.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM TAREAS WHERE USUARIO_ID = 102")) {

      int count = 0;
      while (rs.next()) {
        count++;
        Assertions.assertEquals(102, rs.getInt("USUARIO_ID"));
      }
      Assertions.assertTrue(count > 0, "No se encontraron tareas para el usuario 102");
    }
  }
}
