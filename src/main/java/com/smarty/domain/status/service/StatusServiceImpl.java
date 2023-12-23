package com.smarty.domain.status.service;

import com.smarty.domain.status.entity.Status;
import com.smarty.domain.status.repository.StatusRepository;
import com.smarty.infrastructure.exception.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;

    public StatusServiceImpl(StatusRepository statusRepository) {
        this.statusRepository = statusRepository;
    }

    @Override
    public Status getStatusById(Long id) {
        Optional<Status> optionalStatus = statusRepository.findById(id);

        if (optionalStatus.isEmpty()) {
            throw new NotFoundException("Status with id %d doesn't exist".formatted(id));
        }

        return optionalStatus.get();
    }

}
