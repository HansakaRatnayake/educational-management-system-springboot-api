package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseCourseDTO;
import com.lezord.system_api.dto.response.ResponseLectureRecordDetailsDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PlayerDataResponseDto {
    private ResponseLectureRecordDetailsDTO initialVideo;
    private ResponseCourseDTO course;
    private List<ResponseLectureRecordDetailsDTO> dataList;
}
