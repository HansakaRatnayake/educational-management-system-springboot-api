package com.lezord.system_api.repository;

import com.lezord.system_api.dto.response.converters.AssignmentMarksStats;
import com.lezord.system_api.entity.CourseContentType;
import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.Student;
import com.lezord.system_api.entity.StudentHasAssignment;
import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentHasAssignmentRepository extends JpaRepository<StudentHasAssignment, String> {
    Optional<StudentHasAssignment> findFirstByStudentPropertyIdAndAssignmentPropertyIdOrderByCreatedAtDesc(String studentId, String assignmentId);


    List<StudentHasAssignment> findStudentHasAssignmentsByAssignment_IntakeAndAssignment_Lesson_CourseStage_CourseContentTypeAndStatusType(Intake assignmentIntake, CourseContentType assignmentLessonCourseStageCourseContentType, StudentHasAssignmentTypes statusType);


    @Query("""
            SELECT 
                MAX(s.fullMarks) AS maxMarks,
                MIN(s.fullMarks) AS minMarks,
                AVG(s.fullMarks) AS averageMarks
            FROM StudentHasAssignment s
            WHERE 
                s.assignment.intake = :intake AND
                s.assignment.lesson.courseStage.courseContentType = :courseContentType AND
                s.statusType = :statusType
            """)
    AssignmentMarksStats getAssignmentMarksStatsByFilters(
            @Param("intake") Intake intake,
            @Param("courseContentType") CourseContentType courseContentType,
            @Param("statusType") StudentHasAssignmentTypes statusType
    );

    @Query(value = """
    SELECT s FROM StudentHasAssignment s 
    JOIN s.assignment a 
    JOIN a.lesson l 
    JOIN l.courseStage c 
    JOIN c.courseContentType t 
    WHERE a.intake.propertyId = :intakeId 
    AND s.statusType IN :statusTypes 
    AND s.student.propertyId = :studentId 
    AND t.propertyId = :contentTypeId 
    AND s.updateAt = (
        SELECT MAX(s2.updateAt) 
        FROM StudentHasAssignment s2 
        WHERE s2.assignment.propertyId = s.assignment.propertyId 
        AND s2.student.propertyId = s.student.propertyId
    )
    ORDER BY a.orderIndex ASC
""")
    Page<StudentHasAssignment> findAllCompletedAssignments(
            @Param("studentId") String studentId,
            @Param("intakeId") String intakeId,
            @Param("contentTypeId") String contentTypeId,
            @Param("statusTypes") List<StudentHasAssignmentTypes> statusTypes,
            Pageable pageable
    );

    List<StudentHasAssignment> findAllByAssignmentPropertyId(String assignmentId);


    Page<StudentHasAssignment> findAllByStudentPropertyIdAndStatusTypeOrderByUpdateAtDesc(String studentId, StudentHasAssignmentTypes type, Pageable pageable);

    long countStudentHasAssignmentsByAssignment_IntakeAndStudentAndStatusTypeAndMarksType(Intake assignmentIntake, Student student, StudentHasAssignmentTypes statusType, StudentHasAssignmentMarksTypes marksType);

}
