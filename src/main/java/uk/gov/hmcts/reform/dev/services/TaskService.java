package uk.gov.hmcts.reform.dev.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.StatusUpdateDto;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        log.info("Fetching task with id: {}", id);
        return taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Task not found with id: {}", id);
                return new TaskNotFoundException("Task not found with id: " + id);
            });
    }

    @Transactional
    public Task updateTask(Long id, Task updatedData) {
        log.info("Updating task with id: {}", id);
        return taskRepository.findById(id)
            .map(task -> {
                task.setTitle(updatedData.getTitle());
                task.setDescription(updatedData.getDescription());
                task.setStatus(updatedData.getStatus());
                task.setDueDate(updatedData.getDueDate());
                Task savedTask = taskRepository.save(task);
                log.info("Successfully updated task with id: {}", id);
                return savedTask;
            })
            .orElseThrow(() -> {
                log.warn("Attempted to update task but not found with id: {}", id);
                return new TaskNotFoundException("Task not found with id: " + id);
            });
    }

    @Transactional
    public Task updateStatus(Long id, StatusUpdateDto statusDto) {
        log.info("Updating status of task with id: {} to {}", id, statusDto.status());
        Task taskToUpdate = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Attempted to update task status but not found with id: {}", id);
                return new TaskNotFoundException("Task not found with id: " + id);
            });

        taskToUpdate.setStatus(statusDto.status());
        Task updated = taskRepository.save(taskToUpdate);
        log.info("Successfully updated status of task with id: {}", id);
        return updated;
    }

    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> {
                log.warn("Attempted to delete task but not found with id: {}", id);
                return new TaskNotFoundException("Task not found with id: " + id);
            });

        log.info("Deleting task with id: {}", id);
        taskRepository.delete(task);
    }

    @Transactional
    public Task addTask(Task task) {
        log.info("Adding new task with title: {}", task.getTitle());
        return taskRepository.save(task);
    }

}
