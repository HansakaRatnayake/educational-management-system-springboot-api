package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseEnrollmentDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedEnrollmentDTO {
    private long count;
    private List<ResponseEnrollmentDTO> dataList;
}
