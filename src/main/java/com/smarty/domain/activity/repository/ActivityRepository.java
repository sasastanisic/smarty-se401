package com.smarty.domain.activity.repository;

import com.smarty.domain.activity.domain.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    boolean existsByActivityNameAndStudent_Id(String activityName, Long studentId);

}
