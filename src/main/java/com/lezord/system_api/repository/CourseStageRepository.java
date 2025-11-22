package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Course;
import com.lezord.system_api.entity.CourseContentType;
import com.lezord.system_api.entity.CourseStage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseStageRepository extends JpaRepository<CourseStage, String> {

    Page<CourseStage> findAllByCourseAndCourseContentType(Course course, CourseContentType courseContentType, Pageable pageable);

    long countCourseStageByCourseAndCourseContentType(Course course, CourseContentType courseContentType);

    List<CourseStage> findByCourseAndCourseContentTypeAndOrderIndexGreaterThanOrderByOrderIndex(Course course, CourseContentType courseContentType, int orderIndexIsGreaterThan);

    List<CourseStage> findAllByCoursePropertyIdAndCourseContentTypePropertyIdAndActiveStatusOrderByOrderIndexAsc(String courseId,String typeId,Boolean status);

    @Query("SELECT COUNT(cs) FROM CourseStage cs WHERE cs.course.propertyId = :courseId")
    long countCourseStagesByCourseId(@Param("courseId") String courseId);

    @Query("SELECT COUNT(cs) FROM CourseStage cs WHERE cs.course = :courseId")
    long countCourseStagesContentByCourseId(@Param("courseId") String courseId);

}
