package com.smarty.domain.major.service;

import com.smarty.domain.major.entity.Major;
import com.smarty.domain.major.model.MajorRequestDTO;
import com.smarty.domain.major.model.MajorResponseDTO;
import com.smarty.domain.major.model.MajorUpdateDTO;
import com.smarty.domain.major.repository.MajorRepository;
import com.smarty.infrastructure.exception.exceptions.ConflictException;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import com.smarty.infrastructure.mapper.MajorMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MajorServiceImpl implements MajorService {

    private static final String MAJOR_NOT_EXISTS = "Major with id %d doesn't exist";

    private final MajorRepository majorRepository;
    private final MajorMapper majorMapper;

    public MajorServiceImpl(MajorRepository majorRepository, MajorMapper majorMapper) {
        this.majorRepository = majorRepository;
        this.majorMapper = majorMapper;
    }

    @Override
    public MajorResponseDTO createMajor(MajorRequestDTO majorRequestDTO) {
        Major major = majorMapper.toMajor(majorRequestDTO);

        validateCode(majorRequestDTO.code());
        majorRepository.save(major);

        return majorMapper.toMajorResponseDTO(major);
    }

    private void validateCode(String code) {
        if (majorRepository.existsByCode(code)) {
            throw new ConflictException("Major with code %s already exists".formatted(code));
        }
    }

    @Override
    public Page<MajorResponseDTO> getAllMajors(Pageable pageable) {
        return majorRepository.findAll(pageable).map(majorMapper::toMajorResponseDTO);
    }

    @Override
    public MajorResponseDTO getMajorById(Long id) {
        return majorMapper.toMajorResponseDTO(getById(id));
    }

    private Major getById(Long id) {
        Optional<Major> optionalMajor = majorRepository.findById(id);

        if (optionalMajor.isEmpty()) {
            throw new NotFoundException(MAJOR_NOT_EXISTS.formatted(id));
        }

        return optionalMajor.get();
    }

    @Override
    public MajorResponseDTO updateMajor(Long id, MajorUpdateDTO majorUpdateDTO) {
        Major major = getById(id);
        majorMapper.updateMajorFromDTO(majorUpdateDTO, major);

        majorRepository.save(major);

        return majorMapper.toMajorResponseDTO(major);
    }

    @Override
    public void deleteMajor(Long id) {
        if (!majorRepository.existsById(id)) {
            throw new NotFoundException(MAJOR_NOT_EXISTS.formatted(id));
        }

        majorRepository.deleteById(id);
    }

}
