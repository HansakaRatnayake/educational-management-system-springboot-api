package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseApplicationUserAvatarDTO {

    private String propertyId;
    private String resourceUrl;
}
