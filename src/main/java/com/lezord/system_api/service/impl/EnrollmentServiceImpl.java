package com.lezord.system_api.service.impl;


import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedEnrollmentDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.CourseLevel;
import com.lezord.system_api.entity.enums.PaymentStatus;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.CourseService;
import com.lezord.system_api.service.EnrollmentService;
import com.lezord.system_api.util.FileDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final StudentRepository studentRepository;
    private final IntakeRepository intakeRepository;
    private final EnrollmentRepository enrollmentRepo;
    private final FileDataHandler fileDataHandler;
    private final CourseStageRepository courseStageRepository;
    private final CourseStageContentRepository courseStageContentRepository;
    private final CourseService courseService;
    private final StudentInstallmentPlanRepository studentInstallmentPlanRepository;


    @Override
    public boolean createEnrollment(String studentId, String intakeId) {

        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student Data Not Found And Please Check Your Student Id"));

        Intake selectedIntake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake Data Not Found And Please Check Your Intake Id"));

        Enrollment saved = enrollmentRepo.save(
                Enrollment.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .activeState(true)
                        .isVerified(true)
                        .courseCompleteness(false)
                        .student(selectedStudent)
                        .course(selectedIntake.getCourse())
                        .intake(selectedIntake)
                        .canAccessCourse(true)
                        .build()
        );

        return true;

    }

    @Override
    public PaginatedEnrollmentDTO getAllEnrollments(int page, int size) {
        if (size < 1) throw new EntryNotFoundException("Please provide a page size greater that 0");
        Pageable pageable = PageRequest.of(page, size);
        Page<Enrollment> all = enrollmentRepo.findAll(pageable);
        List<ResponseEnrollmentDTO> allEnrollments = all.getContent().stream()
                .map(this::convertToResponseEnrollmentDTO)
                .collect(Collectors.toList());
        return PaginatedEnrollmentDTO.builder()
                .count(enrollmentRepo.findAllCount())
                .dataList(allEnrollments)
                .build();
    }

    @Override
    public void changeActiveStatus(String enrollmentId) {

        if (enrollmentId.isEmpty()) throw new EntryNotFoundException("Enrollment Id Not Found");

        Optional<Enrollment> selectedEnrollment = enrollmentRepo.findById(enrollmentId);
        if (selectedEnrollment.isEmpty()) throw new EntryNotFoundException("Enrollment Not Found");

        Enrollment enrollment = selectedEnrollment.get();
        enrollment.setActiveState(!enrollment.getActiveState());
        enrollmentRepo.save(enrollment);
    }

    @Override
    public void changeCourseAccessStatus(String studentId, String intakeId) {
        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student Data Not Found And Please Check Your Student Id"));
        Intake selectedIntake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake Data Not Found And Please Check Your Intake Id"));

        Enrollment selectedEnrollment = enrollmentRepo.findByStudentPropertyIdAndCoursePropertyId(selectedStudent.getPropertyId(), selectedIntake.getCourse().getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Enrollment Not Found"));

        selectedEnrollment.setCanAccessCourse(!selectedEnrollment.getCanAccessCourse());
        enrollmentRepo.save(selectedEnrollment);
    }

    @Override
    public void changeAllNonPaidStudentsCourseAccessStatus(boolean access) {
        LocalDate today = LocalDate.now();

        List<StudentInstallmentPlan> plans = studentInstallmentPlanRepository
                .findStudentInstallmentPlansByStatusAndNextAndInstallmentEndDateBefore(PaymentStatus.PENDING, true, today);

        // Collect student and course ids in bulk
        Map<Pair<String, String>, Enrollment> enrollmentsMap = enrollmentRepo
                .findAllByStudentPropertyIdInAndCoursePropertyIdIn(
                        plans.stream().map(p -> p.getStudent().getPropertyId()).collect(Collectors.toSet()),
                        plans.stream().map(p -> p.getIntake().getCourse().getPropertyId()).collect(Collectors.toSet())
                ).stream()
                .collect(Collectors.toMap(
                        e -> Pair.of(e.getStudent().getPropertyId(), e.getCourse().getPropertyId()),
                        Function.identity()
                ));

        for (StudentInstallmentPlan plan : plans) {
            String studentId = plan.getStudent().getPropertyId();
            String courseId = plan.getIntake().getCourse().getPropertyId();
            Enrollment enrollment = enrollmentsMap.get(Pair.of(studentId, courseId));

            if (enrollment == null) {
                throw new EntryNotFoundException("Enrollment Not Found for student " + studentId + " and course " + courseId);
            }

            enrollment.setCanAccessCourse(access);
        }

        // Save all at once if needed
        enrollmentRepo.saveAll(enrollmentsMap.values());
    }


    @Override
    public List<ResponsePurchaseDetailDTO> getEnrollmentPurchaseDetails(String enrollmentId) {
        Enrollment enrollment = enrollmentRepo.findById(enrollmentId).orElseThrow(() -> new EntryNotFoundException("Enrollment detail Not Found"));


        return List.of();
    }

    @Override
    public ResponseStudentCourseEnrollmentEligibilityDTO checkEnrollmentEligibility(String intakeId, String studentId) {

        Intake selectedIntake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake Id Not Found"));
        Student selectedStudent = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student Id Not Found"));

        Course course = selectedIntake.getCourse();


        List<Enrollment> enrollmentList = enrollmentRepo.findAllByStudent_PropertyId(selectedStudent.getPropertyId());
        boolean hasMetPrerequisites = studentHasCompletedCourse(course, enrollmentList);

        Optional<Enrollment> enrollmentByStudentAndIntake = enrollmentRepo.getEnrollmentByStudentAndIntake(selectedStudent.getPropertyId(), selectedIntake.getPropertyId());

        if (enrollmentByStudentAndIntake.isEmpty() && hasMetPrerequisites) {
            return ResponseStudentCourseEnrollmentEligibilityDTO.builder()
                    .eligible(true)
                    .responseCourse(courseService.findById(course.getPropertyId()))
                    .enrolled(false)
                    .build();
        }

        if (enrollmentByStudentAndIntake.isPresent()) {
            return ResponseStudentCourseEnrollmentEligibilityDTO.builder()
                    .eligible(true)
                    .responseCourse(courseService.findById(course.getPropertyId()))
                    .enrolled(true)
                    .build();
        }

        if (
                enrollmentList.stream().anyMatch(
                        enrollment -> (enrollment.getCourse().getCourseLevel() == course.getPrerequisite().getCourseLevel()) && enrollment.getCourseCompleteness()
                )
        ) {
            return ResponseStudentCourseEnrollmentEligibilityDTO.builder()
                    .eligible(true)
                    .responseCourse(null)
                    .enrolled(null)
                    .build();
        }

        if (
                enrollmentList.stream().anyMatch(
                        enrollment -> (enrollment.getCourse().getCourseLevel() == course.getPrerequisite().getCourseLevel()) && !enrollment.getCourseCompleteness()
                )
        ) {
            return ResponseStudentCourseEnrollmentEligibilityDTO.builder()
                    .eligible(false)
                    .responseCourse(courseService.findById(course.getPropertyId()))
                    .enrolled(true)
                    .build();
        }

        if (
                enrollmentList.stream().noneMatch(
                        enrollment -> (enrollment.getCourse().getCourseLevel() == course.getPrerequisite().getCourseLevel())
                )
        ) {
            return ResponseStudentCourseEnrollmentEligibilityDTO.builder()
                    .eligible(false)
                    .responseCourse(courseService.findById(course.getPropertyId()))
                    .enrolled(false)
                    .build();
        }
        return null;
    }

    private boolean studentHasCompletedCourse(Course course, List<Enrollment> enrollmentList) {
        if (course.getCourseLevel() == CourseLevel.VERY_BEGINNER) return true;
        if (enrollmentList.isEmpty()) return false;
        return enrollmentList.stream().allMatch(
                enrollment -> (enrollment.getCourse().getCourseLevel() == course.getPrerequisite().getCourseLevel()) && enrollment.getCourseCompleteness()
        );
    }

    private ResponseEnrollmentDTO convertToResponseEnrollmentDTO(Enrollment enrollment) {
        ResponseEnrollmentDTO dto = new ResponseEnrollmentDTO();
        try {
            dto = ResponseEnrollmentDTO.builder()
                    .propertyId(enrollment.getPropertyId())
                    .createdDate(enrollment.getCreatedDate())
                    .activeState(enrollment.getActiveState())
                    .isVerified(enrollment.getIsVerified())
                    .courseCompleteness(enrollment.getCourseCompleteness())
                    .student(convertToResponseStudentDTO(enrollment.getStudent()))
                    .course(convertToResponseCourseDTO(enrollment.getCourse()))
                    .intake(convertToResponseIntakeDTO(enrollment.getIntake()))
                    .build();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dto;
    }

    private ResponseStudentDTO convertToResponseStudentDTO(Student student) {
        return ResponseStudentDTO.builder()
                .propertyId(student.getPropertyId())
                .nic(student.getNic())
                .dob(student.getDob())
                .email(student.getEmail())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .displayName(student.getDisplayName())
                .gender(student.getGender())
                .activeStatus(student.getActiveState())
                .city(student.getCity())
                .country(student.getCountry())
                .build();
    }

    private ResponseCourseDTO convertToResponseCourseDTO(Course course) throws SQLException {
        return ResponseCourseDTO.builder()
                .propertyId(course.getPropertyId())
                .name(course.getName())
                .description(course.getDescription())
                .duration(course.getDuration())
                .assignmentCount(course.getAssigmentCount())
                .activeStatus(course.getActiveStatus())
                .introVideoUrl(course.getIntroVideoUrl())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .courseLevel(course.getCourseLevel().toString())
                .courseThumbnail(fileDataHandler.blobToString(new SerialBlob(course.getCourseThumbnail().getResourceUrl())))
                .build();
    }

    private ResponseIntakeDTO convertToResponseIntakeDTO(Intake intake) {
        return ResponseIntakeDTO.builder()
                .propertyId(intake.getPropertyId())
                .name(intake.getName())
                .intakeNumber(intake.getIntakeNumber())
                .intakeEndDate(intake.getIntakeEndDate())
                .availableSeats(intake.getAvailableSeats())
                .activeStatus(intake.isActiveStatus())
                .price(intake.getPrice())
                .isInstalmentEnabled(intake.isInstallmentEnabled())
                .build();
    }

//    private List<ResponseInstructorDTO> convertToResponseInstructorDTO(List<InstructorIntakeAssignation> instructors){
//        List<ResponseInstructorDTO> allData = new ArrayList<>();
//        for (InstructorIntakeAssignation instructor:instructors){
//            allData.add(
//                    ResponseInstructorDTO.builder()
//                            .propertyId(instructor.)
//                            .displayName(instructor.getDisplayName())
//                            .dob(instructor.getDob())
//                            .nic(instructor.getNic())
//                            .gender(instructor.getGender())
//                            .address(convertToResponseAddressDTO(instructor.getAddress()))
//                            .employment(convertToResponseEmploymentDTO(instructor.getEmploymentDetails()))
//                            .academicAndProfessionalBackground(convertToResponseAcademic(instructor.getAcademicAndProfessionalBackground()))
//                            .build()
//            );
//        }
//        return allData;
//    }
//
//    private ResponseAcademicAndProfessionalBackgroundDetailDTO convertToResponseAcademic(AcademicAndProfessionalBackground academicAndProfessionalBackground){
//        return ResponseAcademicAndProfessionalBackgroundDetailDTO.builder()
//                .highestQualification(academicAndProfessionalBackground.getHighestQualification())
//                .japaneseLanguageLevel(academicAndProfessionalBackground.getJapaneseLanguageLevel())
//                .teachingExperience(academicAndProfessionalBackground.getTeachingExperience())
//                .specialization(academicAndProfessionalBackground.getSpecialization())
//                .biography(academicAndProfessionalBackground.getBiography())
//                .build();
//    }
//
//    private ResponseEmploymentDetailDTO convertToResponseEmploymentDTO(EmploymentDetails employmentDetails){
//        return ResponseEmploymentDetailDTO.builder()
//                .employmentType(employmentDetails.getEmploymentType())
//                .designation(employmentDetails.getDesignation())
//                .dateJoined(employmentDetails.getDateJoined())
//                .activeStatus(employmentDetails.getActiveStatus())
//                .build();
//    }
//
//    private ResponseAddressDetailDTO convertToResponseAddressDTO(Address address){
//        return ResponseAddressDetailDTO.builder()
//                .street(address.getStreet())
//                .city(address.getCity())
//                .country(address.getCountry())
//                .district(address.getDistrict())
//                .postalCode(address.getPostalCode())
//                .province(address.getProvince())
//                .build();
//    }

}
