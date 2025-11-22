package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestCourseDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginatedCourseDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.core.AcademicAndProfessionalBackground;
import com.lezord.system_api.entity.enums.CourseLevel;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.exception.InternalServerException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.CourseService;
import com.lezord.system_api.service.FileService;
import com.lezord.system_api.util.FileDataHandler;
import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final CourseStageRepository courseStageRepository;
    private final CourseStageContentRepository courseStageContentRepository;
    private final IntakeRepository intakeRepository;
    private final InstructorIntakeAssignationRepository instructorIntakeAssignationRepository;
    private final CourseThumbnailRepository courseThumbnailRepository;
    private final CoursePrerequisiteRepository coursePrerequisiteRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final FileService fileService;
    private final FileDataHandler fileDataHandler;

    @Value("${aws.bucketName}")
    private String bucket;


    @Override
    public void create(RequestCourseDTO requestCourseDTO) {
        Course course = createCourse(requestCourseDTO);
        UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = null;


        if ((requestCourseDTO.getCourseLevel() != CourseLevel.VERY_BEGINNER) && (courseRepository.findByCourseLevel(CourseLevel.VERY_BEGINNER).isEmpty())){
            throw new BadRequestException("Cannot add course without adding a VERY_BEGINNER course");
        }

        try {
            if (requestCourseDTO.getCourseThumbnail() != null && !requestCourseDTO.getCourseThumbnail().isEmpty()) {
                uploadedResourceBinaryDataDTO = fileService.create(
                        requestCourseDTO.getCourseThumbnail(), bucket, "courses/thumbnails"
                );

                CourseThumbnail courseThumbnail = null;

                courseThumbnail = CourseThumbnail.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .createdDate(Instant.now())
                        .course(course)
                        .hash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()))
                        .directory(uploadedResourceBinaryDataDTO.getDirectory().getBytes())
                        .fileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()))
                        .resourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()))
                        .build();


                course.setCourseThumbnail(courseThumbnail);
                courseRepository.save(course);


            } else {
                throw new BadRequestException("Course thumbnail is empty");
            }

        }catch ( Exception e) {
            if (uploadedResourceBinaryDataDTO != null) {
                try {
                    fileService.delete(
                            fileDataHandler.blobToString(new SerialBlob(uploadedResourceBinaryDataDTO.getFilename())),
                            bucket,
                            uploadedResourceBinaryDataDTO.getDirectory()
                    );
                } catch (SQLException ex) {
                    throw new InternalServerException("Error while deleting file: " + ex.getMessage());
                }

            }
            if (e instanceof BadRequestException) {
                throw new BadRequestException("Course thumbnail is empty");
            }
            throw new InternalServerException("Error while converting date: " + e.getMessage());
        }




    }

    @Override
    public void update(RequestCourseDTO requestCourseDTO, String courseId) {  // need to implement after commit deletion option when update failed
        UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = null;
        Course selectedCourse = courseRepository.findById(courseId).orElseThrow(
                () -> new EntryNotFoundException(String.format("%s Course not found", courseId))
        );

        Optional<CourseThumbnail> selectedCourseThumbnail = courseThumbnailRepository.findByCourseId(courseId);
        CourseThumbnail courseThumbnail = selectedCourseThumbnail.orElse(null);


        try {


            if (requestCourseDTO.getCourseThumbnail() != null && !requestCourseDTO.getCourseThumbnail().isEmpty()) {

                uploadedResourceBinaryDataDTO = fileService.create(requestCourseDTO.getCourseThumbnail(), bucket, "courses/thumbnails");

                if (selectedCourseThumbnail.isPresent()) {
                    // Update the existing CourseThumbnail object in-place
                    CourseThumbnail existingThumbnail = selectedCourseThumbnail.get();

                    String oldFileName = fileDataHandler.blobToString(new SerialBlob(existingThumbnail.getFileName()));
                    String oldDirectory = fileDataHandler.blobToString(new SerialBlob(existingThumbnail.getDirectory()));

                    existingThumbnail.setUpdatedDate(Instant.now());
                    existingThumbnail.setHash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()));
                    existingThumbnail.setDirectory(uploadedResourceBinaryDataDTO.getDirectory().getBytes());
                    existingThumbnail.setFileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()));
                    existingThumbnail.setResourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()));

                    // Delete old file from storage
                    fileService.delete(oldFileName, bucket, oldDirectory);

                    courseThumbnail = existingThumbnail;
                } else {
                    // Create new CourseThumbnail
                    courseThumbnail = CourseThumbnail.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .createdDate(Instant.now())
                            .course(selectedCourse)
                            .hash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()))
                            .directory(uploadedResourceBinaryDataDTO.getDirectory().getBytes())
                            .fileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()))
                            .resourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()))
                            .build();
                }

            }

            selectedCourse.setPropertyId(courseId);
            selectedCourse.setName(requestCourseDTO.getName());
            selectedCourse.setDescription(requestCourseDTO.getDescription());
            selectedCourse.setDuration(requestCourseDTO.getDuration());
            selectedCourse.setAssigmentCount(requestCourseDTO.getAssigmentCount());
            selectedCourse.setIntroVideoUrl(requestCourseDTO.getIntroVideoUrl());
            selectedCourse.setUpdatedAt(Instant.now());
            selectedCourse.setCourseLevel(requestCourseDTO.getCourseLevel());
            selectedCourse.setCourseThumbnail(courseThumbnail);
            selectedCourse.setPrerequisite(courseRepository.findById(requestCourseDTO.getPrerequisite()).orElseThrow(() -> new EntryNotFoundException("pre requisite course not found")));

            courseRepository.save(selectedCourse);


        } catch ( SQLException e) {
            if (uploadedResourceBinaryDataDTO != null) {
                try {
                    fileService.delete(
                            fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.get().getFileName())),
                            bucket,
                            fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.get().getDirectory()))
                    );
                } catch (SQLException ex) {
                    throw new InternalServerException("Error while deleting file: " + ex.getMessage());
                }

            }
            throw new InternalServerException("Error while converting date: " + e.getMessage());
        }
    }


    @Override
    public void delete(String courseId) {

        CourseThumbnail selectedCourseThumbnail = courseThumbnailRepository.findByCourseId(courseId).orElseThrow(
                () -> new EntryNotFoundException(String.format("%s Course not found", courseId))
        );

        coursePrerequisiteRepository.deleteCoursePrerequisitesByCoursePropertyId(courseId);
        courseRepository.deleteById(courseId);

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {

                try {
                    fileService.delete(
                            fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.getFileName())),
                            bucket,
                            fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.getDirectory()))
                    );

                } catch (SQLException ex) {
                    throw new InternalServerException("Error while deleting file: " + ex.getMessage());
                }
            }
        });

    }

    @Override
    public boolean changeStatus(String courseId) {
        Course selectedCourse = courseRepository.findById(courseId).orElseThrow(
                () -> new EntryNotFoundException(String.format("%s Course not found", courseId))
        );
        selectedCourse.setActiveStatus(!selectedCourse.getActiveStatus());
        courseRepository.save(selectedCourse);
        return !selectedCourse.getActiveStatus();
    }

    @Override
    public int count(String searchText) {
        return courseRepository.countCourses(searchText);
    }

    @Override
    public ResponseCourseDTO findById(String courseId) {
        return convertToResponseCourseDto(courseRepository.findById(courseId).orElseThrow(
                () -> new EntryNotFoundException(String.format("%s Course not found", courseId))
        ));
    }

    @Override
    public ResponseCourseDTO findByIntakeId(String intakeId) {
        Intake itk = intakeRepository.findById(intakeId).orElseThrow(() -> new EntryNotFoundException("Intake not found for this course"));

        try {

            List<ResponseIntakeDTO> responseIntakeDTOS = intakeRepository.findAllByCoursePropertyId(itk.getCourse().getPropertyId()).stream().map(
                    intake -> ResponseIntakeDTO.builder()
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
                            .build()
            ).toList();

            List<ResponseIntakeHasInstructorDTO> instructors = null;

            instructors = instructorIntakeAssignationRepository
                    .findAllByIntakePropertyId(itk.getPropertyId())
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

            return ResponseCourseDTO.builder()
                    .propertyId(itk.getCourse().getPropertyId())
                    .name(itk.getCourse().getName())
                    .description(itk.getCourse().getDescription())
                    .duration(itk.getCourse().getDuration())
                    .assignmentCount(itk.getCourse().getAssigmentCount())
                    .activeStatus(itk.getCourse().getActiveStatus())
                    .introVideoUrl(itk.getCourse().getIntroVideoUrl())
                    .createdAt(itk.getCourse().getCreatedAt())
                    .updatedAt(itk.getCourse().getUpdatedAt())
                    .courseLevel(itk.getCourse().getCourseLevel().toString())
                    .courseThumbnail(fileDataHandler.blobToString(new SerialBlob(itk.getCourse().getCourseThumbnail().getResourceUrl())))
                    .prerequisite(itk.getCourse().getPrerequisite() != null ? ResponsePrerequisitesCourseDTO.builder()
                            .propertyId(itk.getCourse().getPrerequisite().getPropertyId())
                            .name(itk.getCourse().getPrerequisite().getName())
                            .courseLevel(itk.getCourse().getPrerequisite().getCourseLevel())
                            .build() : null)
                    .intakes(responseIntakeDTOS)
                    .latestIntake(
                            ResponseIntakeDTO.builder()
                                                                .propertyId(itk.getPropertyId())
                                                                .name(itk.getName())
                                                                .intakeNumber(itk.getIntakeNumber())
                                                                .availableSeats(itk.getAvailableSeats())
                                                                .intakeStartDate(itk.getIntakeStartDate())
                                                                .intakeEndDate(itk.getIntakeEndDate())
                                                                .activeStatus(itk.isActiveStatus())
                                                                .courseId(itk.getCourse().getPropertyId())
                                                                .courseName(itk.getCourse().getName())
                                                                .isInstalmentEnabled(itk.isInstallmentEnabled())
                                                                .price(itk.getPrice())
                                                                .instructors(instructors)
                                                                .build())
                    .stagesCount(courseStageRepository.countCourseStagesByCourseId(itk.getCourse().getPropertyId()))
                    .lessonsCount(courseStageContentRepository.countCourseStagesContentByCourseId(itk.getCourse().getPropertyId()))
                    .assignmentCount(itk.getCourse().getAssigmentCount())
                    .build();
        } catch (SQLException e) {
            throw new InternalServerException("Error while converting course to DTO: " + e.getMessage());
        }


    }

    @Override
    public List<ResponseCourseDTO> findAll(String searchText) {
        return courseRepository.findAllCourses(searchText).stream().map(this::convertToResponseCourseDto).collect(Collectors.toList());
    }

    @Override
    public PaginatedCourseDTO findAllCoursesByStudentId(String searchText, String studentId) {
        if (studentId.isEmpty()) throw new EntryNotFoundException("Student id not found");

        List<Enrollment> selectedEnrollments = enrollmentRepository.findAllByStudentPropertyIdAndCourseNameContainingIgnoreCase(studentId,searchText);

        List<ResponseCourseDTO> list = new ArrayList<>();
        selectedEnrollments.forEach(item->{
            System.out.println(item.getCourse().getPropertyId());
            Optional<Course> selectedCourse = courseRepository.findById(item.getCourse().getPropertyId());
            list.add(
                    ResponseCourseDTO.builder()
                            .propertyId(selectedCourse.get().getPropertyId())
                            .name(selectedCourse.get().getName())
                            .description(selectedCourse.get().getDescription())
                            .duration(selectedCourse.get().getDuration())
                            .activeStatus(selectedCourse.get().getActiveStatus())
                            .build()
            );
        });
        return PaginatedCourseDTO.builder()
                .count((long) selectedEnrollments.size())
                .dataList(list)
                .build();
    }

    @Override
    public List<ResponseCourseDTO> findLatestCourseslist() {
        return intakeRepository.findIntakesByActiveStatus(true, Sort.by("intakeStartDate").descending()).stream().map(itk ->{


                    List<ResponseIntakeDTO> responseIntakeDTOS = intakeRepository.findAllByCoursePropertyId(itk.getCourse().getPropertyId()).stream().map(
                            intake -> ResponseIntakeDTO.builder()
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
                                    .build()
                    ).toList();



            List<ResponseIntakeHasInstructorDTO> instructors = null;

            instructors = instructorIntakeAssignationRepository
                    .findAllByIntakePropertyId(itk.getPropertyId())
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

            try {
                return ResponseCourseDTO.builder()
                        .propertyId(itk.getCourse().getPropertyId())
                        .name(itk.getCourse().getName())
                        .description(itk.getCourse().getDescription())
                        .duration(itk.getCourse().getDuration())
                        .assignmentCount(itk.getCourse().getAssigmentCount())
                        .activeStatus(itk.getCourse().getActiveStatus())
                        .introVideoUrl(itk.getCourse().getIntroVideoUrl())
                        .createdAt(itk.getCourse().getCreatedAt())
                        .updatedAt(itk.getCourse().getUpdatedAt())
                        .courseLevel(itk.getCourse().getCourseLevel().toString())
                        .courseThumbnail(fileDataHandler.blobToString(new SerialBlob(itk.getCourse().getCourseThumbnail().getResourceUrl())))
                        .prerequisite(itk.getCourse().getPrerequisite() != null ? ResponsePrerequisitesCourseDTO.builder()
                                .propertyId(itk.getCourse().getPrerequisite().getPropertyId())
                                .name(itk.getCourse().getPrerequisite().getName())
                                .courseLevel(itk.getCourse().getPrerequisite().getCourseLevel())
                                .build() : null)
                        .intakes(responseIntakeDTOS)
                        .latestIntake(
                                ResponseIntakeDTO.builder()
                                                                        .propertyId(itk.getPropertyId())
                                                                        .name(itk.getName())
                                                                        .intakeNumber(itk.getIntakeNumber())
                                                                        .availableSeats(itk.getAvailableSeats())
                                                                        .intakeStartDate(itk.getIntakeStartDate())
                                                                        .intakeEndDate(itk.getIntakeEndDate())
                                                                        .activeStatus(itk.isActiveStatus())
                                                                        .courseId(itk.getCourse().getPropertyId())
                                                                        .courseName(itk.getCourse().getName())
                                                                        .isInstalmentEnabled(itk.isInstallmentEnabled())
                                                                        .price(itk.getPrice())
                                                                        .instructors(instructors)
                                                                        .build())
                        .stagesCount(courseStageRepository.countCourseStagesByCourseId(itk.getCourse().getPropertyId()))
                        .lessonsCount(courseStageContentRepository.countCourseStagesContentByCourseId(itk.getCourse().getPropertyId()))
                        .assignmentCount(itk.getCourse().getAssigmentCount())
                        .build();
            } catch (SQLException e) {
                throw new InternalServerException("Error while converting course to DTO: " + e.getMessage());
            }
        }).toList();



    }

    private Course createCourse(RequestCourseDTO dto) {

            return Course.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .duration(dto.getDuration())
                    .assigmentCount(dto.getAssigmentCount())
                    .activeStatus(true)
                    .introVideoUrl(dto.getIntroVideoUrl())
                    .createdAt(Instant.now())
                    .updatedAt(null)
                    .courseLevel(dto.getCourseLevel())
                    .prerequisite(dto.getPrerequisite() != null ? courseRepository.findById(dto.getPrerequisite()).orElseThrow(() -> new EntryNotFoundException("prerequisite course not found")):null)
                    .build();
    }

    private ResponseCourseDTO convertToResponseCourseDto(Course course) {
        try {

            List<ResponseIntakeDTO> responseIntakeDTOS = intakeRepository.findAllByCoursePropertyId(course.getPropertyId()).stream().map(
                    intake -> ResponseIntakeDTO.builder()
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
                            .build()
            ).toList();

            Intake latestIntake = intakeRepository.findTopByCoursePropertyIdOrderByIntakeStartDateDesc(course.getPropertyId()).orElse(null);

            List<ResponseIntakeHasInstructorDTO> instructors = null;

            if (latestIntake != null) {
                instructors = instructorIntakeAssignationRepository
                        .findAllByIntakePropertyId(latestIntake.getPropertyId())
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
            }

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
                    .prerequisite(course.getPrerequisite() != null ? ResponsePrerequisitesCourseDTO.builder()
                            .propertyId(course.getPrerequisite().getPropertyId())
                            .name(course.getPrerequisite().getName())
                            .courseLevel(course.getPrerequisite().getCourseLevel())
                            .build() : null)
//                    .courseStages(course.getCourseStages().stream().map((courseStage) -> ResponseCourseStageDTO.builder()
//                            .propertyId(courseStage.getPropertyId())
//                            .title(courseStage.getTitle())
//                            .description(courseStage.getDescription())
//                            .createdDate(courseStage.getCreatedDate())
//                            .updatedDate(courseStage.getUpdatedDate())
//                            .orderIndex(courseStage.getOrderIndex())
//                            .build()).collect(Collectors.toList()))
                    .intakes(responseIntakeDTOS)
                    .latestIntake(
                            latestIntake != null ? ResponseIntakeDTO.builder()
                            .propertyId(latestIntake.getPropertyId())
                            .name(latestIntake.getName())
                            .intakeNumber(latestIntake.getIntakeNumber())
                            .availableSeats(latestIntake.getAvailableSeats())
                            .intakeStartDate(latestIntake.getIntakeStartDate())
                            .intakeEndDate(latestIntake.getIntakeEndDate())
                            .activeStatus(latestIntake.isActiveStatus())
                            .courseId(latestIntake.getCourse().getPropertyId())
                            .courseName(latestIntake.getCourse().getName())
                            .isInstalmentEnabled(latestIntake.isInstallmentEnabled())
                            .price(latestIntake.getPrice())
                            .instructors(instructors)
                            .build():null)
                    .stagesCount(courseStageRepository.countCourseStagesByCourseId(course.getPropertyId()))
                    .lessonsCount(courseStageContentRepository.countCourseStagesContentByCourseId(course.getPropertyId()))
                    .assignmentCount(course.getAssigmentCount())
                    .build();
        } catch (SQLException e) {
            throw new InternalServerException("Error while converting course to DTO: " + e.getMessage());
        }
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


}
