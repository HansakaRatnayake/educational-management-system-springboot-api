package com.lezord.system_api.repository;

import com.lezord.system_api.entity.ApplicationUserAvatar;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface ApplicationUserAvatarRepository extends JpaRepository<ApplicationUserAvatar, String> {

    Optional<ApplicationUserAvatar> findApplicationUserAvatarByApplicationUserUserId(String userId);


}
