package com.lezord.system_api.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestLectureRecordDTO {
    private String title;
    private String length;
    private Date date;
    private boolean freeAvailability;
    private String courseStageContentId;
    private String intakeId;
    private MultipartFile thumbnail;
    private MultipartFile video;
}
