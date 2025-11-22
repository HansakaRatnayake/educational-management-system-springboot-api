package com.lezord.system_api.repository;

import com.lezord.system_api.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {


    Optional<UserRole> findUserRoleByRoleName(String roleName);
    Optional<UserRole> findTopByOrderByRoleIdDesc();

    Optional<UserRole> findByRoleName(String roleName);


//    @Query("SELECT ur FROM user_role ur WHERE ur.roleName = :role")
//    public Optional<UserRole> findByRoleName(@Param("role") String role, Pageable pageable);

}
