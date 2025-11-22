package com.lezord.system_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestAddressDetailDTO {

    @NotBlank(message = "street required")
    private String street;

    @NotBlank(message = "city required")
    private String city;

    @NotBlank(message = "district required")
    private String district;

    @NotBlank(message = "province required")
    private String province;

    @NotBlank(message = "postalCode required")
    private String postalCode;

    @NotBlank(message = "country required")
    private String country;
}
