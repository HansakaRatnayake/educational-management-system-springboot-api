package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseStudentDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginateStudentDTO {

    private Long count;
    private List<ResponseStudentDTO> dataList;
}
