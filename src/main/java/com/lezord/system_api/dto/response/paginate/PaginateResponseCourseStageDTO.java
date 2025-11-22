package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseCourseStageDTO;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateResponseCourseStageDTO {
    private Long count;
    private List<ResponseCourseStageDTO> dataList;
}
