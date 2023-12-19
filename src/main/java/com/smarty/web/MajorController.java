package com.smarty.web;

import com.smarty.domain.major.model.MajorRequestDTO;
import com.smarty.domain.major.model.MajorResponseDTO;
import com.smarty.domain.major.model.MajorUpdateDTO;
import com.smarty.domain.major.service.MajorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/majors")
public class MajorController {

    private final MajorService majorService;

    public MajorController(MajorService majorService) {
        this.majorService = majorService;
    }

    @PostMapping
    public ResponseEntity<MajorResponseDTO> createMajor(@Valid @RequestBody MajorRequestDTO majorRequestDTO) {
        return ResponseEntity.ok(majorService.createMajor(majorRequestDTO));
    }

    @GetMapping
    public ResponseEntity<Page<MajorResponseDTO>> getAllMajors(Pageable pageable) {
        return ResponseEntity.ok(majorService.getAllMajors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MajorResponseDTO> getMajorById(@PathVariable Long id) {
        return ResponseEntity.ok(majorService.getMajorById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MajorResponseDTO> updateMajor(@PathVariable Long id, @Valid @RequestBody MajorUpdateDTO majorUpdateDTO) {
        return ResponseEntity.ok(majorService.updateMajor(id, majorUpdateDTO));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMajor(@PathVariable Long id) {
        majorService.deleteMajor(id);
    }

}
