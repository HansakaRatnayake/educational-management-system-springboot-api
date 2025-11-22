package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseStudentIntakeEnrollmentDetailDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateStudentIntakeEnrollmentDetailDTO {
    private Long count;
    private List<ResponseStudentIntakeEnrollmentDetailDTO> dataList;
}
