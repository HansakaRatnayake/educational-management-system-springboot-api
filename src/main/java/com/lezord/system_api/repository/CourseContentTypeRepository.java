package com.lezord.system_api.repository;

import com.lezord.system_api.entity.CourseContentType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseContentTypeRepository extends JpaRepository<CourseContentType, String> {
}
