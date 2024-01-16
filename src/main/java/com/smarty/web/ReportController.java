package com.smarty.web;

import com.smarty.domain.report.model.ReportRequestDTO;
import com.smarty.domain.report.model.ReportResponseDTO;
import com.smarty.domain.report.model.ReportUpdateDTO;
import com.smarty.domain.report.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<ReportResponseDTO> createReport(@Valid @RequestBody ReportRequestDTO reportRequestDTO) {
        return ResponseEntity.ok(reportService.createReport(reportRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<ReportResponseDTO>> getAllReports(Pageable pageable) {
        return ResponseEntity.ok(reportService.getAllReports(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> getReportById(@PathVariable Long id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReportResponseDTO> updateReport(@PathVariable Long id, @Valid @RequestBody ReportUpdateDTO reportUpdateDTO) {
        return ResponseEntity.ok(reportService.updateReport(id, reportUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
    }

}
