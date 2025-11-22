package com.lezord.system_api.dto.response.paginate;


import com.lezord.system_api.dto.response.ResponseCourseStageContentDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateCourseStageContentDTO {

    private Long count;
    private List<ResponseCourseStageContentDTO> dataList;
}
