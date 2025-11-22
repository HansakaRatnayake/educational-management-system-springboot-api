package com.lezord.system_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lezord.system_api.entity.enums.Gender;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RequestAdminDTO {

    @NotBlank(message = "userId required")
    private String userId;

    @NotBlank(message = "phoneNumber required")
    private String phoneNumber;

    @NotBlank(message = "countryCode required")
    private String countryCode;

    @NotBlank(message = "fullName required")
    private String fullName;

    @NotBlank(message = "displayName required")
    private String displayName;

    @NotBlank(message = "email required")
    private String email;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dob;

    @NotBlank(message = "nic required")
    private String nic;

    @NotNull(message = "gender required")
    private Gender gender;

    @Valid
    @NotNull(message = "Address is required")
    private RequestAddressDetailDTO address;

    @Valid
    @NotNull(message = "Employment is required")
    private RequestEmploymentDetailDTO employment;

}
