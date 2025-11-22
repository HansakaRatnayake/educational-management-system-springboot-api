package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.ResponseProgramStatusDTO;
import com.lezord.system_api.entity.enums.ProgramStatus;
import com.lezord.system_api.service.StatusService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class StatusServiceImpl implements StatusService {
    @Override
    public List<ResponseProgramStatusDTO> getAllProgramStatus() {
        return Arrays.stream(ProgramStatus.values())
                .map(status -> new ResponseProgramStatusDTO(status.name()))
                .toList();
    }
}
