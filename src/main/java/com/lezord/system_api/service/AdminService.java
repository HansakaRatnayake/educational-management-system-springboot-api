package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestAdminDTO;
import com.lezord.system_api.dto.response.ResponseAdminDTO;

public interface AdminService {

    void update(RequestAdminDTO adminDTO, String adminId);
    void changeStatus(String adminId);

    ResponseAdminDTO getByApplicationUserId(String user);

}
