package com.smarty.domain.activity.repository;

import com.smarty.domain.activity.domain.Activity;
import com.smarty.domain.task.enums.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    boolean existsByActivityNameAndStudent_Id(String activityName, Long studentId);

    @Query("SELECT COUNT(a) FROM activity a " +
            "JOIN task t ON a.task.id = t.id " +
            "WHERE t.type = :type AND a.student.id = :studentId AND t.course.id = :courseId")
    int findNumberOfActivitiesByTaskType(Type type, Long studentId, Long courseId);

}
