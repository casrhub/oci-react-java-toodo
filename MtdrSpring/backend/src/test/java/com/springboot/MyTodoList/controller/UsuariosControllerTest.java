package com.springboot.MyTodoList.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UsuariosController.class)
public class UsuariosControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private UsuarioService usuarioService;

  // para que el MyTodoListApplication no falle al intentar inyectar esos beans
  @MockBean private TareaService tareaService;
  @MockBean private SubTareaService subTareaService;
  @MockBean private SprintService sprintService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void createUserTest() throws Exception {
    Usuarios mockNuevoUsuarios = new Usuarios();
    mockNuevoUsuarios.setId(1);
    mockNuevoUsuarios.setNombre("Diego Ivan Morales");
    mockNuevoUsuarios.setEmail("a01643382@tec.mx");
    mockNuevoUsuarios.setRol("developer");
    mockNuevoUsuarios.setEquipoId(1);

    Mockito.when(usuarioService.save(any(Usuarios.class))).thenReturn(mockNuevoUsuarios);

    mockMvc
        .perform(
            post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockNuevoUsuarios)))
        .andExpect(status().isCreated()) // 201
        .andExpect(jsonPath("$.usuario_id").value(1))
        // esto es para validar que el usuario ID y el estatus 201 sea lo único que
        // devuelve:
        .andExpect(jsonPath("$.nombre").doesNotExist())
        .andExpect(jsonPath("$.email").doesNotExist())
        .andExpect(jsonPath("$.rol").doesNotExist())
        .andExpect(jsonPath("$.equipo_id").doesNotExist());
  }

  @Test
  public void getUserByIdTest() throws Exception {
    Usuarios mockExistentUsuario = new Usuarios();
    mockExistentUsuario.setId(1);
    mockExistentUsuario.setNombre("Diego Ivan Morales");
    mockExistentUsuario.setEmail("a01643382@tec.mx");
    mockExistentUsuario.setRol("developer");
    mockExistentUsuario.setEquipoId(1);

    Mockito.when(usuarioService.findById(1)).thenReturn(Optional.of(mockExistentUsuario));

    mockMvc
        .perform(get("/usuarios/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk()) // 200
        .andExpect(jsonPath("$.usuario_id").value(1))
        .andExpect(jsonPath("$.nombre").value("Diego Ivan Morales"))
        .andExpect(jsonPath("$.email").value("a01643382@tec.mx"))
        .andExpect(jsonPath("$.rol").value("developer"))
        .andExpect(jsonPath("$.equipo_id").value(1));
  }

  @Test
  public void updateUserTest() throws Exception {
    Usuarios existingUsuario = new Usuarios();
    existingUsuario.setId(1);
    existingUsuario.setNombre("Diego Ivan Morales");
    existingUsuario.setEmail("a01643382@tec.mx");
    existingUsuario.setRol("developer");
    existingUsuario.setEquipoId(1);

    Usuarios updatedUsuario = new Usuarios();
    updatedUsuario.setId(1); // puede que se omita en el json
    updatedUsuario.setNombre("Diego Ivan Morales Gallardo");
    updatedUsuario.setEmail("a01643382@tec.mx");
    updatedUsuario.setRol("manager");
    updatedUsuario.setEquipoId(1);

    Mockito.when(usuarioService.findById(1))
        .thenReturn(Optional.of(existingUsuario)); // MOCK find by ID

    Mockito.when(usuarioService.save(Mockito.any(Usuarios.class)))
        .thenReturn(updatedUsuario); // MOCK save

    mockMvc
        .perform(
            put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUsuario)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.usuario_id").value(1))
        .andExpect(jsonPath("$.nombre").value("Diego Ivan Morales Gallardo"))
        .andExpect(jsonPath("$.email").value("a01643382@tec.mx"))
        .andExpect(jsonPath("$.rol").value("manager"))
        .andExpect(jsonPath("$.equipo_id").value(1));
  }

  @Test
  public void getAllUsersTest() throws Exception {
    Usuarios usuario1 = new Usuarios();
    usuario1.setId(1);
    usuario1.setNombre("Diego Ivan Morales Gallardo");
    usuario1.setEmail("a01643382@tec.mx");
    usuario1.setRol("manager");
    usuario1.setEquipoId(1);

    Usuarios usuario2 = new Usuarios();
    usuario2.setId(2);
    usuario2.setNombre("Fernanda Diaz Gutierrez");
    usuario2.setEmail("a01639572@tec.mx");
    usuario2.setRol("developer");
    usuario2.setEquipoId(1);

    List<Usuarios> mockUserList = List.of(usuario1, usuario2);

    Mockito.when(usuarioService.findAll()).thenReturn(mockUserList);

    mockMvc
        .perform(get("/usuarios").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].usuario_id").value(1))
        .andExpect(jsonPath("$[0].nombre").value("Diego Ivan Morales Gallardo"))
        .andExpect(jsonPath("$[0].email").value("a01643382@tec.mx"))
        .andExpect(jsonPath("$[0].rol").value("manager"))
        .andExpect(jsonPath("$[0].equipo_id").value(1))
        .andExpect(jsonPath("$[1].usuario_id").value(2))
        .andExpect(jsonPath("$[1].nombre").value("Fernanda Diaz Gutierrez"))
        .andExpect(jsonPath("$[1].email").value("a01639572@tec.mx"))
        .andExpect(jsonPath("$[1].rol").value("developer"))
        .andExpect(jsonPath("$[1].equipo_id").value(1));
  }

  @Test
  public void getAllUsersByTeamIdTest() throws Exception {
    Usuarios usuario1 = new Usuarios();
    usuario1.setId(1);
    usuario1.setNombre("Diego Ivan Morales Gallardo");
    usuario1.setEmail("a01643382@tec.mx");
    usuario1.setRol("manager");
    usuario1.setEquipoId(1);

    Usuarios usuario2 = new Usuarios();
    usuario2.setId(2);
    usuario2.setNombre("Fernanda Diaz Gutierrez");
    usuario2.setEmail("a01639572@tec.mx");
    usuario2.setRol("developer");
    usuario2.setEquipoId(1);

    List<Usuarios> mockUsuarios = List.of(usuario1, usuario2);

    Mockito.when(usuarioService.findByEquipoId(1)).thenReturn(mockUsuarios);

    mockMvc
        .perform(get("/usuarios/equipo/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        // user 1
        .andExpect(jsonPath("$[0].usuario_id").value(1))
        .andExpect(jsonPath("$[0].nombre").value("Diego Ivan Morales Gallardo"))
        .andExpect(jsonPath("$[0].email").value("a01643382@tec.mx"))
        .andExpect(jsonPath("$[0].rol").value("manager"))
        .andExpect(jsonPath("$[0].equipo_id").value(1))
        // user 2
        .andExpect(jsonPath("$[1].usuario_id").value(2))
        .andExpect(jsonPath("$[1].nombre").value("Fernanda Diaz Gutierrez"))
        .andExpect(jsonPath("$[1].email").value("a01639572@tec.mx"))
        .andExpect(jsonPath("$[1].rol").value("developer"))
        .andExpect(jsonPath("$[1].equipo_id").value(1));
  }

  @Test
  public void getUsersByRoleTest() throws Exception {
    Usuarios manager1 = new Usuarios();
    manager1.setId(1);
    manager1.setNombre("Diego Ivan Morales Gallardo");
    manager1.setEmail("a01643382@tec.mx");
    manager1.setRol("manager");
    manager1.setEquipoId(1);

    Usuarios manager2 = new Usuarios();
    manager2.setId(2);
    manager2.setNombre("César Silva");
    manager2.setEmail("a01533452@tec.mx");
    manager2.setRol("manager");
    manager2.setEquipoId(2);

    Usuarios developer = new Usuarios();
    developer.setId(3);
    developer.setNombre("Fernanda Diaz Gutierrez");
    developer.setEmail("a01639572@tec.mx");
    developer.setRol("developer");
    developer.setEquipoId(1);

    List<Usuarios> soloManagers = List.of(manager1, manager2);

    Mockito.when(usuarioService.findByRol("manager")).thenReturn(soloManagers);

    mockMvc
        .perform(get("/usuarios/rol/manager").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].rol").value("manager"))
        .andExpect(jsonPath("$[1].rol").value("manager"))
        .andExpect(jsonPath("$[?(@.rol == 'developer')]").doesNotExist());
  }

  @Test
  public void updateUserManagerToUserRoleTest() throws Exception {
    Usuarios invalidUser = new Usuarios();
    invalidUser.setNombre("Diego Ivan Morales Gallardo");
    invalidUser.setEmail("a01643382@tec.mx");
    invalidUser.setRol("manager, developer");
    invalidUser.setEquipoId(1);

    mockMvc
        .perform(
            put("/usuarios/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
        .andExpect(status().isBadRequest()) // 400
        .andExpect(jsonPath("$.error").value("El usuario con rol manager no puede tener otro rol"));
  }
}
