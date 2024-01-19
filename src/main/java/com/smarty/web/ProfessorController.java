package com.smarty.web;

import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.professor.model.ProfessorRequestDTO;
import com.smarty.domain.professor.model.ProfessorResponseDTO;
import com.smarty.domain.professor.model.ProfessorUpdateDTO;
import com.smarty.domain.professor.service.ProfessorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/professors")
public class ProfessorController {

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> createProfessor(@Valid @RequestBody ProfessorRequestDTO professorRequestDTO) {
        return ResponseEntity.ok(professorService.createProfessor(professorRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ProfessorResponseDTO>> getAllProfessors(Pageable pageable) {
        return ResponseEntity.ok(professorService.getAllProfessors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> getProfessorById(@PathVariable Long id) {
        return ResponseEntity.ok(professorService.getProfessorById(id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ProfessorResponseDTO> getProfessorByEmail(@RequestParam String email) {
        return ResponseEntity.ok(professorService.getProfessorByEmail(email));
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<ProfessorResponseDTO>> getProfessorsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(professorService.getProfessorsByCourse(courseId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> updateProfessor(@PathVariable Long id, @Valid @RequestBody ProfessorUpdateDTO professorUpdateDTO) {
        return ResponseEntity.ok(professorService.updateProfessor(id, professorUpdateDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        return ResponseEntity.ok(professorService.updatePassword(id, passwordUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProfessor(@PathVariable Long id) {
        professorService.deleteProfessor(id);
    }

}
