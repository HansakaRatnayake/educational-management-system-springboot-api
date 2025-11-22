package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseAssignmentSubQuestionDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedAssignmentSubQuestionDTO {
    private long count;
    private List<ResponseAssignmentSubQuestionDTO> dataList;
}
