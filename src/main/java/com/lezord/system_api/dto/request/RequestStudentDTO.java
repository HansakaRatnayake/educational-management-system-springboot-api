package com.lezord.system_api.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lezord.system_api.entity.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestStudentDTO {

    @NotBlank(message = "userId required")
    private String userId;

    @NotBlank(message = "phoneNumber required")
    private String phoneNumber;

    @NotBlank(message = "country code required")
    private String countryCode;

    @NotBlank(message = "Fullname name required")
    private String fullName;

    @NotBlank(message = "first name required")
    private String firstName;

    @NotBlank(message = "last name required")
    private String lastName;

    @NotBlank(message = "Display name is required")
    private String displayName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "DOB is required")
    private LocalDate dob;

    @NotBlank(message = "NIC is required")
    private String nic;

    @NotBlank(message = "Email is required")
    private String email;

    @NotNull(message = "Gender is required")
    private Gender gender;

    @NotNull(message = "Address is required")
    private String address;

    @NotNull(message = "City is required")
    private String city;

    @NotNull(message = "Country is required")
    private String country;

    
}
