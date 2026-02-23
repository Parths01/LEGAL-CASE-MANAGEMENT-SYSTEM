package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.TaskDtos.*;
import com.legal.casemanagement.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<TaskListItem>> getTasks(
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) Long assignedTo) {
        return ResponseEntity.ok(taskService.getTasks(caseId, assignedTo));
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody CreateTaskRequest request) {
        TaskResponse response = taskService.createTask(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to create task. Check required fields."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id,
            @RequestBody UpdateTaskStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Status is required"));
        }
        boolean updated = taskService.updateTaskStatus(id, request.getStatus());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SuccessResponse("Task status updated to " + request.getStatus()));
    }

    static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
