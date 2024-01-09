package com.smarty.infrastructure.mapper;

import com.smarty.domain.activity.domain.Activity;
import com.smarty.domain.activity.model.ActivityRequestDTO;
import com.smarty.domain.activity.model.ActivityResponseDTO;
import com.smarty.domain.activity.model.ActivityUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActivityMapper {

    @Mapping(source = "taskId", target = "task.id")
    @Mapping(source = "studentId", target = "student.id")
    Activity toActivity(ActivityRequestDTO activityRequestDTO);

    ActivityResponseDTO toActivityResponseDTO(Activity activity);

    void updateActivityFromDTO(ActivityUpdateDTO activityUpdateDTO, @MappingTarget Activity activity);

}
