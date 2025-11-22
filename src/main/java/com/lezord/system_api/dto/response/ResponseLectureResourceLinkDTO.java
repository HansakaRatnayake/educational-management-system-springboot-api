package com.lezord.system_api.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseLectureResourceLinkDTO {

    private String propertyId;

    private String resourceUrl;

    private LocalDate resourceDate;

}
