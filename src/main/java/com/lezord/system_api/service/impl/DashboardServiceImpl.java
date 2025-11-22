package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.converters.AssignmentMarksStats;
import com.lezord.system_api.dto.response.converters.MonthlyRevenue;
import com.lezord.system_api.dto.response.converters.YearlyRevenue;
import com.lezord.system_api.dto.response.util.AssignmentMarksStatsResponse;
import com.lezord.system_api.dto.response.util.EmptyAssignmentMarksStats;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.PaymentStatus;
import com.lezord.system_api.entity.enums.StudentHasAssignmentMarksTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.DashboardService;
import com.lezord.system_api.service.StudentProgressService;
import com.lezord.system_api.util.FileDataHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final IntakeRepository intakeRepository;
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final FileDataHandler fileDataHandler;
    private final PaymentRepository paymentRepository;
    private final InstructorIntakeAssignationRepository instructorIntakeAssignationRepository;
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;
    private final CourseContentTypeRepository courseContentTypeRepository;
    private final StudentProgressService studentProgressService;



    @Override
    public List<ResponseStudentDashboardViewEnrolledCourseDTO> getStudentEnrolledCourses(String studentId) {
        studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        return enrollmentRepository.getEnrollmentByStudent_PropertyId(studentId, Sort.by("createdDate").descending())
                .stream()
                .map(
                        enrollment -> ResponseStudentDashboardViewEnrolledCourseDTO.builder()
                                .courseName(enrollment.getCourse().getName())
                                .courseId(enrollment.getCourse().getPropertyId())
                                .intakeName(enrollment.getIntake().getName())
                                .intakeId(enrollment.getIntake().getPropertyId())
                                .build()
                ).toList();

    }

    @Override
    public ResponseStudentDashboardDetailDTO getStudentDashboardDetail(String studentId, String intakeId) {
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new EntryNotFoundException("Student not found"));
        Intake intake = null;

        if (intakeId.isEmpty()) {
            Optional<Enrollment> topEnrollment = enrollmentRepository.getTopByStudentOrderByCreatedDateDesc(student);
            intake = topEnrollment.map(Enrollment::getIntake).orElse(null);
        }else {
            intake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake not found"));
        }

        if (intake == null){

            List<ResponseStudentDashboardAssigmentMarksDetailDTO> assigmentMarksDetailDTOS = new ArrayList<>();
            for (CourseContentType specialization: courseContentTypeRepository.findAll()){
                assigmentMarksDetailDTOS.add(
                        ResponseStudentDashboardAssigmentMarksDetailDTO.builder()
                                .assigmentType(specialization.getType())
                                .marks(0L)
                                .build()
                );

            }
            return ResponseStudentDashboardDetailDTO.builder()
                    .assigmentMarksDetailList(assigmentMarksDetailDTOS)
                    .courseOverview(ResponseStudentDashboardCourseOverviewDTO.builder()
                            .courseName("")
                            .progress(0)
                            .instructors(List.of())
                            .duration("")
                            .assigmentCount(0)
                            .status("")
                            .build())
                    .statCards(ResponseStudentDashboardStatCardDetailDTO.builder()
                            .programCount(0)
                            .progress(0)
                            .assigmentCount(0)
                            .stageCount(0)
                            .build())
                    .build();
        }

        Enrollment enrollment = enrollmentRepository.getEnrollmentByStudentAndIntake(student.getPropertyId(), intake.getPropertyId()).orElseThrow(() -> new EntryNotFoundException("Enrollment not found"));
        enrollment.setProgress(studentProgressService.calculateStudentProgress(studentId, intake.getPropertyId()));
        enrollmentRepository.save(enrollment);

        return ResponseStudentDashboardDetailDTO.builder()
                .assigmentMarksDetailList(getAssigmentMarksDetailList(studentId, intakeId))
                .courseOverview(getCourseOverviewDetails(enrollment))
                .statCards(getStudentStatCardDetail(student, intake, enrollment))
                .build();
    }

    @Override
    public List<ResponseInstructorDashboardViewAssignedCourseDTO> getInstructorAssignedCourses(String instructorId) {
        return instructorIntakeAssignationRepository.findAllByInstructor_PropertyId(instructorId).stream().map(assignation -> ResponseInstructorDashboardViewAssignedCourseDTO.builder()
                .courseId(assignation.getIntake().getCourse().getPropertyId())
                .intakeId(assignation.getIntake().getPropertyId())
                .intakeName(assignation.getIntake().getName())
                .courseName(assignation.getIntake().getCourse().getName())
                .build()).toList();
    }

    @Override
    public ResponseAdminDashboardStatCardDetailDTO getAdminDashboardStatCardDetails() {
        return ResponseAdminDashboardStatCardDetailDTO.builder()
                .batchCount((int) intakeRepository.count())
                .instructorCount((int) instructorRepository.count())
                .studentCount((int) studentRepository.count())
                .totalRevenue(totalRevenue())
                .build();
    }

    @Override
    public ResponseInstructorDashboardDetailDTO getInstructorDashboardDetail(String instructorId, String intakeId) {
        Instructor instructor = instructorRepository.findById(instructorId).orElseThrow(() -> new EntryNotFoundException("Instructor not found"));

        Intake intake = null;

        if (intakeId.isEmpty()) {
            Optional<InstructorIntakeAssignation> latestAssignation = instructorIntakeAssignationRepository.findTopByInstructor_PropertyIdOrderByCreatedDateDesc(instructorId);
            intake = latestAssignation.map(InstructorIntakeAssignation::getIntake).orElse(null);

        }else {
            intake = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake not found"));
        }

        if (intake == null){
            ResponseInstructorDashboardDetailDTO.builder()
                    .assigmentMarksDetailList(List.of())
                    .statCards(ResponseInstructorDashboardStatCardDetailDTO.builder()
                            .assignCoursesCount(0)
                            .progress(0)
                            .stagesCount(0)
                            .assigmentCount(0)
                            .build())
                    .build();
        }

        InstructorIntakeAssignation assignations = instructorIntakeAssignationRepository.findInstructorIntakeAssignationByInstructorAndIntake(instructor, intake).orElseThrow(() -> new EntryNotFoundException("Intake assignment not found"));

        return ResponseInstructorDashboardDetailDTO.builder()
                .assigmentMarksDetailList(getAverageAssigmentMarksDetail(intake))
                .statCards(getInstructorStatCardDetail(instructor,intake,assignations))
                .build();
    }

    @Override
    public List<MonthlyRevenue> calculateMonthlyRevenue(String year) {
        return paymentRepository.findMonthlyRevenue(PaymentStatus.SUCCESS.name(),Integer.parseInt(year));
    }

    @Override
    public List<YearlyRevenue> calculateYearlyRevenue(String year) {
        return paymentRepository.findYearlyRevenue(PaymentStatus.SUCCESS.name(), Integer.parseInt(year));
    }

    @Override
    public BigDecimal totalRevenue() {
        return paymentRepository.findTotalRevenue(PaymentStatus.SUCCESS.name()).getTotalRevenue();
    }


    private List<ResponseStudentDashboardAssigmentMarksDetailDTO> getAssigmentMarksDetailList(String studentId, String intakeId) {

        List<ResponseStudentDashboardAssigmentMarksDetailDTO> assigmentMarksDetailDTOList = new ArrayList<>();

        for (CourseContentType specialization: courseContentTypeRepository.findAll()){
//            ArrayList<Long> marks = new ArrayList<>();
//            ArrayList<String> name = new ArrayList<>();

            if (intakeId == null){
                assigmentMarksDetailDTOList.add(
                        ResponseStudentDashboardAssigmentMarksDetailDTO.builder()
                                .assigmentType(specialization.getType())
                                .marks(0L)
//                                .name(name)
                                .build()
                );
            }else {
                AtomicLong total = new AtomicLong(0);
                AtomicInteger count = new AtomicInteger(0);

                for (LessonAssignment assignment : lessonAssignmentRepository.findByStudentIdAndIntakeIdAndCourseContentType(studentId, intakeId, specialization.getPropertyId())) {
                    List<StudentHasAssignment> studentHasPassedAssignments = assignment.getStudentHasAssignments()
                            .stream()
                            .filter(
                                    studentHasAssignment ->
                                            studentHasAssignment.getStudent().getPropertyId().equals(studentId) && (studentHasAssignment.getMarksType() == StudentHasAssignmentMarksTypes.PASSED)
                            ).toList();
                    studentHasPassedAssignments.forEach(
                            studentHasAssignment -> {
                                total.addAndGet(studentHasAssignment.getFullMarks());
                                count.incrementAndGet();
//                                marks.add(studentHasAssignment.getFullMarks());
//                                name.add(studentHasAssignment.getAssignment().getTitle());
                            }
                    );

                }


                assigmentMarksDetailDTOList.add(
                        ResponseStudentDashboardAssigmentMarksDetailDTO.builder()
                                .assigmentType(specialization.getType())
                                .marks(count.get() == 0 ? 0L : (total.get()/count.get()) * 100)
//                                .name(name)
                                .build()
                );
            }
        }

        return assigmentMarksDetailDTOList;
    }

    private ResponseStudentDashboardCourseOverviewDTO getCourseOverviewDetails(Enrollment enrollment) {

        return ResponseStudentDashboardCourseOverviewDTO.builder()
                .courseName(enrollment.getCourse().getName())
                .progress(enrollment.getProgress())
                .instructors(
                        enrollment.getIntake().getInstructorIntakeAssignations()
                                .stream()
                                .map(
                                        instructorIntakeAssignation -> ResponseStudentDashboardViewInstructorDetailDTO.builder()
                                                .instructorId(instructorIntakeAssignation.getInstructor().getPropertyId())
                                                .instructorName(instructorIntakeAssignation.getInstructor().getDisplayName())
                                                .instructorAvatar(instructorIntakeAssignation.getInstructor().getApplicationUser().getApplicationUserAvatar() != null ? fileDataHandler.byteArrayToString(instructorIntakeAssignation.getInstructor().getApplicationUser().getApplicationUserAvatar().getResourceUrl()) : null)
                                                .build()
                                ).toList()
                )
                .duration(enrollment.getCourse().getDuration() + " weeks")
                .assigmentCount(enrollment.getCourse().getAssigmentCount())
                .status(enrollment.getIntake().getStatus().toString())
                .build();
    }

    private ResponseStudentDashboardStatCardDetailDTO getStudentStatCardDetail(Student student, Intake intake, Enrollment enrollment) {

        return ResponseStudentDashboardStatCardDetailDTO.builder()
                .programCount((int) enrollmentRepository.countByStudentAndIntake(student, intake))
                .assigmentCount(enrollment.getCourse().getAssigmentCount())
                .progress(enrollment.getProgress())
                .stageCount(enrollment.getCourse().getCourseStages().size())
                .build();
    }

    private List<ResponseInstructorDashboardAverageAssigmentMarksDetailDTO> getAverageAssigmentMarksDetail(Intake intake) {

        List<ResponseInstructorDashboardAverageAssigmentMarksDetailDTO> assigmentMarksDetailDTOList = new ArrayList<>();

        for (CourseContentType specialization: courseContentTypeRepository.findAll()){

            ArrayList<Long> marks = new ArrayList<>();
            ArrayList<String> name = new ArrayList<>();

            AssignmentMarksStats projection  = studentHasAssignmentRepository.getAssignmentMarksStatsByFilters(intake, specialization, StudentHasAssignmentTypes.COMPLETED);
            AssignmentMarksStatsResponse response = new AssignmentMarksStatsResponse(
                    projection != null ? projection : new EmptyAssignmentMarksStats()
            );

           assigmentMarksDetailDTOList.add(
                   ResponseInstructorDashboardAverageAssigmentMarksDetailDTO.builder()
                   .assigmentType(specialization.getType())
                   .marks(response)
                   .build()
           );
        }

        return assigmentMarksDetailDTOList;
    }

    private ResponseInstructorDashboardStatCardDetailDTO getInstructorStatCardDetail(Instructor instructor, Intake intake, InstructorIntakeAssignation assignation) {

        return ResponseInstructorDashboardStatCardDetailDTO.builder()
                .assigmentCount(assignation.getIntake().getCourse().getAssigmentCount())
                .stagesCount(assignation.getIntake().getCourse().getCourseStages().size())
                .progress((int) assignation.getProgress())
                .assignCoursesCount((int) instructorIntakeAssignationRepository.countInstructorIntakeAssignationByInstructor_PropertyId(instructor.getPropertyId()))
                .build();
    }

}
