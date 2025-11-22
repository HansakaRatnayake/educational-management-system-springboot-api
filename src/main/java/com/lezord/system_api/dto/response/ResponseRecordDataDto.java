package com.lezord.system_api.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRecordDataDto {

    private int stagesCount;

    private long recordCount;
}
