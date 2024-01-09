package com.smarty.domain.activity.service;

import com.smarty.domain.activity.domain.Activity;
import com.smarty.domain.activity.model.ActivityRequestDTO;
import com.smarty.domain.activity.model.ActivityResponseDTO;
import com.smarty.domain.activity.model.ActivityUpdateDTO;
import com.smarty.domain.activity.repository.ActivityRepository;
import com.smarty.domain.student.service.StudentService;
import com.smarty.domain.task.service.TaskService;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
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
        activityMapper.updateActivityFromDTO(activityUpdateDTO, activity);

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
