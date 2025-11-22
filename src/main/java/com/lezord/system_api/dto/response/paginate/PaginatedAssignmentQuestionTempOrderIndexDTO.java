package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseAssignmentQuestionTempOrderIndexDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedAssignmentQuestionTempOrderIndexDTO {
    private Long count;
    private List<ResponseAssignmentQuestionTempOrderIndexDTO> dataList;
}
