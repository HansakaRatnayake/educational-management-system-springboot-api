package com.lezord.system_api.service;


import com.lezord.system_api.dto.response.ResponseStudentInstallmentPlanDTO;

import java.util.List;

public interface StudentInstallmentPlanService {


    List<ResponseStudentInstallmentPlanDTO> getStudentInstallmentPlans(String studentId, String intakeId);
}
