package com.lezord.system_api.dto.response.paginate;

import com.lezord.system_api.dto.response.ResponseLectureRecordDetailsDTO;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class PaginatedResponseLectureRecordDetailsDTO {

    private long count;
    private List<ResponseLectureRecordDetailsDTO> dataList;
}
