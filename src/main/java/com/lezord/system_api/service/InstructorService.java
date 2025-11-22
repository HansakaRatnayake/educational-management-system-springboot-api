package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestInstructorDTO;
import com.lezord.system_api.dto.response.ResponseInstructorDTO;
import com.lezord.system_api.dto.response.paginate.PaginateClientViewInstructorDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateResponseInstructorDTO;
import org.springframework.validation.annotation.Validated;

@Validated
public interface InstructorService {

    void create(RequestInstructorDTO instructorDTO);
    void update(RequestInstructorDTO instructorDTO, String instructorId);
    void delete(String instructorId);
    void changeStatus(String instructorId);

    Long totalInstructorCount();
    ResponseInstructorDTO findById(String instructorId);
    ResponseInstructorDTO findByApplicationUserId(String userId);
    PaginateResponseInstructorDTO findAll(String searchText, int pageNumber, int pageSize);
    PaginateClientViewInstructorDetailDTO findAllInstructorsForClient(String searchText, int pageNumber, int pageSize);


}
