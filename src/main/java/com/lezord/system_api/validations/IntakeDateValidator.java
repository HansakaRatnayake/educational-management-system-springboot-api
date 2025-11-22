package com.lezord.system_api.validations;

import com.lezord.system_api.dto.request.RequestIntakeDTO;
import com.lezord.system_api.validations.anno.ValidIntakeDates;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IntakeDateValidator implements ConstraintValidator<ValidIntakeDates, RequestIntakeDTO> {

    @Override
    public boolean isValid(RequestIntakeDTO dto, ConstraintValidatorContext context) {
        if (dto.getIntakeStartDate() == null || dto.getIntakeEndDate() == null) {
            return true; // Let @NotBlank or @Future handle nulls
        }

        return dto.getIntakeEndDate().isAfter(dto.getIntakeStartDate());
    }
}
