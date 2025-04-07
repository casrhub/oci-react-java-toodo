package com.springboot.MyTodoList.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Este test se encarga de comprobar el comportamiento del endpoint:
 * GET /tareas
 */
@SpringBootTest // Levanta el contexto completo de Spring Boot para que todo esté disponible
@AutoConfigureMockMvc // Configura automáticamente la herramienta MockMvc para simular peticiones HTTP sin servidor real
class TareaControllerTest {

    // Con esta inyección de dependencia (@Autowired), obtenemos una instancia de MockMvc
    // que nos servirá para realizar llamadas a los endpoints de forma simulada.
    @Autowired
    private MockMvc mockMvc;

    /**
     * Método de prueba que verifica que al llamar a GET /tareas, la respuesta
     * sea un estado HTTP 200 (OK). De manera predeterminada, el test no exige
     * nada más allá de esa verificación, pero podrías añadir más comprobaciones
     * si lo necesitas (por ejemplo, el formato de la respuesta, contenido JSON, etc.).
     */
    @Test
    void testGetAllTareas() throws Exception {
        // Ejecutamos una petición GET contra el endpoint /tareas, indicando
        // que esperamos (o enviamos) contenido JSON.
        mockMvc.perform(get("/tareas")
                .contentType(MediaType.APPLICATION_JSON))

                // Verificamos que la respuesta tenga el estado HTTP 200 (OK).
                .andExpect(status().isOk());
    }
}
