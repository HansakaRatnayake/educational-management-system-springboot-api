package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestIntakeDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.CourseRepository;
import com.lezord.system_api.repository.EnrollmentRepository;
import com.lezord.system_api.repository.InstructorIntakeAssignationRepository;
import com.lezord.system_api.repository.IntakeRepository;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginateIntakeDTO;
import com.lezord.system_api.dto.response.paginate.PaginateStudentIntakeEnrollmentDetailDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.AcademicAndProfessionalBackground;
import com.lezord.system_api.entity.enums.IntakeStatus;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.InstructorService;
import com.lezord.system_api.service.IntakeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class IntakeServiceImpl implements IntakeService {

    private final IntakeRepository intakeRepository;
    private final CourseRepository courseRepository;
    private final InstructorIntakeAssignationRepository instructorIntakeAssignationRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final InstructorService instructorService;

    @Override
    public void create(RequestIntakeDTO dto) {

        Course course = courseRepository.findById(dto.getCourseId()).orElseThrow(() -> new EntryNotFoundException(String.format("Course with id %s not found", dto.getCourseId())));


        Intake intake = Intake.builder()
                .propertyId(UUID.randomUUID().toString())
                .name(dto.getName())
                .intakeNumber(generateIntakeNumber(dto.getCourseId()))
                .availableSeats(dto.getAvailableSeats())
                .intakeStartDate(dto.getIntakeStartDate())
                .intakeEndDate(dto.getIntakeEndDate())
                .installmentEnabled(false)
//                .isExposeToClient(false)
                .activeStatus(true)
                .course(course)
                .price(dto.getPrice())
                .status(IntakeStatus.PENDING)
                .build();

        intakeRepository.save(intake);
    }

    @Override
    public void update(RequestIntakeDTO dto, String intakeId) {
        Intake selectedIntake = intakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        selectedIntake.setName(dto.getName());
        selectedIntake.setAvailableSeats(dto.getAvailableSeats());
        selectedIntake.setIntakeStartDate(dto.getIntakeStartDate());
        selectedIntake.setIntakeEndDate(dto.getIntakeEndDate());
        selectedIntake.setPrice(dto.getPrice());

        intakeRepository.save(selectedIntake);
    }

    @Override
    public void delete(String intakeId) {
        if (!intakeRepository.existsById(intakeId)) {
            throw new EntryNotFoundException("Intake not found");
        }
        intakeRepository.deleteById(intakeId);
    }

    @Override
    public void changeStatus(String intakeId) {
        Intake intake = intakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        intake.setActiveStatus(!intake.isActiveStatus());
        intakeRepository.save(intake);
    }

    @Override
    public void changeinstalment(String intakeId) {
        Intake intake = intakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        intake.setActiveStatus(!intake.isInstallmentEnabled());
        intakeRepository.save(intake);
    }

//    @Override
//    public void changeClientExposeStatus(String intakeId) {
//        Intake intake = intakeRepository.findById(intakeId)
//                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));
//
//        intake.setExposeToClient(!intake.isExposeToClient());
//        intakeRepository.save(intake);
//    }


    @Override
    public ResponseIntakeDTO findById(String intakeId) {
        Intake intake = intakeRepository.findById(intakeId)
                .orElseThrow(() -> new EntryNotFoundException("Intake not found"));

        return mapToResponseIntakeDTO(intake);
    }

    @Override
    public PaginateIntakeDTO findAll(String courseId, String instructorId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("intakeStartDate").descending());
        Page<Intake> page = intakeRepository.findAllByCourseIdAndInstructorId(
                (courseId != null && !courseId.isBlank()) ? courseId : null,
                (instructorId != null && !instructorId.isBlank()) ? instructorId : null,
                pageable
        );

        return PaginateIntakeDTO.builder()
                .count(page.getTotalElements())
                .dataList(page.getContent().stream().map(this::mapToResponseIntakeDTO).toList())
                .build();
    }

    @Override
    public PaginateStudentIntakeEnrollmentDetailDTO findAllForStudent(String courseId, String studentId) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student Id Not Found");

        List<Enrollment> selectedEnrollments = enrollmentRepository.findByStudentAndOptionalCourseOrderByCreatedDateDesc(studentId, courseId);

        List<ResponseStudentIntakeEnrollmentDetailDTO> list = new ArrayList<>();
        for (Enrollment e:selectedEnrollments){
            Optional<Intake> selectedIntake = intakeRepository.findByPropertyIdOrderByIntakeStartDateDesc(e.getIntake().getPropertyId());
            list.add(ResponseStudentIntakeEnrollmentDetailDTO.builder()
                            .intake(
                                    ResponseIntakeDTO.builder()
                                            .propertyId(selectedIntake.get().getPropertyId())
                                            .name(selectedIntake.get().getName())
                                            .intakeNumber(selectedIntake.get().getIntakeNumber())
                                            .intakeStartDate(selectedIntake.get().getIntakeStartDate())
                                            .intakeEndDate(selectedIntake.get().getIntakeEndDate())
                                            .availableSeats(selectedIntake.get().getAvailableSeats())
                                            .activeStatus(selectedIntake.get().isActiveStatus())
                                            .price(selectedIntake.get().getPrice())
                                            .isInstalmentEnabled(selectedIntake.get().isInstallmentEnabled())
                                            .courseId(selectedIntake.get().getCourse().getPropertyId())
                                            .courseName(selectedIntake.get().getCourse().getName())
                                            .build()
                            )
                            .access(e.getCanAccessCourse())
                            .build()

            );
        }
        return  PaginateStudentIntakeEnrollmentDetailDTO.builder()
                .count((long) selectedEnrollments.size())
                .dataList(list)
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


    private int generateIntakeNumber(String courseId) {
        int last = intakeRepository.getLastIntakeNumber(courseId);
        return last + 1;
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
