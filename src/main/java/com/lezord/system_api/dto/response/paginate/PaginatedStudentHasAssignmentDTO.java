package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseStudentHasAssignmentCustomDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedStudentHasAssignmentDTO {
    private Long count;
    private List<ResponseStudentHasAssignmentCustomDTO> dataList;
}
