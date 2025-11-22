package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Intake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IntakeRepository extends JpaRepository<Intake, String> {

    @Query("SELECT COALESCE(MAX(i.intakeNumber), 0) FROM Intake i WHERE i.course.propertyId = :courseId")
    int getLastIntakeNumber(@Param("courseId") String courseId);


    @Query(
            "SELECT i FROM Intake i " +
                    "LEFT JOIN i.instructorIntakeAssignations assignation " +
                    "LEFT JOIN assignation.instructor instructor " +
                    "WHERE " +
                    "(:courseId IS NULL OR LOWER(i.course.propertyId) LIKE LOWER(CONCAT('%', :courseId, '%'))) " +
                    "AND (:instructorId IS NULL OR LOWER(instructor.propertyId) LIKE LOWER(CONCAT('%', :instructorId, '%')))"
    )
    Page<Intake> findAllByCourseIdAndInstructorId(
            @Param("courseId") String courseId,
            @Param("instructorId") String instructorId,
            Pageable pageable
    );


    List<Intake> findAllByCoursePropertyId(@Param("courseId") String courseId);


    Optional<Intake> findTopByCoursePropertyIdOrderByIntakeStartDateDesc(String courseId);

    Optional<Intake> findByPropertyIdOrderByIntakeStartDateDesc(String id);

    List<Intake> findIntakesByActiveStatus(boolean activeStatus, Sort sort);
}
