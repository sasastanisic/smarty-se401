package com.smarty.domain.task.repository;

import com.smarty.domain.task.entity.Task;
import com.smarty.domain.task.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    boolean existsByTypeAndCourse_Id(Type type, Long courseId);

}
