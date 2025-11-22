package com.lezord.system_api.repository;

import com.lezord.system_api.dto.response.ResponseFailedRequestCustomDTO;
import com.lezord.system_api.entity.Enrollment;
import com.lezord.system_api.entity.Intake;
import com.lezord.system_api.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment,String> {

    @Query(value = "SELECT COUNT(property_id) FROM enrollment",nativeQuery = true)
    Long findAllCount();


    List<Enrollment> findAllByStudent_PropertyId(String studentPropertyId);

    List<Enrollment> findAllByStudentPropertyIdAndCoursePropertyIdOrderByCreatedDateDesc(String studentId, String courseId);
    List<Enrollment> findAllByStudentPropertyIdOrderByCreatedDateDesc(String studentId);

    @Query("""
    SELECT e FROM Enrollment e
    WHERE e.course.propertyId = :courseId
      AND (
        LOWER(e.student.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
        LOWER(e.student.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))
      )
""")
    Page<Enrollment> searchByCourseAndStudentName(@Param("courseId") String courseId, @Param("searchText") String searchText,Pageable pageable);
    @Query("""
    SELECT e FROM Enrollment e
    WHERE e.course.propertyId = :courseId
      AND e.intake.propertyId = :intakeId
      AND (
        LOWER(e.student.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
        LOWER(e.student.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))
      )
      
""")
    Page<Enrollment> searchByCourseIntakeAndStudentName(
            @Param("courseId") String courseId,
            @Param("intakeId") String intakeId,
            @Param("searchText") String searchText,
            Pageable pageable
    );


    @Query("""
    SELECT DISTINCT new com.nozomi.system_api.dto.response.ResponseFailedRequestCustomDTO(
        r.propertyId,
        CONCAT(s.firstName, ' ', s.lastName),
        s.email,
        a.title,
        r.isRequestAccepted
    )
    FROM StudentAssignmentFailedRequest r
    JOIN r.student s
    JOIN r.lessonAssignment a
    JOIN Enrollment e ON e.student.propertyId = s.propertyId
    WHERE r.isRequestAccepted = false
      AND (:courseId IS NULL OR :courseId = '' OR e.course.propertyId = :courseId)
      AND (:intakeId IS NULL OR e.intake.propertyId = :intakeId)
      AND (
          :searchText IS NULL OR :searchText = '' OR
          LOWER(s.firstName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR
          LOWER(s.lastName) LIKE LOWER(CONCAT('%', :searchText, '%'))
      )
""")
    Page<ResponseFailedRequestCustomDTO> findFailedRequestsFlexible(
            @Param("courseId") String courseId,
            @Param("intakeId") String intakeId,
            @Param("searchText") String searchText,
            Pageable pageable
    );




    List<Enrollment> findAllByStudentPropertyIdAndCourseNameContainingIgnoreCase(String studentId, String searchText);

    default List<Enrollment> findByStudentAndOptionalCourseOrderByCreatedDateDesc(String studentId, String courseId) {
        if (courseId != null && !courseId.isEmpty()) {
            return findAllByStudentPropertyIdAndCoursePropertyIdOrderByCreatedDateDesc(studentId, courseId);
        } else {
            return findAllByStudentPropertyIdOrderByCreatedDateDesc(studentId);
        }
    }

    @Query(
            "SELECT e FROM Enrollment e WHERE e.student.propertyId = :studentId AND e.intake.propertyId = :intakeId"
    )
    Optional<Enrollment> getEnrollmentByStudentAndIntake(String studentId, String intakeId);

    Optional<Enrollment> findByStudentPropertyIdAndCoursePropertyId(String studentId, String courseId);

    List<Enrollment> getEnrollmentByStudent_PropertyId(String studentPropertyId, Sort sort);

    long countByStudentAndIntake(Student student, Intake intake);

    Optional<Enrollment> getTopByStudentOrderByCreatedDateDesc(Student student);

    List<Enrollment> findEnrollmentByIntake(Intake intake);

    Optional<Enrollment> findFirstByStudentPropertyIdOrderByCreatedDateDesc(String studentId);
           
    List<Enrollment> findAllByStudentPropertyIdInAndCoursePropertyIdIn(Set<String> studentIds, Set<String> courseIds);

    List<Enrollment> findByStudentPropertyIdInAndIntakeCoursePropertyIdIn(Set<String> studentIds, Set<String> courseIds);

}
