package com.smarty.web;

import com.smarty.domain.task.model.TaskRequestDTO;
import com.smarty.domain.task.model.TaskResponseDTO;
import com.smarty.domain.task.model.TaskUpdateDTO;
import com.smarty.domain.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        return ResponseEntity.ok(taskService.createTask(taskRequestDTO));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(Pageable pageable) {
        return ResponseEntity.ok(taskService.getAllTasks(pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(taskService.getTasksByCourse(courseId));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        return ResponseEntity.ok(taskService.updateTask(id, taskUpdateDTO));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

}
