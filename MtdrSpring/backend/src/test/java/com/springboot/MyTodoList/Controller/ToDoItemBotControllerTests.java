package com.springboot.MyTodoList.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

public class ToDoItemBotControllerTests {
    @Mock
    private TareaService tareaService;

    @InjectMocks
    private ToDoItemBotController controller;

     @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTareas_ReturnsListOfTareas() {
        // Arrange
        Tarea tarea1 = new Tarea(1L, "Write tests", "Write unit tests for controller", "pendiente");

        Tarea tarea2 = new Tarea(1L, "Write tests", "Write unit tests for controller", "pendiente");

        List<Tarea> mockTareas = Arrays.asList(tarea1, tarea2);

        when(tareaService.findAll()).thenReturn(mockTareas);

        // Act
        List<Tarea> result = controller.getAllTareas();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Write unit tests for controller", result.get(0).getDescripcion());
        verify(tareaService, times(1)).findAll();
    }

}
