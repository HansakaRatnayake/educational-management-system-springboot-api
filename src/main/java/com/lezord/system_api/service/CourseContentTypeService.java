package com.lezord.system_api.service;


import com.lezord.system_api.dto.response.ResponseCourseContentTypeDTO;

import java.util.List;

public interface CourseContentTypeService {

    void initializeCourseContentType();
    List<ResponseCourseContentTypeDTO> findAll();


}
