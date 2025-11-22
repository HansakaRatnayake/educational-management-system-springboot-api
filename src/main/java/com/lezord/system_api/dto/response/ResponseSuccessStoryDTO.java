package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseSuccessStoryDTO {

    private String propertyId;

    private String title;

    private int rating;

    private String story;

    private String userName;

    private String userAvatar;

    private boolean activeStatus;

    private String status;
}
