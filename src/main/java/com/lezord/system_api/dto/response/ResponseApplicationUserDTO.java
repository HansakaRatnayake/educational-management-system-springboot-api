package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApplicationUserDTO {

    private String userId;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String phoneNumber;
    private String phoneNumberWithCountryCode;
    private List<ResponseApplicationUserRoleDTO> roles;
}
