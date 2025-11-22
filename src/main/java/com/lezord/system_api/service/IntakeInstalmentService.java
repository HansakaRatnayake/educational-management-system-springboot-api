package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestIntakeInstalmentDTO;
import com.lezord.system_api.dto.response.ResponseIntakeInstallmentDTO;

import java.util.List;

public interface IntakeInstalmentService {

    void create (List<RequestIntakeInstalmentDTO> requestIntakeInstalmentDTOS, String intakeId);
    void update (List<RequestIntakeInstalmentDTO> dtos, String intakeId);
    void delete (String intakeId);
    void changeStatus(String installmentId);


    List<ResponseIntakeInstallmentDTO> findInstallmentsByIntake(String intakeId);
    ResponseIntakeInstallmentDTO findInstallmentById(String installmentId);
}

