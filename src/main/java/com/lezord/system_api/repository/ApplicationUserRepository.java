package com.lezord.system_api.repository;

import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.Set;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, String> {

    Optional<ApplicationUser> findByUsername(String username);

    Optional<ApplicationUser> findByPhoneNumber(String phoneNumber);


    @Query("SELECT u FROM ApplicationUser u WHERE u.username LIKE %:searchText% OR u.fullName LIKE %:searchText%")
    long countUsers(@Param("searchText") String searchText);

    @Query(
            "SELECT u FROM ApplicationUser u WHERE " +
                    "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
                    "LOWER(u.phoneNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))"
    )
    Page<ApplicationUser> searchUsers(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT u.roles FROM ApplicationUser u WHERE u.username = :username")
    Set<UserRole> findRolesByUsername(String username);


    Set<ApplicationUser> findApplicationUserByRoles(Set<UserRole> roles);
}
