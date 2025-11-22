package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestPendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.dto.response.ResponsePendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateResponsePendingInstructorRegistrationDetailDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface PendingInstructorRegistrationDetailService {

    void create(@Valid RequestPendingInstructorRegistrationDetailDTO requestPendingInstructorRegistrationDetailDTO);
    void delete(String pendingInstructorRegistrationId);

    long count();
    PaginateResponsePendingInstructorRegistrationDetailDTO findAll(String searchText, Integer pageNum, Integer pageSize);
    ResponsePendingInstructorRegistrationDetailDTO findById(String pendingInstructorRegistrationId);




}
