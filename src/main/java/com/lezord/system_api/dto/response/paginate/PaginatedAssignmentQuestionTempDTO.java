package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseAssignmentQuestionTempDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedAssignmentQuestionTempDTO {
    private Long count;
    private List<ResponseAssignmentQuestionTempDTO> dataList;
}
