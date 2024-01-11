package com.smarty.infrastructure.mapper;

import com.smarty.domain.exam.domain.Exam;
import com.smarty.domain.exam.model.ExamRequestDTO;
import com.smarty.domain.exam.model.ExamResponseDTO;
import com.smarty.domain.exam.model.ExamUpdateDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(source = "studentId", target = "student.id")
    @Mapping(source = "courseId", target = "course.id")
    Exam toExam(ExamRequestDTO examRequestDTO);

    ExamResponseDTO toExamResponseDTO(Exam exam);

    void updateExamFromDTO(ExamUpdateDTO examUpdateDTO, @MappingTarget Exam exam);

}
