package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestIntakeDTO;
import com.lezord.system_api.dto.response.ResponseIntakeDTO;
import com.lezord.system_api.dto.response.paginate.PaginateIntakeDTO;
import com.lezord.system_api.dto.response.paginate.PaginateStudentIntakeEnrollmentDetailDTO;
import org.springframework.validation.annotation.Validated;

@Validated
public interface IntakeService {

    void create(RequestIntakeDTO intakeDTO);
    void update(RequestIntakeDTO intakeDTO, String instructorId);
    void delete(String intakeId);
    void changeStatus(String intakeId);
    void changeinstalment(String intakeId);
//    void changeClientExposeStatus(String intakeId);

    ResponseIntakeDTO findById(String intakeId);
    PaginateIntakeDTO findAll(String searchText, String instructorId, int pageNumber, int pageSize);
    PaginateStudentIntakeEnrollmentDetailDTO findAllForStudent(String courseId, String studentId);

}
