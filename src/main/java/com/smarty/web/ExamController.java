package com.smarty.web;

import com.smarty.domain.exam.model.ExamRequestDTO;
import com.smarty.domain.exam.model.ExamResponseDTO;
import com.smarty.domain.exam.model.ExamUpdateDTO;
import com.smarty.domain.exam.service.ExamService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @PostMapping
    public ResponseEntity<ExamResponseDTO> createExam(@Valid @RequestBody ExamRequestDTO examRequestDTO) {
        return ResponseEntity.ok(examService.createExam(examRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ExamResponseDTO>> getAllExams(Pageable pageable) {
        return ResponseEntity.ok(examService.getAllExams(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Long id) {
        return ResponseEntity.ok(examService.getExamById(id));
    }

    @GetMapping("/by-student/{studentId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamHistoryByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(examService.getExamHistoryByStudent(studentId));
    }

    @GetMapping("/by-course/{courseId}")
    public ResponseEntity<List<ExamResponseDTO>> getExamHistoryByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamHistoryByCourse(courseId));
    }

    @GetMapping("/by-student-passed/{studentId}")
    public ResponseEntity<List<ExamResponseDTO>> getPassedExamsByStudent(@PathVariable Long studentId, @RequestParam int year) {
        return ResponseEntity.ok(examService.getPassedExamsByStudent(studentId, year));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExamResponseDTO> updateExam(@PathVariable Long id, @Valid @RequestBody ExamUpdateDTO examUpdateDTO) {
        return ResponseEntity.ok(examService.updateExam(id, examUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
    }

}
