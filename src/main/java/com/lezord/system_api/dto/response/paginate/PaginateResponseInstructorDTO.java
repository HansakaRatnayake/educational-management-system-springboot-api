package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseInstructorDTO;
import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateResponseInstructorDTO {
    private Long count;
    private List<ResponseInstructorDTO> dataList;
}
