package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestApplicationRoleDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
public interface ApplicationUserRoleService {
    public void initializeRoles();

    void create(@Valid RequestApplicationRoleDTO requestApplicationRoleDTO);
    void update(@Valid RequestApplicationRoleDTO requestApplicationRoleDTO, String roleId);
    void delete(String roleId);

    List<ResponseApplicationUserRoleDTO> findAll();
}
