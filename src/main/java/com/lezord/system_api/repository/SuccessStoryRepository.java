package com.lezord.system_api.repository;

import com.lezord.system_api.entity.SuccessStory;
import com.lezord.system_api.entity.enums.SuccessStoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SuccessStoryRepository extends JpaRepository<SuccessStory, String> {


    Optional<SuccessStory> findByApplicationUser_UserId(String userId);

    @Query(
            """
            SELECT s FROM SuccessStory s 
            WHERE LOWER(s.applicationUser.username) LIKE LOWER(CONCAT('%', :searchText, '%')) 
            OR LOWER(s.applicationUser.fullName) LIKE LOWER(CONCAT('%', :searchText, '%'))
            """
    )
    Page<SuccessStory> search(@Param("searchText") String searchText, Pageable pageable);

    @Query(
            "DELETE SuccessStory s WHERE s.propertyId = :storyId"
    )
    void deleteStory(@Param("storyId") String storyId);

    @Query(
            """
            select s from SuccessStory s where s.status = :status AND LOWER(s.applicationUser.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) order by s.createdAt DESC
            """
    )
    Page<SuccessStory> findByStatusOrderByCreatedAtDesc(SuccessStoryStatus status, @Param("searchText") String searchText, Pageable pageable);

    long countSuccessStoryByActiveStatus(boolean activeStatus);
}
