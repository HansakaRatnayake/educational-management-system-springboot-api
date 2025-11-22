package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponsePendingInstructorRegistrationDetailDTO;
import lombok.*;

import java.util.List;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateResponsePendingInstructorRegistrationDetailDTO {
    private Long count;
    private List<ResponsePendingInstructorRegistrationDetailDTO> dataList;
}
