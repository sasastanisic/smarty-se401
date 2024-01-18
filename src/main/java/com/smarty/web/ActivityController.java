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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDTO> createActivity(@Valid @RequestBody ActivityRequestDTO activityRequestDTO) {
        return ResponseEntity.ok(activityService.createActivity(activityRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ActivityResponseDTO>> getAllActivities(Pageable pageable) {
        return ResponseEntity.ok(activityService.getAllActivities(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> getActivityById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getActivityById(id));
    }

    @GetMapping("/by-course-of-student/{studentId}")
    public ResponseEntity<List<ActivityResponseDTO>> getStudentActivitiesByCourse(@PathVariable Long studentId, @RequestParam String code) {
        return ResponseEntity.ok(activityService.getStudentActivitiesByCourse(studentId, code));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDTO> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityUpdateDTO activityUpdateDTO) {
        return ResponseEntity.ok(activityService.updateActivity(id, activityUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
    }

}
