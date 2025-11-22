package com.lezord.system_api.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateApplicationUserDTO {

    @NotBlank(message = "fullName required")
    private String fullName;

    @NotBlank(message = "countryCode required")
    private String countryCode;

    @NotBlank(message = "phoneNumber required")
    private String phoneNumber;
}
