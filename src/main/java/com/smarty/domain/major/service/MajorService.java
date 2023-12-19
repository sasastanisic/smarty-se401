package com.smarty.domain.major.service;

import com.smarty.domain.major.model.MajorRequestDTO;
import com.smarty.domain.major.model.MajorResponseDTO;
import com.smarty.domain.major.model.MajorUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MajorService {

    MajorResponseDTO createMajor(MajorRequestDTO majorRequestDTO);

    Page<MajorResponseDTO> getAllMajors(Pageable pageable);

    MajorResponseDTO getMajorById(Long id);

    MajorResponseDTO updateMajor(Long id, MajorUpdateDTO majorUpdateDTO);

    void deleteMajor(Long id);

}
