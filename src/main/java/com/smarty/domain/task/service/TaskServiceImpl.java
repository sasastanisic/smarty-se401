package com.smarty.domain.task.service;

import com.smarty.domain.course.service.CourseService;
import com.smarty.domain.task.entity.Task;
import com.smarty.domain.task.enums.Type;
import com.smarty.domain.task.model.TaskRequestDTO;
import com.smarty.domain.task.model.TaskResponseDTO;
import com.smarty.domain.task.model.TaskUpdateDTO;
import com.smarty.domain.task.repository.TaskRepository;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.ForbiddenException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.TaskMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskServiceImpl implements TaskService {

    private static final String TASK_NOT_EXISTS = "Task with id %d doesn't exist";
    private static final int MAX_TASK_POINTS_BY_COURSE = 70;

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final CourseService courseService;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskMapper taskMapper,
                           CourseService courseService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.courseService = courseService;
    }

    @Override
    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        Task task = taskMapper.toTask(taskRequestDTO);
        var course = courseService.getById(taskRequestDTO.courseId());

        validateTypeByCourse(taskRequestDTO.type(), taskRequestDTO.courseId());
        validateTotalTaskPointsByCourse(taskRequestDTO.maxPoints(), taskRequestDTO.numberOfTasks(), taskRequestDTO.courseId());
        task.setCourse(course);
        taskRepository.save(task);

        return taskMapper.toTaskResponseDTO(task);
    }

    private void validateTypeByCourse(String type, Long courseId) {
        if (taskRepository.existsByTypeAndCourse_Id(Type.valueOf(type), courseId)) {
            throw new ConflictException("Course with id %d already has task type '%s'".formatted(courseId, type));
        }
    }

    private void validateTotalTaskPointsByCourse(double maxPoints, int numberOfTasks, Long courseId) {
        var totalTaskPoints = taskRepository.findTotalTaskPointsByCourse(courseId);

        totalTaskPoints = totalTaskPoints == null ? 0.0 : totalTaskPoints;

        if (totalTaskPoints + maxPoints * numberOfTasks > MAX_TASK_POINTS_BY_COURSE) {
            throw new ForbiddenException("The limit of " + MAX_TASK_POINTS_BY_COURSE + " points for saving tasks has been reached");
        }
    }

    @Override
    public Page<TaskResponseDTO> getAllTasks(Pageable pageable) {
        return taskRepository.findAll(pageable).map(taskMapper::toTaskResponseDTO);
    }

    @Override
    public TaskResponseDTO getTaskById(Long id) {
        return taskMapper.toTaskResponseDTO(getById(id));
    }

    public Task getById(Long id) {
        Optional<Task> optionalTask = taskRepository.findById(id);

        if (optionalTask.isEmpty()) {
            throw new NotFoundException(TASK_NOT_EXISTS.formatted(id));
        }

        return optionalTask.get();
    }

    @Override
    public List<TaskResponseDTO> getTasksByCourse(Long courseId) {
        List<Task> tasksByCourse = taskRepository.findByCourse_Id(courseId);
        courseService.existsById(courseId);

        if (tasksByCourse.isEmpty()) {
            throw new NotFoundException("There are 0 tasks for course with id %d".formatted(courseId));
        }

        return tasksByCourse
                .stream()
                .map(taskMapper::toTaskResponseDTO)
                .toList();
    }

    @Override
    public TaskResponseDTO updateTask(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = getById(id);
        taskMapper.updateTaskFromDTO(taskUpdateDTO, task);

        taskRepository.save(task);

        return taskMapper.toTaskResponseDTO(task);
    }

    @Override
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException(TASK_NOT_EXISTS.formatted(id));
        }

        taskRepository.deleteById(id);
    }

}
