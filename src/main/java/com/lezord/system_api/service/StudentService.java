package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestStudentDTO;
import com.lezord.system_api.dto.response.ResponseStudentDTO;
import com.lezord.system_api.dto.response.paginate.PaginateNonPaidStudentDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateStudentDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface StudentService {

    void create(@Valid RequestStudentDTO studentDTO);
    void update(@Valid RequestStudentDTO studentDTO, String studentId);
    void delete(String studentId);
    void changeStatus(String studentId);

    Long count(String searchText);
    long countStudentByActiveState(boolean active);
    ResponseStudentDTO findByUserId(String userId);
    ResponseStudentDTO findById(String studentId);
    PaginateStudentDTO findByIntakeId(String intakeId,int pageNumber, int pageSize);
    PaginateStudentDTO findAll(String searchText, String courseId, String intakeId, int pageNumber, int pageSize);
    PaginateNonPaidStudentDetailDTO findNonPaidStudents(String searchText, int pageNumber, int pageSize);



}