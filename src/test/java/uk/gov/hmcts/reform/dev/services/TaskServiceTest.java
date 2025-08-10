package uk.gov.hmcts.reform.dev.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.dev.dto.StatusUpdateDto;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.dev.models.Status.COMPLETED;
import static uk.gov.hmcts.reform.dev.models.Status.IN_PROGRESS;
import static uk.gov.hmcts.reform.dev.models.Status.PENDING;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

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
    void testShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(existing));

        List<Task> result = taskService.getAllTasks();

        assertThat(result).containsExactly(existing);
        verify(taskRepository).findAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    void testShouldReturnTaskById() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(existing));

        Task result = taskService.getTaskById(42L);

        assertThat(result).isSameAs(existing);
        verify(taskRepository).findById(42L);
    }

    @Test
    void testShouldNotReturnNonExistentTask() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getTaskById(99L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("99");

        verify(taskRepository).findById(99L);
    }

    @Test
    void testShouldUpdateTask() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.updateTask(42L, updatedData);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertThat(saved.getId()).isEqualTo(42L);
        assertThat(saved.getTitle()).isEqualTo("New title");
        assertThat(saved.getDescription()).isEqualTo("New description");
        assertThat(saved.getStatus()).isEqualTo(IN_PROGRESS);
        assertThat(saved.getDueDate()).isEqualTo(LocalDate.of(2025, 8, 20));

        assertThat(result).isSameAs(saved);
        verify(taskRepository).findById(42L);
    }

    @Test
    void testShouldNotUpdateNonExistentTask() {
        when(taskRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.updateTask(123L, updatedData))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("123");

        verify(taskRepository).findById(123L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testShouldUpdateTaskStatus() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        StatusUpdateDto dto = mock(StatusUpdateDto.class);
        when(dto.status()).thenReturn(COMPLETED);

        Task result = taskService.updateStatus(42L, dto);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        Task saved = captor.getValue();

        assertThat(saved.getStatus()).isEqualTo(COMPLETED);
        assertThat(result).isSameAs(saved);
        verify(taskRepository).findById(42L);
    }

    @Test
    void testShouldNotUpdateStatusOfNonExistentTask() {
        when(taskRepository.findById(5L)).thenReturn(Optional.empty());
        StatusUpdateDto dto = mock(StatusUpdateDto.class);
        when(dto.status()).thenReturn(COMPLETED);

        assertThatThrownBy(() -> taskService.updateStatus(5L, dto))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("5");

        verify(taskRepository).findById(5L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    void testShouldDeleteTask() {
        when(taskRepository.findById(42L)).thenReturn(Optional.of(existing));

        taskService.deleteTaskById(42L);

        verify(taskRepository).findById(42L);
        verify(taskRepository).delete(existing);
    }

    @Test
    void testShouldNotDeleteNonExistentTask() {
        when(taskRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.deleteTaskById(77L))
            .isInstanceOf(TaskNotFoundException.class)
            .hasMessageContaining("77");

        verify(taskRepository).findById(77L);
        verify(taskRepository, never()).delete(any());
    }

    @Test
    void testShouldAddTask() {
        Task newTask = new Task();
        newTask.setTitle("New title 2");
        newTask.setDescription("New description 2");
        newTask.setStatus(PENDING);
        newTask.setDueDate(LocalDate.of(2025, 8, 20));
        when(taskRepository.save(newTask)).thenReturn(newTask);

        Task result = taskService.addTask(newTask);

        assertThat(result).isSameAs(newTask);
        verify(taskRepository).save(newTask);
    }
}

