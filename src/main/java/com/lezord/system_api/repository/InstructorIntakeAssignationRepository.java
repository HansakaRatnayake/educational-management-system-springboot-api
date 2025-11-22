package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Instructor;
import com.lezord.system_api.entity.InstructorIntakeAssignation;
import com.lezord.system_api.entity.InstructorIntakeAssignationKey;
import com.lezord.system_api.entity.Intake;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface InstructorIntakeAssignationRepository extends JpaRepository<InstructorIntakeAssignation, InstructorIntakeAssignationKey> {

    @Query(
            "SELECT ia FROM InstructorIntakeAssignation ia " +
                    "WHERE ia.intake.name LIKE LOWER(CONCAT('%', :searchText, '%'))" +
                    "OR ia.intake.propertyId LIKE LOWER(CONCAT('%', :searchText, '%'))" +
                    "OR ia.instructor.propertyId Like LOWER(CONCAT('%', :searchText, '%'))"
    )
    Page<InstructorIntakeAssignation> searchAssignation(@Param("searchText") String searchText, Pageable pageable);


    List<InstructorIntakeAssignation> findAllByIntakePropertyId(String intakeId);

    List<InstructorIntakeAssignation> findAllByInstructor_PropertyId(String instructorPropertyId);

    Optional<InstructorIntakeAssignation> findInstructorIntakeAssignationByInstructorAndIntake(Instructor instructor, Intake intake);

    long countInstructorIntakeAssignationByInstructor_PropertyId(String instructorPropertyId);


    Optional<InstructorIntakeAssignation> findTopByInstructor_PropertyIdOrderByCreatedDateDesc(String instructorPropertyId);
}

