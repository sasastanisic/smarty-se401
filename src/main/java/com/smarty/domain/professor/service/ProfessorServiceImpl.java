package com.smarty.domain.professor.service;

import com.smarty.domain.account.enums.Role;
import com.smarty.domain.account.service.AccountService;
import com.smarty.domain.professor.entity.Professor;
import com.smarty.domain.professor.model.ProfessorRequestDTO;
import com.smarty.domain.professor.model.ProfessorResponseDTO;
import com.smarty.domain.professor.model.ProfessorUpdateDTO;
import com.smarty.domain.professor.repository.ProfessorRepository;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.ProfessorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private static final String PROFESSOR_NOT_EXISTS = "Professor with id %d doesn't exist";

    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final AccountService accountService;

    public ProfessorServiceImpl(ProfessorRepository professorRepository,
                                ProfessorMapper professorMapper,
                                AccountService accountService) {
        this.professorRepository = professorRepository;
        this.professorMapper = professorMapper;
        this.accountService = accountService;
    }

    @Override
    public ProfessorResponseDTO createProfessor(ProfessorRequestDTO professorRequestDTO) {
        Professor professor = professorMapper.toProfessor(professorRequestDTO);

        accountService.existsByEmail(professorRequestDTO.account().email());
        professor.getAccount().setRole(validateRole(professorRequestDTO.role()));
        professorRepository.save(professor);

        return professorMapper.toProfessorResponseDTO(professor);
    }

    private Role validateRole(String role) {
        return switch (role.toUpperCase()) {
            case "PROFESSOR" -> Role.PROFESSOR;
            case "ASSISTANT" -> Role.ASSISTANT;
            default -> throw new BadRequestException("Role should be either 'PROFESSOR' or 'ASSISTANT'");
        };
    }

    @Override
    public Page<ProfessorResponseDTO> getAllProfessors(Pageable pageable) {
        return professorRepository.findAll(pageable).map(professorMapper::toProfessorResponseDTO);
    }

    @Override
    public ProfessorResponseDTO getProfessorById(Long id) {
        return professorMapper.toProfessorResponseDTO(getById(id));
    }

    private Professor getById(Long id) {
        Optional<Professor> optionalProfessor = professorRepository.findById(id);

        if (optionalProfessor.isEmpty()) {
            throw new NotFoundException(PROFESSOR_NOT_EXISTS.formatted(id));
        }

        return optionalProfessor.get();
    }

    @Override
    public ProfessorResponseDTO updateProfessor(Long id, ProfessorUpdateDTO professorUpdateDTO) {
        Professor professor = getById(id);
        professorMapper.updateProfessorFromDTO(professorUpdateDTO, professor);

        professor.getAccount().setRole(validateRole(professorUpdateDTO.role()));
        professorRepository.save(professor);

        return professorMapper.toProfessorResponseDTO(professor);
    }

    @Override
    public void deleteProfessor(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new NotFoundException(PROFESSOR_NOT_EXISTS.formatted(id));
        }

        professorRepository.deleteById(id);
    }

}
// TODO: Password -> from PUT to PATCH
