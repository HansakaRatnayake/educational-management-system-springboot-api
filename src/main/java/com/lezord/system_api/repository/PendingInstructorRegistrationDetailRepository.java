package com.lezord.system_api.repository;

import com.lezord.system_api.entity.PendingInstructorRegistrationDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PendingInstructorRegistrationDetailRepository extends JpaRepository<PendingInstructorRegistrationDetail, String> {
    Optional<PendingInstructorRegistrationDetail> findByUsername(String username);

    @Query(
            """
            SELECT p FROM PendingInstructorRegistrationDetail p WHERE 
                    LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR 
                    LOWER(p.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR 
                    LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))
            """
    )
    Page<PendingInstructorRegistrationDetail> searchPendingInstructorRegistrationDetail(@Param("searchText") String searchText, Pageable pageable);


    @Query(
            "SELECT COUNT(p)  FROM PendingInstructorRegistrationDetail p WHERE " +
                    "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(p.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))"
    )
    Long searchCountPendingInstructorRegistrationDetail(@Param("searchText") String searchText);
}
