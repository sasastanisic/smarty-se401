package com.smarty.domain.professor.service;

import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.professor.entity.Professor;
import com.smarty.domain.professor.model.ProfessorRequestDTO;
import com.smarty.domain.professor.model.ProfessorResponseDTO;
import com.smarty.domain.professor.model.ProfessorUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProfessorService {

    ProfessorResponseDTO createProfessor(ProfessorRequestDTO professorRequestDTO);

    Page<ProfessorResponseDTO> getAllProfessors(Pageable pageable);

    ProfessorResponseDTO getProfessorById(Long id);

    Professor getById(Long id);

    void existsById(Long id);

    ProfessorResponseDTO updateProfessor(Long id, ProfessorUpdateDTO professorUpdateDTO);

    ProfessorResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);

    void deleteProfessor(Long id);

}
