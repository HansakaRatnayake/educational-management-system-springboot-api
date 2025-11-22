package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestCourseDTO;
import com.lezord.system_api.dto.response.ResponseCourseDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedCourseDTO;
import jakarta.validation.Valid;

import java.util.List;

public interface CourseService {

    public void create(@Valid RequestCourseDTO requestCourseDTO);
    public void update(@Valid RequestCourseDTO requestCourseDTO, String courseId);
    public void delete(String courseId);
    public boolean changeStatus(String courseId);

    int count(String searchText);
    public ResponseCourseDTO findById(String courseId);
    public ResponseCourseDTO findByIntakeId(String intakeId);
    public List<ResponseCourseDTO> findAll(String searchText);

    PaginatedCourseDTO findAllCoursesByStudentId(String searchText,String studentId);
    List<ResponseCourseDTO> findLatestCourseslist();
}
