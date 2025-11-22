package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponseCourseThumbnailDTO;
import org.springframework.web.multipart.MultipartFile;

public interface CourseThumbnailService {

     void create(MultipartFile file, String courseId);
     void delete(String courseId);
     ResponseCourseThumbnailDTO findByCourseId(String courseId);

     void update(MultipartFile file, String courseId);
}
