package com.lezord.system_api.repository;

import com.lezord.system_api.entity.CoursePrerequisite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CoursePrerequisiteRepository extends JpaRepository<CoursePrerequisite, String> {

    List<CoursePrerequisite> findAllByCoursePropertyId(String courseId);

    void deleteCoursePrerequisitesByCoursePropertyId(String courseId);
}
