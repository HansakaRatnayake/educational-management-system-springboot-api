package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseNonPaidStudentDetailsDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateNonPaidStudentDetailDTO {

    private boolean access;
    private Long count;
    private List<ResponseNonPaidStudentDetailsDTO> dataList;
}
