package com.lezord.system_api.repository;

import com.lezord.system_api.entity.CourseThumbnail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CourseThumbnailRepository extends JpaRepository<CourseThumbnail, String> {

    @Query("SELECT c FROM CourseThumbnail c WHERE c.course.propertyId = :courseId")
    Optional<CourseThumbnail> findByCourseId(@Param("courseId") String courseId);


}
