package com.lezord.system_api.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApplicationUserRoleDTO {
    private String propertyId;
    private String role;
    private boolean active;
}
