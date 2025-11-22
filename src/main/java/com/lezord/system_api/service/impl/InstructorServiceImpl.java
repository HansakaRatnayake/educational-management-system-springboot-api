package com.lezord.system_api.service.impl;

import com.amazonaws.services.cloudfront.model.EntityNotFoundException;
import com.lezord.system_api.dto.request.RequestAcademicAndProfessionalBackgroundDetailDTO;
import com.lezord.system_api.dto.request.RequestAddressDetailDTO;
import com.lezord.system_api.dto.request.RequestEmploymentDetailDTO;
import com.lezord.system_api.dto.request.RequestInstructorDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.Instructor;
import com.lezord.system_api.entity.InstructorIntakeAssignation;
import com.lezord.system_api.entity.Intake;
import com.nozomi.system_api.dto.request.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginateClientViewInstructorDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateResponseInstructorDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.AcademicAndProfessionalBackground;
import com.lezord.system_api.entity.core.Address;
import com.lezord.system_api.entity.core.EmploymentDetails;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.ApplicationUserRepository;
import com.lezord.system_api.repository.InstructorIntakeAssignationRepository;
import com.lezord.system_api.repository.InstructorRepository;
import com.lezord.system_api.service.InstructorService;
import com.lezord.system_api.util.FileDataHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final InstructorIntakeAssignationRepository instructorIntakeAssignationRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final FileDataHandler fileDataHandler;


    @Override
    public void create(RequestInstructorDTO dto) {
        instructorRepository.save(createInstructor(dto));
    }

    @Override
    public void update(RequestInstructorDTO dto, String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntryNotFoundException("Instructor not found: " + instructorId));


        instructor.setDisplayName(dto.getDisplayName());
        instructor.setDob(dto.getDob());
        instructor.setNic(dto.getNic());
        instructor.setGender(dto.getGender());
        instructor.setAddress(mapToAddress(dto.getAddress()));
        instructor.setEmploymentDetails(mapToEmploymentDetails(dto.getEmployment()));
        instructor.setAcademicAndProfessionalBackground(mapToAcademicBackground(dto.getAcademicAndProfessionalBackground()));

        instructorRepository.save(instructor);
    }

    @Override
    public void delete(String instructorId) {
        instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntryNotFoundException("Instructor not found: " + instructorId));

        instructorRepository.deleteById(instructorId);
    }

    @Override
    public void changeStatus(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntryNotFoundException("Instructor not found: " + instructorId));

        boolean newStatus = !instructor.getEmploymentDetails().getActiveStatus();
        instructor.getEmploymentDetails().setActiveStatus(newStatus);

        instructorRepository.save(instructor);
    }

    @Override
    public Long totalInstructorCount() {
        return instructorRepository.count();
    }

    @Override
    public ResponseInstructorDTO findById(String instructorId) {
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntryNotFoundException("Instructor not found: " + instructorId));

        return mapToResponseInstructorDTO(instructor);
    }

    @Override
    public ResponseInstructorDTO findByApplicationUserId(String userId) {
        Instructor instructor = instructorRepository.findByApplicationUserUserId(userId).orElseThrow(() -> new EntryNotFoundException("instructor not found"));
        return mapToResponseInstructorDTO(instructor);
    }

    @Override
    public PaginateResponseInstructorDTO findAll(String searchText, int pageNumber, int pageSize) {
        Page<Instructor> instructorPage = instructorRepository.searchInstructors(searchText,PageRequest.of(pageNumber,pageSize,Sort.by("displayName").ascending()));


        return PaginateResponseInstructorDTO.builder()
                .count(instructorPage.getTotalElements())
                .dataList(instructorPage.getContent().stream().map(this::mapToResponseInstructorDTO).collect(Collectors.toList()))
                .build();
    }

    @Override
    public PaginateClientViewInstructorDetailDTO findAllInstructorsForClient(String searchText, int pageNumber, int pageSize) {
        Page<Instructor> instructorPage = instructorRepository.searchInstructors(searchText,PageRequest.of(pageNumber,pageSize,Sort.by("displayName").ascending()));

        return PaginateClientViewInstructorDetailDTO.builder()
                .count(instructorPage.getTotalElements())
                .dataList(instructorPage.getContent().stream().map(
                        instructor -> ResponseClientViewInstructorDetailDTO.builder()
                                .propertyId(instructor.getPropertyId())
                                .instructorName(instructor.getDisplayName())
                                .email(instructor.getEmail())
                                .experience(instructor.getAcademicAndProfessionalBackground() != null ? instructor.getAcademicAndProfessionalBackground().getTeachingExperience() : 0)
                                .highestQualification(instructor.getAcademicAndProfessionalBackground() != null ? instructor.getAcademicAndProfessionalBackground().getHighestQualification() : "No-Content")
                                .bio(instructor.getAcademicAndProfessionalBackground() != null ? instructor.getAcademicAndProfessionalBackground().getBiography() : "No-Content")
                                .avatar(instructor.getApplicationUser().getApplicationUserAvatar() != null ? fileDataHandler.byteArrayToString(instructor.getApplicationUser().getApplicationUserAvatar().getResourceUrl()) : null)
                                .assignedIntakes(instructorIntakeAssignationRepository.findAllByInstructor_PropertyId(instructor.getPropertyId())
                                        .stream()
                                        .map(instructorIntakeAssignation -> ResponseIntakeDTO.builder()
                                                .propertyId(instructorIntakeAssignation.getIntake().getPropertyId())
                                                .name(instructorIntakeAssignation.getIntake().getName())
                                                .intakeNumber(instructorIntakeAssignation.getIntake().getIntakeNumber())
                                                .courseName(instructorIntakeAssignation.getIntake().getCourse().getName())
                                                .courseId(instructorIntakeAssignation.getIntake().getCourse().getPropertyId())
                                                .price(instructorIntakeAssignation.getIntake().getPrice())
                                                .build()
                                        )
                                        .toList()
                                )
                                .build()
                ).toList())
                .build();

    }

    private Address mapToAddress(RequestAddressDetailDTO dto) {
        return Address.builder()
                .street(dto.getStreet())
                .city(dto.getCity())
                .district(dto.getDistrict())
                .province(dto.getProvince())
                .postalCode(dto.getPostalCode())
                .country(dto.getCountry())
                .build();
    }

    private EmploymentDetails mapToEmploymentDetails(RequestEmploymentDetailDTO dto) {
        return EmploymentDetails.builder()
                .employmentType(dto.getEmploymentType())
                .designation(dto.getDesignation())
                .activeStatus(true)
                .dateJoined(java.time.LocalDate.now())
                .build();
    }

    private AcademicAndProfessionalBackground mapToAcademicBackground(RequestAcademicAndProfessionalBackgroundDetailDTO dto) {
        return AcademicAndProfessionalBackground.builder()
                .highestQualification(dto.getHighestQualification())
                .japaneseLanguageLevel(dto.getJapaneseLanguageLevel())
                .teachingExperience(dto.getTeachingExperience())
                .specialization(dto.getSpecialization())
                .biography(dto.getBiography())
                .build();
    }

    private ResponseInstructorDTO mapToResponseInstructorDTO(Instructor instructor) {
        ApplicationUser applicationUser = applicationUserRepository.findById(instructor.getApplicationUser().getUserId()).orElseThrow(() -> new EntryNotFoundException("Application user not found: " + instructor.getApplicationUser().getUserId()));


        return ResponseInstructorDTO.builder()
                .applicationUser(mapToResponseApplicationUserDTO(applicationUser))
                .propertyId(instructor.getPropertyId())
                .displayName(instructor.getDisplayName())
                .gender(instructor.getGender())
                .nic(instructor.getNic())
                .dob(instructor.getDob())
                .employment(mapToResponseEmploymentDetailDTO(instructor.getEmploymentDetails()))
                .academicAndProfessionalBackground(mapToResponseAcademicAndProfessionalBackgroundDetailDTO(instructor.getAcademicAndProfessionalBackground()))
                .address(mapToResponseAddressDetailDTO(instructor.getAddress()))
                .assignedIntakes(instructor.getInstructorIntakeAssignations().stream().map(
                        this::mapToResponseInstructorIntakeDTO
                        ).toList())
                .build();
    }

    private ResponseInstructorIntakeDTO mapToResponseInstructorIntakeDTO(InstructorIntakeAssignation assignation) {
        return ResponseInstructorIntakeDTO.builder()
                .intakeId(assignation.getIntake().getPropertyId())
                .intakeName(assignation.getIntake().getName())
                .build();
    }

    private ResponseAddressDetailDTO mapToResponseAddressDetailDTO(Address address) {
        if (address == null) return null;
        return ResponseAddressDetailDTO.builder()
                .street(address.getStreet())
                .city(address.getCity())
                .district(address.getDistrict())
                .postalCode(address.getPostalCode())
                .province(address.getProvince())
                .country(address.getCountry())
                .build();
    }

    private ResponseEmploymentDetailDTO mapToResponseEmploymentDetailDTO(EmploymentDetails employmentDetails) {

        return ResponseEmploymentDetailDTO.builder()
                .activeStatus(employmentDetails != null?employmentDetails.getActiveStatus():null)
                .dateJoined(employmentDetails != null? employmentDetails.getDateJoined():null)
                .designation(employmentDetails != null?employmentDetails.getDesignation():null)
                .employmentType(employmentDetails != null?employmentDetails.getEmploymentType():null)
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

    private Instructor createInstructor(RequestInstructorDTO dto) {

        ApplicationUser applicationUser = applicationUserRepository.findById(dto.getUserId()).orElseThrow(() -> new EntityNotFoundException("User not found"));


        return Instructor.builder()
                .propertyId(generatePropertyId())
                .applicationUser(applicationUser)
                .displayName(dto.getDisplayName())
                .email(dto.getEmail())
                .dob(dto.getDob())
                .nic(dto.getNic())
                .gender(dto.getGender())
                .address(mapToAddress(dto.getAddress()))
                .employmentDetails(mapToEmploymentDetails(dto.getEmployment()))
                .academicAndProfessionalBackground(mapToAcademicBackground(dto.getAcademicAndProfessionalBackground()))
                .build();
    }

    public ResponseIntakeDTO mapToResponseIntakeDTO(Intake intake) {

        List<ResponseIntakeHasInstructorDTO> instructors = instructorIntakeAssignationRepository
                .findAllByIntakePropertyId(intake.getPropertyId())
                .stream()
                .map(assignation -> {
                    Instructor instructor = assignation.getInstructor();
                    return ResponseIntakeHasInstructorDTO.builder()
                            .instructorId(instructor.getPropertyId())
                            .instructorName(instructor.getDisplayName())
                            .email(instructor.getEmail())
                            .gender(instructor.getGender())
                            .nic(instructor.getNic())
                            .dob(instructor.getDob())
                            .applicationUser(mapToResponseApplicationUserDTO(instructor.getApplicationUser()))
                            .academicAndProfessionalBackground(mapToResponseAcademicAndProfessionalBackgroundDetailDTO(instructor.getAcademicAndProfessionalBackground()))
                            .build();
                })
                .toList();

        return ResponseIntakeDTO.builder()
                .propertyId(intake.getPropertyId())
                .name(intake.getName())
                .intakeNumber(intake.getIntakeNumber())
                .availableSeats(intake.getAvailableSeats())
                .intakeStartDate(intake.getIntakeStartDate())
                .intakeEndDate(intake.getIntakeEndDate())
                .activeStatus(intake.isActiveStatus())
                .courseId(intake.getCourse().getPropertyId())
                .courseName(intake.getCourse().getName())
                .isInstalmentEnabled(intake.isInstallmentEnabled())
                .price(intake.getPrice())
                .instructors(instructors)
                .build();
    }

    private String generatePropertyId() {
        return "INST-" + UUID.randomUUID();
    }
}
