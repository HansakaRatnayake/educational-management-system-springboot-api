package com.lezord.system_api.repository;

import com.lezord.system_api.entity.CourseStageContent;
import com.lezord.system_api.entity.LessonAssignment;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonAssignmentRepository extends JpaRepository<LessonAssignment, String> {

    @Query(value = "SELECT COUNT(property_id) FROM lesson_assignment WHERE lesson_id = ?1",nativeQuery = true)
    Long findAllCount(String lessonId);


    @Query("SELECT COALESCE(MAX(cs.orderIndex), 0) FROM LessonAssignment cs WHERE cs.lesson.propertyId = :lessonId")
    int getLastLessonAssignmentNumber(@Param("lessonId") String lessonId);
    Page<LessonAssignment> findByLessonPropertyIdAndIntakePropertyIdOrderByOrderIndexAsc(String lessonId, String intake, Pageable pageable);
    List<LessonAssignment> findByLessonPropertyIdAndIntakePropertyIdOrderByOrderIndexAsc(String lessonId, String intake);
    Page<LessonAssignment> findByLessonPropertyIdAndIntakePropertyIdAndStatusTypeInOrderByOrderIndexAsc(
            String lessonId,
            String intake,
            List<LessonAssignmentStatusTypes> types,
            Pageable pageable
    );

    List<LessonAssignment> findByLessonPropertyIdAndIntakePropertyIdAndStatusTypeInOrderByOrderIndexAsc(
            String lessonId,
            String intake,
            List<LessonAssignmentStatusTypes> types
    );


    List<LessonAssignment> findByLessonAndOrderIndexGreaterThanOrderByOrderIndex(CourseStageContent courseStageContent, int orderIndexIsGreaterThan);

    Optional<LessonAssignment> findByPropertyIdAndStatusType(String assignmentId, LessonAssignmentStatusTypes type);


    @Query("""
        SELECT la
        FROM LessonAssignment la
        JOIN la.studentHasAssignments sha
        JOIN la.lesson csc
        JOIN csc.courseStage cs
        WHERE sha.student.propertyId = :studentId
          AND la.intake.propertyId = :intakeId
          AND cs.courseContentType.propertyId = :courseContentTypeId
        """)
    List<LessonAssignment> findByStudentIdAndIntakeIdAndCourseContentType(
            @Param("studentId") String studentId,
            @Param("intakeId") String intakeId,
            @Param("courseContentTypeId") String courseContentTypeId
    );


    long countLessonAssignmentsByLesson(CourseStageContent lesson);

    List<LessonAssignment> findAllByLessonCourseStageCourseContentTypePropertyIdAndIntakePropertyId(String contentTypeId, String intakeId);
}
