package uk.gov.hmcts.reform.dev.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.dev.dto.StatusUpdateDto;
import uk.gov.hmcts.reform.dev.models.Task;
import uk.gov.hmcts.reform.dev.services.TaskService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of tasks")
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Get a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task found"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(
        @Parameter(description = "ID of the task to retrieve") @PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Add a new task")
    @ApiResponse(responseCode = "201", description = "Task successfully created")
    @ApiResponse(responseCode = "400", description = "Invalid data provided")
    @PostMapping
    public ResponseEntity<Task> addTask(
        @Parameter(description = "Task to be added") @Valid @RequestBody Task task) {
        Task savedTask = taskService.addTask(task);
        return ResponseEntity.status(CREATED).body(savedTask);
    }

    @Operation(summary = "Update an existing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task successfully updated"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
        @Parameter(description = "ID of the task to update") @PathVariable Long id,
        @Parameter(description = "Updated task") @Valid @RequestBody Task task) {
        Task updatedTask = taskService.updateTask(id, task);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Update the status of an existing task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Task status successfully updated"),
        @ApiResponse(responseCode = "404", description = "Task not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status provided")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<Task> updateTaskStatus(
        @Parameter(description = "ID of the task to update") @PathVariable Long id,
        @Valid @RequestBody StatusUpdateDto statusDto) {
        Task updatedTask = taskService.updateStatus(id, statusDto);
        return ResponseEntity.ok(updatedTask);
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Task successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
        @Parameter(description = "ID of the task to delete") @PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }
}
