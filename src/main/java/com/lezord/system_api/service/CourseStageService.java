package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestCourseStageDTO;
import com.lezord.system_api.dto.response.ResponseAuthenticatedCourseStageTypeDTO;
import com.lezord.system_api.dto.response.ResponseClientCourseStageTypeDTO;
import com.lezord.system_api.dto.response.paginate.PaginateResponseCourseStageDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface CourseStageService {

    void create(@Valid RequestCourseStageDTO requestCourseStageDTO);
    void update(@Valid RequestCourseStageDTO requestCourseStageDTO, String courseStageId);
    void delete(String courseStageId);
    public List<ResponseClientCourseStageTypeDTO> getAllStagesWithData(String courseId);
    public List<ResponseAuthenticatedCourseStageTypeDTO> getAllStagesWithDataForAuthenticated(String courseId,String studentId,String intakeId);

    PaginateResponseCourseStageDTO findAllByCourseAndStageContentType(String courseId, String CourseStageContentTypeId, int pageNo, int pageSize);

}
