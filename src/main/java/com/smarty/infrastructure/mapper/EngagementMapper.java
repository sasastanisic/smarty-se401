package com.smarty.infrastructure.mapper;

import com.smarty.domain.engagement.entity.Engagement;
import com.smarty.domain.engagement.model.EngagementRequestDTO;
import com.smarty.domain.engagement.model.EngagementResponseDTO;
import com.smarty.domain.engagement.model.EngagementUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EngagementMapper {

    @Mapping(source = "professorId", target = "professor.id")
    @Mapping(source = "courseId", target = "course.id")
    Engagement toEngagement(EngagementRequestDTO engagementRequestDTO);

    EngagementResponseDTO toEngagementResponseDTO(Engagement engagement);

    @Mapping(source = "professorId", target = "professor.id")
    @Mapping(source = "courseId", target = "course.id")
    void updateEngagementFromDTO(EngagementUpdateDTO engagementUpdateDTO, @MappingTarget Engagement engagement);

}
