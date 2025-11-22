package com.lezord.system_api.entity.core;

import com.lezord.system_api.entity.enums.JapaneseLanguageLevel;
import com.lezord.system_api.entity.enums.Specialization;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class AcademicAndProfessionalBackground {

    @Column(name = "highest_qualification")
    private String highestQualification;

    @Enumerated(EnumType.STRING)
    @Column(name = "japanese_language_level")
    private JapaneseLanguageLevel japaneseLanguageLevel;// e.g., JLPT N1/N2/N3

    @Column(name = "teaching_experience")
    private int teachingExperience; // e.g., "5 years"

    @Enumerated(EnumType.STRING)
    @Column(name = "specialization")
    private Specialization specialization;// e.g., Grammar, Kanji

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;// Optional bio for profile


}
