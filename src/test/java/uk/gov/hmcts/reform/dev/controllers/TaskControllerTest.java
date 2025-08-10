package uk.gov.hmcts.reform.dev.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.dev.dto.StatusUpdateDto;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.dev.models.Status.COMPLETED;
import static uk.gov.hmcts.reform.dev.models.Status.IN_PROGRESS;
import static uk.gov.hmcts.reform.dev.models.Status.PENDING;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private Task existing;
    private Task updatedData;

    @BeforeEach
    void setUp() {
        existing = new Task();
        existing.setId(42L);
        existing.setTitle("Old title");
        existing.setDescription("Old description");
        existing.setStatus(PENDING);
        existing.setDueDate(LocalDate.of(2025, 8, 15));

        updatedData = new Task();
        updatedData.setTitle("New title");
        updatedData.setDescription("New description");
        updatedData.setStatus(IN_PROGRESS);
        updatedData.setDueDate(LocalDate.of(2025, 8, 20));
    }

    @Test
    void testGetAllTasksReturnsOk() {
        when(taskService.getAllTasks()).thenReturn(List.of(existing, updatedData));

        ResponseEntity<List<Task>> response = taskController.getAllTasks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Old title", response.getBody().getFirst().getTitle());
        assertEquals("Old description", response.getBody().getFirst().getDescription());
        assertEquals(PENDING, response.getBody().getFirst().getStatus());
        assertEquals(LocalDate.of(2025, 8, 15), response.getBody().getFirst().getDueDate());
        assertEquals("New title", response.getBody().getLast().getTitle());
        assertEquals("New description", response.getBody().getLast().getDescription());
        assertEquals(IN_PROGRESS, response.getBody().getLast().getStatus());
        assertEquals(LocalDate.of(2025, 8, 20), response.getBody().getLast().getDueDate());
    }

    @Test
    void testGetTaskByIdReturnsOk() {
        when(taskService.getTaskById(42L)).thenReturn(existing);

        ResponseEntity<Task> response = taskController.getTask(42L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Old title", response.getBody().getTitle());
        assertEquals("Old description", response.getBody().getDescription());
        assertEquals(PENDING, response.getBody().getStatus());
        assertEquals(LocalDate.of(2025, 8, 15), response.getBody().getDueDate());
    }

    @Test
    void testCreateTaskReturnsOk() {
        Task newTask = new Task(123L, "New task", "New description", PENDING, LocalDate.of(2025, 10, 15));

        when(taskService.addTask(any(Task.class))).thenReturn(newTask);

        ResponseEntity<Task> response = taskController.addTask(newTask);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("New task", response.getBody().getTitle());
        assertEquals("New description", response.getBody().getDescription());
        assertEquals(PENDING, response.getBody().getStatus());
        assertEquals(LocalDate.of(2025, 10, 15), response.getBody().getDueDate());
    }

    @Test
    void testUpdateTaskReturnsOk() {
        when(taskService.updateTask(eq(42L), any(Task.class))).thenReturn(updatedData);

        ResponseEntity<Task> response = taskController.updateTask(42L, updatedData);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New title", response.getBody().getTitle());
        assertEquals("New description", response.getBody().getDescription());
        assertEquals(IN_PROGRESS, response.getBody().getStatus());
        assertEquals(LocalDate.of(2025, 8, 20), response.getBody().getDueDate());
    }

    @Test
    void testDeleteTaskReturnsNoContent() {
        new Task(123L, "New Task", "New Description", PENDING, LocalDate.now());
        Mockito.doNothing().when(taskService).deleteTaskById(123L);

        ResponseEntity<Void> response = taskController.deleteTask(123L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdateTaskStatusReturnsOk() {
        StatusUpdateDto status = new StatusUpdateDto(COMPLETED);
        updatedData.setStatus(COMPLETED);

        when(taskService.updateStatus(eq(42L), any(StatusUpdateDto.class))).thenReturn(updatedData);

        ResponseEntity<Task> response = taskController.updateTaskStatus(42L, status);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("New title", response.getBody().getTitle());
        assertEquals("New description", response.getBody().getDescription());
        assertEquals(COMPLETED, response.getBody().getStatus());
        assertEquals(LocalDate.of(2025, 8, 20), response.getBody().getDueDate());
    }

}

