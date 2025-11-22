package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseAssignmentQuestionDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedAssignmentQuestionDTO {
    private long count;
    private List<ResponseAssignmentQuestionDTO> dataList;
}
