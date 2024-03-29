package com.smarty.web;

import com.smarty.domain.engagement.model.EngagementRequestDTO;
import com.smarty.domain.engagement.model.EngagementResponseDTO;
import com.smarty.domain.engagement.model.EngagementUpdateDTO;
import com.smarty.domain.engagement.service.EngagementService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/engagements")
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EngagementResponseDTO> createEngagement(@Valid @RequestBody EngagementRequestDTO engagementRequestDTO) {
        return ResponseEntity.ok(engagementService.createEngagement(engagementRequestDTO));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR', 'ADMIN')")
    @GetMapping
    public ResponseEntity<Page<EngagementResponseDTO>> getAllEngagements(Pageable pageable) {
        return ResponseEntity.ok(engagementService.getAllEngagements(pageable));
    }

    @PreAuthorize("hasAnyRole('ASSISTANT', 'PROFESSOR', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<EngagementResponseDTO> getEngagementById(@PathVariable Long id) {
        return ResponseEntity.ok(engagementService.getEngagementById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EngagementResponseDTO> updateEngagement(@PathVariable Long id, @Valid @RequestBody EngagementUpdateDTO engagementUpdateDTO) {
        return ResponseEntity.ok(engagementService.updateEngagement(id, engagementUpdateDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEngagement(@PathVariable Long id) {
        engagementService.deleteEngagement(id);
    }

}
