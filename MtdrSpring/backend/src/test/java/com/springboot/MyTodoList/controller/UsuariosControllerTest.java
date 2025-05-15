package com.springboot.MyTodoList.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.UsuarioService;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Pruebas unitarias para {@link UsuariosController} siguiendo el mismo
 * enfoque de aislamiento que en {@link KpiControllerTest} y
 * {@link SubTareaControllerTest}.  Se emplea un contexto MVC mínimo,
 * se deshabilitan filtros de seguridad y se fuerza el *ant_path_matcher*
 * para evitar problemas con patrones complejos.
 */
@WebMvcTest(
        controllers = UsuariosController.class,
        properties = "spring.mvc.pathmatch.matching-strategy=ant_path_matcher")
@AutoConfigureMockMvc(addFilters = false)
public class UsuariosControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private UsuarioService usuarioService;

  /**
   * Registramos manualmente el controlador para mantener un contexto liviano.
   */
  @Configuration
  static class TestConfig {
    static final UsuariosController controller = new UsuariosController();

    @Bean
    public UsuariosController usuariosController() {
      return controller;
    }
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(usuarioService);
  }

  // ---------------------------------------------------------------------
  // CREATE & GET BY ID
  // ---------------------------------------------------------------------
  @Test
  void testCreateAndGetUsuario() throws Exception {
    Usuarios saved = buildUsuario(1, "Diego Ivan Morales", "a01643382@tec.mx", "developer", 1);
    Mockito.when(usuarioService.save(Mockito.any(Usuarios.class))).thenReturn(saved);
    Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(saved));

    String payload = objectMapper.writeValueAsString(saved);

    // Crear
    mockMvc.perform(post("/usuarios").contentType(MediaType.APPLICATION_JSON).content(payload))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.usuario_id").value(1))
            .andExpect(jsonPath("$.nombre").doesNotExist());

    // Obtener
    mockMvc.perform(get("/usuarios/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuario_id").value(1))
            .andExpect(jsonPath("$.nombre").value("Diego Ivan Morales"))
            .andExpect(jsonPath("$.email").value("a01643382@tec.mx"))
            .andExpect(jsonPath("$.rol").value("developer"))
            .andExpect(jsonPath("$.equipo_id").value(1));
  }

  // ---------------------------------------------------------------------
  // UPDATE
  // ---------------------------------------------------------------------
  @Test
  void testUpdateUsuario() throws Exception {
    Usuarios existing = buildUsuario(1, "Diego Ivan Morales", "a01643382@tec.mx", "developer", 1);
    Usuarios updated  = buildUsuario(1, "Diego Ivan Morales Gallardo", "a01643382@tec.mx", "manager", 1);

    Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(existing));
    Mockito.when(usuarioService.save(Mockito.any(Usuarios.class))).thenReturn(updated);

    String json = objectMapper.writeValueAsString(updated);

    mockMvc.perform(put("/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.usuario_id").value(1))
            .andExpect(jsonPath("$.nombre").value("Diego Ivan Morales Gallardo"))
            .andExpect(jsonPath("$.rol").value("manager"));
  }

  // ---------------------------------------------------------------------
  // GET ALL & FILTERS
  // ---------------------------------------------------------------------
  @Test
  void testGetAllUsuariosAndByEquipo() throws Exception {
    Usuarios u1 = buildUsuario(1, "Diego", "d@tec.mx", "manager", 1);
    Usuarios u2 = buildUsuario(2, "Fernanda", "f@tec.mx", "developer", 1);
    Usuarios u3 = buildUsuario(3, "Cesar", "c@tec.mx", "manager", 2);

    Mockito.when(usuarioService.findAll()).thenReturn(Arrays.asList(u1, u2, u3));
    Mockito.when(usuarioService.findByEquipoId(1)).thenReturn(Arrays.asList(u1, u2));
    Mockito.when(usuarioService.findByRol("manager")).thenReturn(Arrays.asList(u1, u3));

    // Todos
    mockMvc.perform(get("/usuarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));

    // Por equipo
    mockMvc.perform(get("/usuarios/equipo/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].usuario_id").value(1))
            .andExpect(jsonPath("$[1].usuario_id").value(2));

    // Por rol
    mockMvc.perform(get("/usuarios/rol/manager"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[*].rol").value(org.hamcrest.Matchers.everyItem(org.hamcrest.Matchers.equalTo("manager"))));
  }

  // ---------------------------------------------------------------------
  // BAD REQUEST (manager con varios roles)
  // ---------------------------------------------------------------------
  @Test
  void testUpdateUsuario_ManagerMultiRole_BadRequest() throws Exception {
    Usuarios invalid = buildUsuario(null, "Diego", "d@tec.mx", "manager, developer", 1);
    String json = objectMapper.writeValueAsString(invalid);

    mockMvc.perform(put("/usuarios/1").contentType(MediaType.APPLICATION_JSON).content(json))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("El usuario con rol manager no puede tener otro rol"));
  }

  // ---------------------------------------------------------------------
  // Helpers
  // ---------------------------------------------------------------------
  private Usuarios buildUsuario(Integer id, String nombre, String email, String rol, Integer equipo) {
    Usuarios u = new Usuarios();
    u.setId(id);
    u.setNombre(nombre);
    u.setEmail(email);
    u.setRol(rol);
    u.setEquipoId(equipo);
    return u;
  }
}