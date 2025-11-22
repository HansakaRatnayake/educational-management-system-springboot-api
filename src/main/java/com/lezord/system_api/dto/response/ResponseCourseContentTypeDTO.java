package com.lezord.system_api.dto.response;


import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseCourseContentTypeDTO {

    private String propertyId;
    private String courseContentType;

}
