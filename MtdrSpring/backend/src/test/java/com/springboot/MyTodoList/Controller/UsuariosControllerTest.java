package com.springboot.MyTodoList.Controller;

import com.springboot.MyTodoList.controller.UsuariosController;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuariosController.class)
public class UsuariosControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    // para que el MyTodoListApplication no falle al intentar inyectar esos beans
    @MockBean
    private TareaService tareaService;
    @MockBean
    private SubTareaService subTareaService;
    @MockBean
    private SprintService sprintService;


    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createUserTest() throws Exception {
        Usuarios mockNuevoUsuarios = new Usuarios();
        mockNuevoUsuarios.setId(1);
        mockNuevoUsuarios.setNombre("Diego Ivan Morales");
        mockNuevoUsuarios.setEmail("a01643382@tec.mx");
        mockNuevoUsuarios.setRol("developer");
        mockNuevoUsuarios.setEquipoId(1);

        Mockito.when(usuarioService.save(any(Usuarios.class))).thenReturn(mockNuevoUsuarios);

        mockMvc.perform(post("/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockNuevoUsuarios)))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.usuario_id").value(1))
                // esto es para validar que el usuario ID y el estatus 201 sea lo único que devuelve:
                .andExpect(jsonPath("$.nombre").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.rol").doesNotExist())
                .andExpect(jsonPath("$.equipo_id").doesNotExist());
    }
}
