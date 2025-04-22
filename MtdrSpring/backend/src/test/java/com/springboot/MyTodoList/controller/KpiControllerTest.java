package com.springboot.MyTodoList.controller;

import org.springframework.boot.test.mock.mockito.MockBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Kpi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KpiController.class)
@AutoConfigureMockMvc(addFilters = false)
public class KpiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    public void testCreateAndGetKpi() throws Exception {
        Kpi kpi = new Kpi();
        kpi.setUsuarioId(1L);
        kpi.setNombreKpi("Rate tiempo promedio / tiempo estimado");
        kpi.setDescripcion("Tiempo promedio por tarea entre tiempo máximo estimado (horas)");
        kpi.setValorActual(0.0);
        kpi.setMeta(1.0);
        kpi.setFechaRegistro(OffsetDateTime.parse("2024-04-03T14:25:00Z"));

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
                .andExpect(jsonPath("$.nombreKpi").value("Rate tiempo promedio / tiempo estimado"));
    }

    @Test
    public void testUpdateKpi() throws Exception {
        Kpi kpi = new Kpi();
        kpi.setUsuarioId(1L);
        kpi.setNombreKpi("Rate tiempo promedio / tiempo estimado");
        kpi.setDescripcion("Tiempo promedio por tarea entre tiempo máximo estimado (horas)");
        kpi.setValorActual(0.0);
        kpi.setMeta(1.0);
        kpi.setFechaRegistro(OffsetDateTime.parse("2024-04-03T14:25:00Z"));

        String payload = objectMapper.writeValueAsString(kpi);

        String response = mockMvc.perform(post("/kpis/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Kpi createdKpi = objectMapper.readValue(response, Kpi.class);
        Long createdId = createdKpi.getKpiId();

        createdKpi.setNombreKpi("Tiempo promedio / tiempo estimado");
        createdKpi.setValorActual(1.0);

        String updatePayload = objectMapper.writeValueAsString(createdKpi);

        mockMvc.perform(put("/kpis/" + createdId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreKpi").value("Tiempo promedio / tiempo estimado"))
                .andExpect(jsonPath("$.valorActual").value(1.0));
    }

    @Test
    public void testGetAllAndFilterByUsuario() throws Exception {
        Kpi kpi1 = new Kpi();
        kpi1.setUsuarioId(1L);
        kpi1.setNombreKpi("Tiempo promedio / tiempo estimado");
        kpi1.setDescripcion("Tiempo promedio por tarea entre tiempo máximo estimado (horas)");
        kpi1.setValorActual(1.0);
        kpi1.setMeta(1.0);
        kpi1.setFechaRegistro(OffsetDateTime.parse("2024-04-03T14:25:00Z"));

        Kpi kpi2 = new Kpi();
        kpi2.setUsuarioId(2L);
        kpi2.setNombreKpi("Tareas antes / tareas asignadas");
        kpi2.setDescripcion("Tareas completadas antes del deadline entre tareas totales asignadas");
        kpi2.setValorActual(0.0);
        kpi2.setMeta(1.0);
        kpi2.setFechaRegistro(OffsetDateTime.parse("2024-04-03T14:58:00Z"));

        String payload1 = objectMapper.writeValueAsString(kpi1);
        String payload2 = objectMapper.writeValueAsString(kpi2);

        mockMvc.perform(post("/kpis/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload1))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/kpis/crear")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload2))
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
