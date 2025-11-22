package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseCourseDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaginatedCourseDTO {
    private Long count;
    private List<ResponseCourseDTO> dataList;
}
