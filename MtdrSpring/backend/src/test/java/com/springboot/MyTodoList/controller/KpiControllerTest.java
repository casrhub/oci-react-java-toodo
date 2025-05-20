package com.springboot.MyTodoList.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Kpi;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(KpiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KpiControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @org.springframework.context.annotation.Configuration
  static class TestConfig {
    static final KpiController controller = new KpiController();

    @org.springframework.context.annotation.Bean
    public KpiController kpiController() {
      return controller;
    }

    public static KpiController getController() {
      return controller;
    }
  }

  @BeforeEach
  public void resetStore() {
    TestConfig.getController().getKpiStore().clear();
    TestConfig.getController().resetIdGenerator();
  }

  private Kpi buildKpi(Long usuarioId,
                       String nombreKpi,
                       String descripcion,
                       Double valorActual,
                       Double meta,
                       OffsetDateTime fechaRegistro) {
    Kpi kpi = new Kpi();
    kpi.setUsuarioId(usuarioId);
    kpi.setNombreKpi(nombreKpi);
    kpi.setDescripcion(descripcion);
    kpi.setValorActual(valorActual);
    kpi.setMeta(meta);
    kpi.setFechaRegistro(fechaRegistro);
    return kpi;
  }

  @Test
  public void testCreateAndGetKpi() throws Exception {
    Kpi kpi = buildKpi(
            1L,
            "Rate tiempo promedio / tiempo estimado",
            "Tiempo promedio por tarea entre tiempo máximo estimado (horas)",
            0.0,
            1.0,
            OffsetDateTime.parse("2024-04-03T14:25:00Z")
    );

    String payload = objectMapper.writeValueAsString(kpi);

    mockMvc.perform(post("/kpis/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.kpiId").exists())
            .andExpect(jsonPath("$.usuarioId").value(1));

    mockMvc.perform(get("/kpis/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.kpiId").value(1))
            .andExpect(jsonPath("$.nombreKpi")
                    .value("Rate tiempo promedio / tiempo estimado"));
  }

  @Test
  public void testUpdateKpi() throws Exception {
    Kpi initial = buildKpi(
            1L,
            "Rate tiempo promedio / tiempo estimado",
            "Tiempo promedio por tarea entre tiempo máximo estimado (horas)",
            0.0,
            1.0,
            OffsetDateTime.parse("2024-04-03T14:25:00Z")
    );

    String createPayload = objectMapper.writeValueAsString(initial);

    String response = mockMvc.perform(post("/kpis/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(createPayload))
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Kpi created = objectMapper.readValue(response, Kpi.class);
    Long id = created.getKpiId();

    created.setNombreKpi("Tiempo promedio / tiempo estimado");
    created.setValorActual(1.0);
    String updatePayload = objectMapper.writeValueAsString(created);

    mockMvc.perform(put("/kpis/" + id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatePayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombreKpi")
                    .value("Tiempo promedio / tiempo estimado"))
            .andExpect(jsonPath("$.valorActual").value(1.0));
  }

  @Test
  public void testGetAllAndFilterByUsuario() throws Exception {
    Kpi kpi1 = buildKpi(
            1L,
            "Tiempo promedio / tiempo estimado",
            "Tiempo promedio por tarea entre tiempo máximo estimado (horas)",
            1.0,
            1.0,
            OffsetDateTime.parse("2024-04-03T14:25:00Z")
    );
    Kpi kpi2 = buildKpi(
            2L,
            "Tareas antes / tareas asignadas",
            "Tareas completadas antes del deadline entre tareas totales asignadas",
            0.0,
            1.0,
            OffsetDateTime.parse("2024-04-03T14:58:00Z")
    );

    String p1 = objectMapper.writeValueAsString(kpi1);
    String p2 = objectMapper.writeValueAsString(kpi2);

    mockMvc.perform(post("/kpis/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(p1))
            .andExpect(status().isCreated());

    mockMvc.perform(post("/kpis/crear")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(p2))
            .andExpect(status().isCreated());

    mockMvc.perform(get("/kpis"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2));

    mockMvc.perform(get("/kpis").param("usuarioId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].usuarioId").value(1));
  }
}
