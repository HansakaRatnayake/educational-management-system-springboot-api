package com.lezord.system_api.util;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardResponseDTO {
    private int code;
    private String message;
    private Object data;
}
