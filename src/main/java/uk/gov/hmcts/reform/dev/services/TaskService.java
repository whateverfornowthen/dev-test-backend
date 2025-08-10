package uk.gov.hmcts.reform.dev.services;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.dev.dto.StatusUpdateDto;
import uk.gov.hmcts.reform.dev.exceptions.TaskNotFoundException;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.repositories.TaskRepository;

import java.util.List;

@AllArgsConstructor
@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public Task updateTask(Long id, Task updatedData) {
        return taskRepository.findById(id)
            .map(task -> {
                task.setTitle(updatedData.getTitle());
                task.setDescription(updatedData.getDescription());
                task.setStatus(updatedData.getStatus());
                task.setDueDate(updatedData.getDueDate());
                return taskRepository.save(task);
            })
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    @Transactional
    public Task updateStatus(Long id, StatusUpdateDto statusDto) {
        Task taskToUpdate = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));


        taskToUpdate.setStatus(statusDto.status());
        return taskRepository.save(taskToUpdate);
    }

    @Transactional
    public void deleteTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));


        taskRepository.delete(task);
    }

    @Transactional
    public Task addTask(Task task) {
        return taskRepository.save(task);
    }

}
