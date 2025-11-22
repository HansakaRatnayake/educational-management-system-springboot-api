package com.lezord.system_api.repository;

import com.lezord.system_api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface AdminRepository extends JpaRepository<Admin, String> {

    Optional<Admin> findAdminByNic(String nic);

    Optional<Admin> findAdminByApplicationUser_PhoneNumber(String applicationUserPhoneNumber);

    Optional<Admin> findAdminByApplicationUser_UserId(String applicationUserUserId);


}
