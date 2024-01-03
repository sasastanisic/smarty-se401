package com.smarty.domain.engagement.service;

import com.smarty.domain.course.domain.Course;
import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.engagement.entity.Engagement;
import com.smarty.domain.engagement.model.EngagementRequestDTO;
import com.smarty.domain.engagement.model.EngagementResponseDTO;
import com.smarty.domain.engagement.model.EngagementUpdateDTO;
import com.smarty.domain.engagement.repository.EngagementRepository;
import com.smarty.domain.professor.entity.Professor;
import com.smarty.domain.professor.service.ProfessorService;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.EngagementMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class EngagementServiceImpl implements EngagementService {

    private static final String ENGAGEMENT_NOT_EXISTS = "Engagement with id %d doesn't exist";

    private final EngagementRepository engagementRepository;
    private final EngagementMapper engagementMapper;
    private final ProfessorService professorService;
    private final CourseService courseService;

    public EngagementServiceImpl(EngagementRepository engagementRepository,
                                 EngagementMapper engagementMapper,
                                 ProfessorService professorService,
                                 CourseService courseService) {
        this.engagementRepository = engagementRepository;
        this.engagementMapper = engagementMapper;
        this.professorService = professorService;
        this.courseService = courseService;
    }

    @Override
    public EngagementResponseDTO createEngagement(EngagementRequestDTO engagementRequestDTO) {
        Engagement engagement = engagementMapper.toEngagement(engagementRequestDTO);
        var professor = professorService.getById(engagementRequestDTO.professorId());
        var course = courseService.getById(engagementRequestDTO.courseId());

        validateProfessorAndCourse(professor, course);
        engagement.setProfessor(professor);
        engagement.setCourse(course);
        engagementRepository.save(engagement);

        return engagementMapper.toEngagementResponseDTO(engagement);
    }

    private void validateProfessorAndCourse(Professor professor, Course course) {
        if (engagementRepository.existsByProfessorAndCourse(professor, course)) {
            throw new ConflictException("Engagement already exists for professor %s and course %s".formatted(professor.getName(), course.getCode()));
        }
    }

    @Override
    public Page<EngagementResponseDTO> getAllEngagements(Pageable pageable) {
        return engagementRepository.findAll(pageable).map(engagementMapper::toEngagementResponseDTO);
    }

    @Override
    public EngagementResponseDTO getEngagementById(Long id) {
        return engagementMapper.toEngagementResponseDTO(getById(id));
    }

    private Engagement getById(Long id) {
        return engagementRepository.findById(id).orElseThrow(() -> new NotFoundException(ENGAGEMENT_NOT_EXISTS.formatted(id)));
    }

    @Override
    public EngagementResponseDTO updateEngagement(Long id, EngagementUpdateDTO engagementUpdateDTO) {
        Engagement engagement = getById(id);
        var professor = professorService.getById(engagementUpdateDTO.professorId());
        var course = courseService.getById(engagementUpdateDTO.courseId());
        engagementMapper.updateEngagementFromDTO(engagementUpdateDTO, engagement);

        validateProfessorAndCourse(professor, course);
        engagement.setProfessor(professor);
        engagement.setCourse(course);
        engagementRepository.save(engagement);

        return engagementMapper.toEngagementResponseDTO(engagement);
    }

    @Override
    public void deleteEngagement(Long id) {
        if (!engagementRepository.existsById(id)) {
            throw new NotFoundException(ENGAGEMENT_NOT_EXISTS.formatted(id));
        }

        engagementRepository.deleteById(id);
    }

}
