package org.hotel.backend.service;


import org.hotel.backend.domain.Extra;
import org.hotel.backend.dto.ExtraCreateCommand;
import org.hotel.backend.dto.ExtraInfo;
import org.hotel.backend.dto.ExtraUpdateCommand;
import org.hotel.backend.exceptionhandling.ExtraNotFoundException;
import org.hotel.backend.repository.ExtraRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExtraService {
    private ExtraRepository extraRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ExtraService(ExtraRepository extraRepository, ModelMapper modelMapper) {
        this.extraRepository = extraRepository;
        this.modelMapper = modelMapper;
    }

    public ExtraInfo saveExtra(ExtraCreateCommand command) {
        Extra extra = modelMapper.map(command, Extra.class);
        Extra savedExtra = extraRepository.save(extra);
        return modelMapper.map(savedExtra, ExtraInfo.class);
    }

    public ExtraInfo updateExtraById(Long extraId, ExtraUpdateCommand command) {
        Extra extra = findExtraById(extraId);
        modelMapper.map(command, extra);
        return modelMapper.map(extra, ExtraInfo.class);
    }

    public Extra findExtraById(Long extraId) {
        Optional<Extra> extraOptional = extraRepository.findById(extraId);
        if (extraOptional.isEmpty()) {
            throw new ExtraNotFoundException(extraId);
        }
        return extraOptional.get();
    }

    public List<ExtraInfo> listAllExtras() {
        return extraRepository.findAll().stream()
                .map(extra -> modelMapper.map(extra, ExtraInfo.class))
                .collect(Collectors.toList());
    }
}