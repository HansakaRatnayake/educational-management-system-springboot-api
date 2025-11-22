package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestCourseStageContentDTO;
import com.lezord.system_api.dto.request.RequestUpdateCourseStageContentDTO;
import com.lezord.system_api.dto.response.paginate.PaginateCourseStageContentDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface CourseStageContentService {

    void create(@Valid RequestCourseStageContentDTO courseStageContentDTO);
    void update(@Valid RequestUpdateCourseStageContentDTO courseStageContentDTO, String courseStageId);
    void delete(String courseStageId);
    void changeStatus(String courseStageId);

    PaginateCourseStageContentDTO getById(String courseStageId, int page, int size);

}
