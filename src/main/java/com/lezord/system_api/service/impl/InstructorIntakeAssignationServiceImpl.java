package com.lezord.system_api.service.impl;


import com.lezord.system_api.dto.request.RequestInstructorIntakeAssignationDTO;
import com.lezord.system_api.dto.response.ResponseAcademicAndProfessionalBackgroundDetailDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserDTO;
import com.lezord.system_api.dto.response.ResponseApplicationUserRoleDTO;
import com.lezord.system_api.dto.response.ResponseInstructorIntakeAssignationDTO;
import com.lezord.system_api.entity.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginateInstructorAssigmentDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.AcademicAndProfessionalBackground;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.InstructorIntakeAssignationRepository;
import com.lezord.system_api.repository.InstructorRepository;
import com.lezord.system_api.repository.IntakeRepository;
import com.lezord.system_api.service.InstructorIntakeAssignationService;
import com.lezord.system_api.service.InstructorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class InstructorIntakeAssignationServiceImpl implements InstructorIntakeAssignationService {

    private final InstructorIntakeAssignationRepository instructorIntakeAssignationRepository;
    private final InstructorRepository instructorRepository;
    private final IntakeRepository intakeRepository;
    private final InstructorService instructorService;

    @Override
    public void create(RequestInstructorIntakeAssignationDTO dto) {
        Instructor instructor = instructorRepository.findById(dto.getInstructorId())
                .orElseThrow(() -> new EntryNotFoundException("Instructor not found"));
        Intake intake = intakeRepository.findById(dto.getIntakeId())
                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        InstructorIntakeAssignationKey key = new InstructorIntakeAssignationKey(
                dto.getInstructorId(), dto.getIntakeId());

        InstructorIntakeAssignation assignation = InstructorIntakeAssignation.builder()
                .instructorIntakeAssignationKey(key)
                .instructor(instructor)
                .intake(intake)
                .activeStatus(true)
                .progress(0)
                .createdDate(Instant.now())
                .build();

        instructorIntakeAssignationRepository.save(assignation);
    }

    @Override
    public void update(RequestInstructorIntakeAssignationDTO dto, String instructorId, String intakeId) {


        InstructorIntakeAssignationKey key = InstructorIntakeAssignationKey.builder().instructorId(instructorId).intakeId(intakeId).build();
        InstructorIntakeAssignation assignation = instructorIntakeAssignationRepository.findById(key)
                .orElseThrow(() -> new EntryNotFoundException("Assignation not found"));

        instructorIntakeAssignationRepository.save(assignation);
    }

    @Override
    public void delete(String instructorId,String intakeId) {
        InstructorIntakeAssignationKey key = InstructorIntakeAssignationKey.builder().instructorId(instructorId).intakeId(intakeId).build();
        InstructorIntakeAssignation assignation = instructorIntakeAssignationRepository.findById(key)
                .orElseThrow(() -> new EntryNotFoundException("Assignation not found"));

        instructorIntakeAssignationRepository.deleteById(key);
    }

    @Override
    public void changeStatus(String instructorId, String intakeId) {
        InstructorIntakeAssignationKey key = InstructorIntakeAssignationKey.builder().instructorId(instructorId).intakeId(intakeId).build();
        InstructorIntakeAssignation assignation = instructorIntakeAssignationRepository.findById(key)
                .orElseThrow(() -> new EntryNotFoundException("Assignation not found"));

        assignation.setActiveStatus(!assignation.getActiveStatus());
        instructorIntakeAssignationRepository.save(assignation);


    }

    @Override
    public PaginateInstructorAssigmentDTO findAll(String searchText, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<InstructorIntakeAssignation> page = instructorIntakeAssignationRepository.searchAssignation(searchText, pageable);

        List<ResponseInstructorIntakeAssignationDTO> dataList = page.stream().map(assignation ->
                ResponseInstructorIntakeAssignationDTO.builder()
                        .propertyId(assignation.getInstructorIntakeAssignationKey().getInstructorId() + "-" +
                                assignation.getInstructorIntakeAssignationKey().getIntakeId())
                        .instructorId(assignation.getInstructor().getPropertyId())
                        .instructorName(assignation.getInstructor().getDisplayName())
                        .instructorEmail(assignation.getInstructor().getEmail())
                        .instructorDob(assignation.getInstructor().getDob())
                        .instructorGender(assignation.getInstructor().getGender())
                        .instructorNic(assignation.getInstructor().getNic())
                        .applicationUser(mapToResponseApplicationUserDTO(assignation.getInstructor().getApplicationUser()))
                        .academicAndProfessionalBackground(mapToResponseAcademicAndProfessionalBackgroundDetailDTO(assignation.getInstructor().getAcademicAndProfessionalBackground()))
                        .intakeId(assignation.getIntake().getPropertyId())
                        .intakeName(assignation.getIntake().getName())
                        .build()
        ).toList();

        return PaginateInstructorAssigmentDTO.builder()
                .count(page.getTotalElements())
                .dataList(dataList)
                .build();
    }


    public ResponseAcademicAndProfessionalBackgroundDetailDTO mapToResponseAcademicAndProfessionalBackgroundDetailDTO(AcademicAndProfessionalBackground academicAndProfessionalBackground){
        if (academicAndProfessionalBackground == null) return null;
        return ResponseAcademicAndProfessionalBackgroundDetailDTO.builder()
                .biography(academicAndProfessionalBackground.getBiography())
                .highestQualification(academicAndProfessionalBackground.getHighestQualification())
                .japaneseLanguageLevel(academicAndProfessionalBackground.getJapaneseLanguageLevel())
                .specialization(academicAndProfessionalBackground.getSpecialization())
                .teachingExperience(academicAndProfessionalBackground.getTeachingExperience())
                .build();
    }

    public ResponseApplicationUserDTO mapToResponseApplicationUserDTO(ApplicationUser applicationUser) {


        return ResponseApplicationUserDTO.builder()
                .userId(applicationUser.getUserId())
                .username(applicationUser.getUsername())
                .fullName(applicationUser.getFullName())
                .phoneNumber(applicationUser.getCountryCode())
                .phoneNumberWithCountryCode(applicationUser.getPhoneNumber())
                .roles(applicationUser.getRoles().stream().map(
                        userRole -> ResponseApplicationUserRoleDTO.builder()
                                .propertyId(userRole.getRoleId())
                                .role(userRole.getRoleName())
                                .active(true)
                                .build()).toList())
                .build();
    }
}
