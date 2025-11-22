package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestInstructorIntakeAssignationDTO;
import com.lezord.system_api.dto.response.paginate.PaginateInstructorAssigmentDTO;
import org.springframework.validation.annotation.Validated;

@Validated
public interface InstructorIntakeAssignationService {

    void create(RequestInstructorIntakeAssignationDTO dto);
    void update(RequestInstructorIntakeAssignationDTO dto, String instructorId,String intakeId);
    void delete(String instructorId,String intakeId);
    void changeStatus(String instructorId,String intakeId);

    PaginateInstructorAssigmentDTO findAll(String searchText, int pageNumber, int pageSize);

}
