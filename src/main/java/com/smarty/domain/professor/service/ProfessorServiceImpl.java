package com.smarty.domain.professor.service;

import com.smarty.domain.account.enums.Role;
import com.smarty.domain.account.model.PasswordUpdateDTO;
import com.smarty.domain.account.service.AccountService;
import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.professor.entity.Professor;
import com.smarty.domain.professor.model.ProfessorRequestDTO;
import com.smarty.domain.professor.model.ProfessorResponseDTO;
import com.smarty.domain.professor.model.ProfessorUpdateDTO;
import com.smarty.domain.professor.repository.ProfessorRepository;
import com.smarty.infrastructure.email.EmailNotificationService;
import com.smarty.infrastructure.exception.exceptions.BadRequestException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.ProfessorMapper;
import com.smarty.infrastructure.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorServiceImpl implements ProfessorService {

    private static final String PROFESSOR_NOT_EXISTS = "Professor with id %d doesn't exist";

    private final ProfessorRepository professorRepository;
    private final ProfessorMapper professorMapper;
    private final AccountService accountService;
    private final CourseService courseService;
    private final AuthenticationService authenticationService;
    private final EmailNotificationService emailNotificationService;
    private PasswordEncoder passwordEncoder;

    public ProfessorServiceImpl(ProfessorRepository professorRepository,
                                ProfessorMapper professorMapper,
                                AccountService accountService,
                                @Lazy CourseService courseService,
                                AuthenticationService authenticationService,
                                EmailNotificationService emailNotificationService) {
        this.professorRepository = professorRepository;
        this.professorMapper = professorMapper;
        this.accountService = accountService;
        this.courseService = courseService;
        this.authenticationService = authenticationService;
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public ProfessorResponseDTO createProfessor(ProfessorRequestDTO professorRequestDTO) {
        Professor professor = professorMapper.toProfessor(professorRequestDTO);
        var encryptedPassword = encodePassword(professorRequestDTO.account().password());

        accountService.validateEmail(professorRequestDTO.account().email());
        emailNotificationService.sendConfirmation(professor.getAccount().getEmail(), professorRequestDTO.name());
        professor.getAccount().setPassword(encryptedPassword);
        professor.getAccount().setRole(validateRole(professorRequestDTO.role()));
        professorRepository.save(professor);

        return professorMapper.toProfessorResponseDTO(professor);
    }

    private String encodePassword(String password) {
        return passwordEncoder.encode(password);
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

    public Professor getById(Long id) {
        Optional<Professor> optionalProfessor = professorRepository.findById(id);

        if (optionalProfessor.isEmpty()) {
            throw new NotFoundException(PROFESSOR_NOT_EXISTS.formatted(id));
        }

        return optionalProfessor.get();
    }

    @Override
    public ProfessorResponseDTO getProfessorByEmail(String email) {
        Professor professor = professorRepository.findByAccount_Email(email);

        if (professor == null) {
            throw new NotFoundException("Professor with email %s doesn't exist".formatted(email));
        }

        return professorMapper.toProfessorResponseDTO(professor);
    }

    @Override
    public void existsById(Long id) {
        if (!professorRepository.existsById(id)) {
            throw new NotFoundException(PROFESSOR_NOT_EXISTS.formatted(id));
        }
    }

    @Override
    public List<ProfessorResponseDTO> getProfessorsByCourse(Long courseId) {
        List<Professor> professorsByCourse = professorRepository.findProfessorsByCourse(courseId);
        courseService.existsById(courseId);

        if (professorsByCourse.isEmpty()) {
            throw new NotFoundException("List of professors by course is empty");
        }

        return professorsByCourse
                .stream()
                .map(professorMapper::toProfessorResponseDTO)
                .toList();
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
    public ProfessorResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO) {
        Professor professor = getById(id);
        var encryptedPassword = encodePassword(passwordUpdateDTO.password());

        authenticationService.canUpdatePassword(professor.getAccount().getEmail());
        arePasswordsMatching(passwordUpdateDTO.password(), passwordUpdateDTO.confirmedPassword());
        professor.getAccount().setPassword(encryptedPassword);
        professorRepository.save(professor);

        return professorMapper.toProfessorResponseDTO(professor);
    }

    private void arePasswordsMatching(String password, String confirmedPassword) {
        if (!password.matches(confirmedPassword)) {
            throw new BadRequestException("Passwords aren't matching");
        }
    }

    @Override
    public void deleteProfessor(Long id) {
        existsById(id);
        professorRepository.deleteById(id);
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

}
