package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ResponsePlayerDataDTO {
    private ResponseLectureRecordDetailsDTO initialVideo;
    private ResponseCourseDTO course;
    private List<ResponseLectureRecordDetailsDTO> dataList;
}
