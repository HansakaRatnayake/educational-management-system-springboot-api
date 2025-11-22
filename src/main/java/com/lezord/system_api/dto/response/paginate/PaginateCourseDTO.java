package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseCourseDTO;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateCourseDTO {
    private Long count;
    private List<ResponseCourseDTO> dataList;
}
