package com.smarty.domain.activity.service;

import com.smarty.domain.activity.domain.Activity;
import com.smarty.domain.activity.model.ActivityRequestDTO;
import com.smarty.domain.activity.model.ActivityResponseDTO;
import com.smarty.domain.activity.model.ActivityUpdateDTO;
import com.smarty.domain.activity.repository.ActivityRepository;
import com.smarty.domain.student.service.StudentService;
import com.smarty.domain.task.enums.Type;
import com.smarty.domain.task.service.TaskService;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.ForbiddenException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.ActivityMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ActivityServiceImpl implements ActivityService {

    private static final String ACTIVITY_NOT_EXISTS = "Activity with id %d doesn't exist";

    private final ActivityRepository activityRepository;
    private final ActivityMapper activityMapper;
    private final TaskService taskService;
    private final StudentService studentService;

    public ActivityServiceImpl(ActivityRepository activityRepository,
                               ActivityMapper activityMapper,
                               TaskService taskService,
                               StudentService studentService) {
        this.activityRepository = activityRepository;
        this.activityMapper = activityMapper;
        this.taskService = taskService;
        this.studentService = studentService;
    }

    @Override
    public ActivityResponseDTO createActivity(ActivityRequestDTO activityRequestDTO) {
        Activity activity = activityMapper.toActivity(activityRequestDTO);
        var task = taskService.getById(activityRequestDTO.taskId());
        var student = studentService.getById(activityRequestDTO.studentId());

        validateActivityNameForStudent(activityRequestDTO.activityName(), activityRequestDTO.studentId());
        validateActivityPoints(activityRequestDTO.points(), task.getMaxPoints());
        validateNumberOfActivitiesByTaskType(String.valueOf(task.getType()), activityRequestDTO.studentId(), task.getCourse().getId(), task.getNumberOfTasks());

        activity.setTask(task);
        activity.setStudent(student);
        activityRepository.save(activity);

        return activityMapper.toActivityResponseDTO(activity);
    }

    private void validateActivityNameForStudent(String activityName, Long studentId) {
        if (activityRepository.existsByActivityNameAndStudent_Id(activityName, studentId)) {
            throw new ConflictException("Activity named %s already exists for student with id %d".formatted(activityName, studentId));
        }
    }

    private void validateActivityPoints(double activityPoints, double maxPoints) {
        if (activityPoints > maxPoints) {
            throw new ForbiddenException("It is not allowed to enter %.2f points for this activity".formatted(activityPoints));
        }
    }

    private void validateNumberOfActivitiesByTaskType(String type, Long studentId, Long courseId, int numberOfTasks) {
        int numberOfActivities = activityRepository.findNumberOfActivitiesByTaskType(Type.valueOf(type), studentId, courseId);

        if (numberOfActivities == numberOfTasks) {
            throw new ForbiddenException("Limit for storing activities by type %s is reached".formatted(type));
        }
    }

    @Override
    public Page<ActivityResponseDTO> getAllActivities(Pageable pageable) {
        return activityRepository.findAll(pageable).map(activityMapper::toActivityResponseDTO);
    }

    @Override
    public ActivityResponseDTO getActivityById(Long id) {
        return activityMapper.toActivityResponseDTO(getById(id));
    }

    private Activity getById(Long id) {
        return activityRepository.findById(id).orElseThrow(() -> new NotFoundException(ACTIVITY_NOT_EXISTS.formatted(id)));
    }

    @Override
    public ActivityResponseDTO updateActivity(Long id, ActivityUpdateDTO activityUpdateDTO) {
        Activity activity = getById(id);
        var task = taskService.getById(activity.getTask().getId());
        activityMapper.updateActivityFromDTO(activityUpdateDTO, activity);

        validateActivityPoints(activityUpdateDTO.points(), task.getMaxPoints());
        activity.setTask(task);
        activityRepository.save(activity);

        return activityMapper.toActivityResponseDTO(activity);
    }

    @Override
    public void deleteActivity(Long id) {
        if (!activityRepository.existsById(id)) {
            throw new NotFoundException(ACTIVITY_NOT_EXISTS.formatted(id));
        }

        activityRepository.deleteById(id);
    }

}
