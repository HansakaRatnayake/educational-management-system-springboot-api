package com.lezord.system_api.dto.request;

import com.lezord.system_api.entity.enums.JapaneseLanguageLevel;
import com.lezord.system_api.entity.enums.Specialization;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestAcademicAndProfessionalBackgroundDetailDTO {

    @NotBlank(message = "HighestQualification required")
    private String highestQualification;

    @NotNull(message = "japaneseLanguageLevel required")
    private JapaneseLanguageLevel japaneseLanguageLevel;

    @NotBlank(message = "teachingExperience required")
    private int teachingExperience;

    @NotNull(message = "specialization required")
    private Specialization specialization;

    @NotBlank(message = "biography required")
    private String biography;
}
