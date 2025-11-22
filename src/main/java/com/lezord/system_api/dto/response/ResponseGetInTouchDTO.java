package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.GetInTouchStatus;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetInTouchDTO {

    private String propertyId;

    private String fullName;

    private String email;

    private String message;

    private String phone;

    private boolean activeStatus;

    private GetInTouchStatus status;

    private Instant createdAt;




}
