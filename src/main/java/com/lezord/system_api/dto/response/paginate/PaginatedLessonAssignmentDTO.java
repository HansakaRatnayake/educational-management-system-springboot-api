package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseLessonAssignmentDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedLessonAssignmentDTO {
    private long count;
    private List<ResponseLessonAssignmentDTO> dataList;
}
