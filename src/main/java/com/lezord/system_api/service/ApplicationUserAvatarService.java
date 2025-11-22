package com.lezord.system_api.service;

import com.lezord.system_api.dto.response.ResponseApplicationUserAvatarDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ApplicationUserAvatarService {

    void create(MultipartFile file, String userId);
    void delete(String userId);
    void update(MultipartFile file, String userId);

    ResponseApplicationUserAvatarDTO findByUserId(String userId);


}
