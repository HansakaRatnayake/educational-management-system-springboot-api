package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Instructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InstructorRepository extends JpaRepository<Instructor, String> {

    @Query(
            "SELECT i FROM Instructor i " +
                    "JOIN i.applicationUser au " +
                    "WHERE LOWER(i.displayName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(i.employmentDetails.designation) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(i.academicAndProfessionalBackground.japaneseLanguageLevel) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(i.academicAndProfessionalBackground.specialization) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(i.nic) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(au.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(au.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))"
    )
    Page<Instructor> searchInstructors(@Param("searchText") String searchText, Pageable pageable);

    Optional<Instructor> findByApplicationUserUserId(String userId);
}
