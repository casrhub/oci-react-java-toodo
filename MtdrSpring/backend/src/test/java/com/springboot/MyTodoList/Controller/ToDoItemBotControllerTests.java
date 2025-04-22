package com.springboot.MyTodoList.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.Optional;



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

    @Test
    public void testDoneCommand_SetsPendingTaskIdAndSessionState() {
        Long chatId = 5780178554L;
        Long taskId = 123L;
    
        // Mock message and update
        Message mockMessage = mock(Message.class);
        when(mockMessage.getText()).thenReturn("/done " + taskId);
        when(mockMessage.getChatId()).thenReturn(chatId);
        when(mockMessage.hasText()).thenReturn(true);
    
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
    
        // Create bot
        ToDoItemBotController bot = new ToDoItemBotController(
            "dummyToken", "TestBot", tareaService, null, null, null
        );
    
        // Trigger logic
        bot.onUpdateReceived(mockUpdate);
    
        // Assert state
        assertEquals(taskId, bot.getPendingTaskIdTwo().get(chatId));
        assertEquals("AWAITING_HORAS_REALES", bot.getSessionState().get(chatId));
    }
    


    @Test
    public void testSetDeadlineCommand_SetsStateCorrectly() {
        Long chatId = 555L;
    
        Message message = mock(Message.class);
        String command = BotCommands.SET_DEADLINE.getCommand();
        when(message.getText()).thenReturn(command + " " + 42);
        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);
    
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
    
        ToDoItemBotController bot = new ToDoItemBotController(
            "token", "BotName", tareaService, null, null, null
        );
    
        bot.onUpdateReceived(update);
    
        assertEquals(42, bot.getPendingTaskId().get(chatId));
        assertEquals("AWAITING_DATE", bot.getSessionState().get(chatId));
    }


    @Test
    public void testLinkCommand_CallsSaveWithCorrectChatId() {
        Long chatId = 5780178554L;
        String email = "test@example.com";
    
        Usuarios user = new Usuarios();
        user.setEmail(email); // make sure this is not null
    
        UsuarioService usuarioService = mock(UsuarioService.class);
        when(usuarioService.findByEmail(email)).thenReturn(Optional.of(user));
    
        Message message = mock(Message.class);
        when(message.getText()).thenReturn("/link " + email);
        when(message.getChatId()).thenReturn(chatId);
        when(message.hasText()).thenReturn(true);
    
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
    
        ToDoItemBotController bot = new ToDoItemBotController(
            "dummyToken", "TestBot", null, null, usuarioService, null
        );
    
        bot.onUpdateReceived(update);
    
        ArgumentCaptor<Usuarios> captor = ArgumentCaptor.forClass(Usuarios.class);
        verify(usuarioService).save(captor.capture());
    
        assertEquals(chatId, captor.getValue().getTelegramChatId());
    }
    
    
    
    
    

    
    @Test
    public void testAssignSprintCommand_AssignsTaskToSprintAndUpdatesStatus() {
        Long chatId = 12345L;
        Long tareaId = 42L;
        Long sprintId = 99L;
        
        // Mock message and update
        Message mockMessage = mock(Message.class);
        when(mockMessage.getText()).thenReturn("/assignsprint " + tareaId);
        when(mockMessage.getChatId()).thenReturn(chatId);
        when(mockMessage.hasText()).thenReturn(true);
        
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true);
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
        
        // Mock services
        TareaService tareaService = mock(TareaService.class);
        SprintService sprintService = mock(SprintService.class);
        
        // Create bot with mocked services
        ToDoItemBotController bot = new ToDoItemBotController(
            "dummyToken", "TestBot", tareaService, sprintService, null, null
        );
        
        // Mock task and sprint
        Tarea mockTarea = new Tarea();
        mockTarea.setTareaId(tareaId);
        mockTarea.setEstado("pendiente");
        
        Sprint mockSprint = new Sprint();
        mockSprint.setSprintId(sprintId);
        
        when(tareaService.findById(tareaId)).thenReturn(Optional.of(mockTarea));
        when(sprintService.findById(sprintId)).thenReturn(Optional.of(mockSprint));
        when(tareaService.save(any(Tarea.class))).thenReturn(mockTarea);
        
        // First call - should set state to AWAITING_SPRINT_ID
        System.out.println("Before first update - Session state: " + bot.getSessionState());
        bot.onUpdateReceived(mockUpdate);
        System.out.println("After first update - Session state: " + bot.getSessionState());
        System.out.println("After first update - Pending task ID: " + bot.getPendingSprintTareaId());
        
        // Verify state was set correctly
        assertEquals("AWAITING_SPRINT_ID", bot.getSessionState().get(chatId));
        assertEquals(tareaId, bot.getPendingSprintTareaId().get(chatId));
        
        // Second call - should process sprint assignment
        Message secondMessage = mock(Message.class);
        when(secondMessage.getText()).thenReturn(sprintId.toString());
        when(secondMessage.getChatId()).thenReturn(chatId);
        when(secondMessage.hasText()).thenReturn(true);
        
        Update secondUpdate = mock(Update.class);
        when(secondUpdate.hasMessage()).thenReturn(true);
        when(secondUpdate.getMessage()).thenReturn(secondMessage);
        
        bot.onUpdateReceived(secondUpdate);
        
        // Verify task was updated correctly
        ArgumentCaptor<Tarea> tareaCaptor = ArgumentCaptor.forClass(Tarea.class);
        verify(tareaService).save(tareaCaptor.capture());
        
        Tarea savedTarea = tareaCaptor.getValue();
        assertEquals("en progreso", savedTarea.getEstado());
        assertEquals(mockSprint, savedTarea.getSprint());
        
        // Verify state was cleared
        assertNull(bot.getSessionState().get(chatId));
        assertNull(bot.getPendingSprintTareaId().get(chatId));
    }
}
