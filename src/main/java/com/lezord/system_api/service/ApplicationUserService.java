package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestApplicationUserByAdminDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserDTO;
import com.lezord.system_api.dto.request.RequestApplicationUserPasswordResetDTO;
import com.lezord.system_api.dto.request.RequestUpdateApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import com.lezord.system_api.dto.response.paginate.PaginateApplicationUserDTO;
import com.lezord.system_api.entity.ApplicationUser;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;

@Validated
public interface ApplicationUserService extends UserDetailsService {
     void create(RequestApplicationUserDTO dto);

     void update(RequestUpdateApplicationUserDTO dto, String userId);

     void initializeAdmin() throws IOException;

     void changeStatus(boolean status, String username, String tokenHeader);

     ResponseApplicationUserDTO findData(String tokenHeader);

     void forgotPasswordSendVerificationCode(String email);

     boolean verifyReset(String otp, String email);

     boolean passwordReset(RequestApplicationUserPasswordResetDTO dto);

     ApplicationUser findById(String userId);

     PaginateApplicationUserDTO findAll(String searchText, int pageNumber, int pageSize);

     void delete(String userId);

     ResponseApplicationUserDTO getApplicationUserByUsername(String username);

     ResponseApplicationUserDTO getApplicationUserByUsername(OAuth2User oAuth2User);

     ApplicationUser processOAuthPostLogin(OAuth2User oAuth2User);

     List<ResponseApplicationUserRoleDTO> findUserRoleByUsername(String username);

     void createUserByAdmin(RequestApplicationUserByAdminDTO dto);

     List<ResponseApplicationUserDTO> findAllByRole(String role);

     void changeRoleForApplicationUser(String roleId, String userId, boolean active);



}
