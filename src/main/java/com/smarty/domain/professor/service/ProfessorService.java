package com.smarty.domain.professor.service;

import com.smarty.domain.professor.model.ProfessorRequestDTO;
import com.smarty.domain.professor.model.ProfessorResponseDTO;
import com.smarty.domain.professor.model.ProfessorUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfessorService {

    ProfessorResponseDTO createProfessor(ProfessorRequestDTO professorRequestDTO);

    Page<ProfessorResponseDTO> getAllProfessors(Pageable pageable);

    ProfessorResponseDTO getProfessorById(Long id);

    ProfessorResponseDTO updateProfessor(Long id, ProfessorUpdateDTO professorUpdateDTO);

    void deleteProfessor(Long id);

}
