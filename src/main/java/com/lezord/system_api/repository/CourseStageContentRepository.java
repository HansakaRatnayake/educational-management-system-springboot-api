package com.lezord.system_api.repository;

import com.lezord.system_api.entity.CourseStage;
import com.lezord.system_api.entity.CourseStageContent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseStageContentRepository extends JpaRepository<CourseStageContent, String> {

    Page<CourseStageContent> findByCourseStagePropertyIdOrderByOrderIndexAsc(String courseStageId, Pageable pageable);

    @Query("SELECT COALESCE(MAX(cs.orderIndex), 0) FROM CourseStageContent cs WHERE cs.courseStage.propertyId = :courseStageId")
    int getLastCourseStageContentNumber(@Param("courseStageId") String courseStageId);

    List<CourseStageContent> findByCourseStageAndOrderIndexGreaterThanOrderByOrderIndex(CourseStage courseStage, int orderIndexIsGreaterThan);

    @Query("SELECT COUNT(cs) FROM CourseStageContent cs WHERE cs.courseStage.course.propertyId = :courseId")
    long countCourseStagesContentByCourseId(@Param("courseId") String courseId);
}
