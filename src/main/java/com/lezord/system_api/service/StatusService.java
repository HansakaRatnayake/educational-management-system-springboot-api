package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponseProgramStatusDTO;

import java.util.List;


public interface StatusService {

    List<ResponseProgramStatusDTO> getAllProgramStatus();

}
