package com.smarty.web;

import com.smarty.domain.activity.model.ActivityRequestDTO;
import com.smarty.domain.activity.model.ActivityResponseDTO;
import com.smarty.domain.activity.model.ActivityUpdateDTO;
import com.smarty.domain.activity.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityRequestDTO activityRequestDTO) {
        return ResponseEntity.ok(activityService.createActivity(activityRequestDTO));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping
    public ResponseEntity<Page<ActivityResponseDTO>> getAllActivities(Pageable pageable) {
        return ResponseEntity.ok(activityService.getAllActivities(pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @PreAuthorize("hasAnyRole('STUDENT', 'ASSISTANT', 'PROFESSOR')")
    @GetMapping("/by-course-of-student/{studentId}")
    public ResponseEntity<List<ActivityResponseDTO>> getStudentActivitiesByCourse(@PathVariable Long studentId, @RequestParam String code) {
        return ResponseEntity.ok(activityService.getStudentActivitiesByCourse(studentId, code));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityUpdateDTO activityUpdateDTO) {
        return ResponseEntity.ok(activityService.updateActivity(id, activityUpdateDTO));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
    }

}
