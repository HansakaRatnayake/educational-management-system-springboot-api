package com.lezord.system_api.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAddressDetailDTO {

    private String street;
    private String city;
    private String district;
    private String province;
    private String postalCode;
    private String country;
}
