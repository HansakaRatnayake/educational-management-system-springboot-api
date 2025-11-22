package com.lezord.system_api.dto.response;

import com.lezord.system_api.entity.enums.JapaneseLanguageLevel;
import com.lezord.system_api.entity.enums.Specialization;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAcademicAndProfessionalBackgroundDetailDTO {

    private String highestQualification;

    private JapaneseLanguageLevel japaneseLanguageLevel;

    private int teachingExperience;

    private Specialization specialization;

    private String biography;
}
