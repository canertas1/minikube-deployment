package com.example.definex.taskmanagement.controller;

import com.example.definex.taskmanagement.dto.request.CreateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskRequest;
import com.example.definex.taskmanagement.dto.request.UpdateTaskStateRequest;
import com.example.definex.taskmanagement.dto.response.CreatedTaskResponse;
import com.example.definex.taskmanagement.dto.response.TaskResponse;
import com.example.definex.taskmanagement.entities.Task;
import com.example.definex.taskmanagement.entities.TaskPriorityType;
import com.example.definex.taskmanagement.entities.TaskStateType;
import com.example.definex.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/{projectId}")
    public ResponseEntity<CreatedTaskResponse> createTask(@Valid @RequestBody CreateTaskRequest createTaskRequest,@PathVariable Long projectId) {
        return ResponseEntity.ok(taskService.save(createTaskRequest,projectId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }
    @PutMapping("/{id}/state")
    public ResponseEntity<TaskResponse> updateTaskState(
            @PathVariable Long id,
            @RequestBody UpdateTaskStateRequest updateTaskStateRequest){

        return ResponseEntity.ok(taskService.updateTaskState(updateTaskStateRequest,id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @RequestBody UpdateTaskRequest updateTaskRequest) {

        return ResponseEntity.ok(taskService.updateTask(id, updateTaskRequest));
    }
    @PatchMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskResponse> assignTaskToUser(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        return ResponseEntity.ok(taskService.assignTaskToTeamMember(taskId, userId));
    }
    @PutMapping("/{id}/priority")
    public ResponseEntity<TaskResponse> changeTaskPriority(
            @PathVariable Long id,
            @RequestParam TaskPriorityType priority) {
        return ResponseEntity.ok(taskService.changeTaskPriority(id, priority));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}