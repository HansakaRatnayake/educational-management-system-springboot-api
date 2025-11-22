package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, String> {

    @Query(
            "SELECT s FROM Student s " +
                    "JOIN s.applicationUser au WHERE " +
                    "LOWER(s.displayName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(s.nic) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(au.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))"
    )
    Page<Student> searchStudents(@Param("searchText") String searchText, Pageable pageable);

    @Query(
            """
            SELECT DISTINCT s FROM Student s
            LEFT JOIN s.enrollmentHashSet e
            LEFT JOIN e.course c
            LEFT JOIN e.intake i
            WHERE
            (
                (:searchText = '' OR :searchText IS NULL)
                OR LOWER(s.email) LIKE LOWER(CONCAT('%', :searchText, '%'))
                OR LOWER(s.applicationUser.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))
            )
            AND (
                (:courseId = '' OR :courseId IS NULL)
                OR c.propertyId = :courseId
            )
            AND (
                (:courseId = '' OR :courseId IS NULL OR :intakeId = '' OR :intakeId IS NULL)
                OR i.propertyId = :intakeId
            )
            """
    )
    Page<Student> searchStudentsWithFilters(
            @Param("searchText") String searchText,
            @Param("courseId") String courseId,
            @Param("intakeId") String intakeId,
            Pageable pageable
    );





    @Query(
            "SELECT COUNT(s) FROM Student s WHERE " +
                    "LOWER(s.displayName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(s.email) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(s.nic) LIKE LOWER(CONCAT('%', :searchText, '%'))"
    )
    Long countStudents(@Param("searchText") String searchText);


    Optional<Student> findStudentByNic(String nic);

    @Query(
            "SELECT DISTINCT s " +
                    "FROM Student s JOIN s.enrollmentHashSet e " +
                    "WHERE e.intake.propertyId = :intakeId"
    )
    Page<Student> findStudentsByIntake(@Param("intakeId") String intakeId, Pageable pageable);

    @Query(
            "SELECT COUNT(DISTINCT s) " +
                    "FROM Student s JOIN s.enrollmentHashSet e " +
                    "WHERE e.intake.propertyId = :intakeId"
    )
    Long findStudentsCountByIntake(@Param("intakeId") String intakeId);

    int countStudentByActiveState(boolean active);

    Optional<Student> findStudentByApplicationUserUserId(String userId);

    Optional<Student> findStudentByApplicationUser_PhoneNumber(String applicationUserPhoneNumber);



}
