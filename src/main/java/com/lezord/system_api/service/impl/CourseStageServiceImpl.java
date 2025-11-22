package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestCourseStageDTO;
import com.lezord.system_api.dto.response.*;
import com.lezord.system_api.entity.*;
import com.lezord.system_api.repository.*;
import com.nozomi.system_api.dto.response.*;
import com.lezord.system_api.dto.response.paginate.PaginateResponseCourseStageDTO;
import com.nozomi.system_api.entity.*;
import com.lezord.system_api.entity.enums.LessonAssignmentStatusTypes;
import com.lezord.system_api.entity.enums.StudentHasAssignmentTypes;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.nozomi.system_api.repository.*;
import com.lezord.system_api.service.CourseStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseStageServiceImpl implements CourseStageService {

    private final CourseStageRepository courseStageRepository;
    private final CourseRepository courseRepository;
    private final CourseContentTypeRepository courseContentTypeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final StudentHasAssignmentRepository studentHasAssignmentRepository;

    @Override
    public void create(RequestCourseStageDTO requestCourseStageDTO) {
        courseStageRepository.save(createCourseStage(requestCourseStageDTO));
    }

    @Override
    public void update(RequestCourseStageDTO requestCourseStageDTO, String courseStageId) {
        CourseStage courseStage = courseStageRepository.findById(courseStageId)
                .orElseThrow(() -> new EntryNotFoundException("CourseStage not found"));

        Course course = courseRepository.findById(requestCourseStageDTO.getCourseId())
                .orElseThrow(() -> new EntryNotFoundException("Course not found"));

        CourseContentType courseContentType = courseContentTypeRepository.findById(requestCourseStageDTO.getCourseContentType())
                .orElseThrow(() -> new EntryNotFoundException("Course content type not found"));


        courseStage.setTitle(requestCourseStageDTO.getTitle());
        courseStage.setDescription(requestCourseStageDTO.getDescription());
        courseStage.setUpdatedDate(Instant.now());
        courseStage.setCourse(course);
        courseStage.setCourseContentType(courseContentType);

        courseStageRepository.save(courseStage);
    }

    @Override
    public void delete(String courseStageId) {
        CourseStage courseStage = courseStageRepository.findById(courseStageId)
                .orElseThrow(() -> new EntryNotFoundException("CourseStage not found"));

        int orderIndex = courseStage.getOrderIndex();
        Course course = courseStage.getCourse();
        CourseContentType courseContentType = courseStage.getCourseContentType();

        courseStageRepository.delete(courseStage);
        courseStageRepository.flush();

        List<CourseStage> updatedStageList = courseStageRepository.findByCourseAndCourseContentTypeAndOrderIndexGreaterThanOrderByOrderIndex(course, courseContentType, orderIndex)
                .stream()
                .peek(stage -> stage.setOrderIndex(stage.getOrderIndex() - 1)).toList();

        courseStageRepository.saveAll(updatedStageList);


    }

    @Override
    public List<ResponseClientCourseStageTypeDTO> getAllStagesWithData(String courseId) {
        if (courseId.isEmpty()) throw new EntryNotFoundException("Course id not found");

        List<CourseContentType> allTypes = courseContentTypeRepository.findAll();

        List<ResponseClientCourseStageTypeDTO> responseClientCourseStageTypeDTOS = new ArrayList<>();

        for (CourseContentType s : allTypes) {
            List<CourseStage> allData
                    = courseStageRepository.findAllByCoursePropertyIdAndCourseContentTypePropertyIdAndActiveStatusOrderByOrderIndexAsc(courseId, s.getPropertyId(), Boolean.TRUE);

            responseClientCourseStageTypeDTOS.add(
                    ResponseClientCourseStageTypeDTO.builder()
                            .contentType(s.getType())
                            .stages(convertToResponseCourseStageDTO(allData))
                            .build()
            );
        }
        return responseClientCourseStageTypeDTOS;
    }

    @Override
    public List<ResponseAuthenticatedCourseStageTypeDTO> getAllStagesWithDataForAuthenticated(String courseId, String studentId, String intakeId) {
        if (courseId.isEmpty()) throw new EntryNotFoundException("Course id not found");

        List<CourseContentType> allTypes = courseContentTypeRepository.findAll();

        List<ResponseAuthenticatedCourseStageTypeDTO> responseClientCourseStageTypeDTOS = new ArrayList<>();

        for (CourseContentType s : allTypes) {
            List<CourseStage> allData
                    = courseStageRepository.findAllByCoursePropertyIdAndCourseContentTypePropertyIdAndActiveStatusOrderByOrderIndexAsc(courseId, s.getPropertyId(), Boolean.TRUE);

            responseClientCourseStageTypeDTOS.add(
                    ResponseAuthenticatedCourseStageTypeDTO.builder()
                            .contentType(s.getType())
                            .remainingAssignmentCount(setRemainingAssignmentsCount(s, studentId, intakeId))
                            .stages(convertToResponseCourseStageAuthenticatedDTO(allData))
                            .build()
            );
        }
        return responseClientCourseStageTypeDTOS;
    }

    private AtomicInteger setRemainingAssignmentsCount(CourseContentType s, String studentId, String intakeId) {
        AtomicInteger count = new AtomicInteger();
        if (studentId != null && intakeId != null) {
            Optional<Enrollment> selectedEnrolment
                    = enrollmentRepository.getEnrollmentByStudentAndIntake(studentId, intakeId);
            if (selectedEnrolment.isPresent()) {
                List<LessonAssignment> allAssignments
                        = lessonAssignmentRepository.findAllByLessonCourseStageCourseContentTypePropertyIdAndIntakePropertyId(s.getPropertyId(), selectedEnrolment.get().getIntake().getPropertyId());
                allAssignments.forEach(item -> {
                    if (item.getStatusType() == LessonAssignmentStatusTypes.ACTIVATED) {
                        Optional<StudentHasAssignment> selectedStudentAssignment =
                                studentHasAssignmentRepository.findFirstByStudentPropertyIdAndAssignmentPropertyIdOrderByCreatedAtDesc(studentId, item.getPropertyId());
                        if (selectedStudentAssignment.isEmpty()) {
                            count.addAndGet(1);
                        } else {
                            StudentHasAssignment assignment = selectedStudentAssignment.get();
                            if (assignment.getStatusType() != StudentHasAssignmentTypes.COMPLETED) {
                                count.addAndGet(1);
                            }
                        }
                    }
                });
            }
        }
        return count;
    }

    private List<ResponseClientCourseStageDTO> convertToResponseCourseStageDTO(List<CourseStage> allData) {
        List<ResponseClientCourseStageDTO> list = new ArrayList<>();
        for (CourseStage s : allData) {
            list.add(
                    ResponseClientCourseStageDTO.builder()
                            .title(s.getTitle())
                            .description(s.getDescription())
                            .orderIndex(s.getOrderIndex())
                            .lessons(convertToResponseCourseStageLesson(s.getContentHashSet()))
                            .build()
            );
        }
        return list;
    }

    private List<ResponseAuthnticatedCourseStageDTO> convertToResponseCourseStageAuthenticatedDTO(List<CourseStage> allData) {
        List<ResponseAuthnticatedCourseStageDTO> list = new ArrayList<>();
        for (CourseStage s : allData) {
            list.add(
                    ResponseAuthnticatedCourseStageDTO.builder()
                            .title(s.getTitle())
                            .description(s.getDescription())
                            .orderIndex(s.getOrderIndex())
                            .lessons(convertToResponseCourseStageAuthenticatedLesson(s.getContentHashSet()))
                            .build()
            );
        }
        return list;
    }

    private List<ResponseClientCourseStageLessonDTO> convertToResponseCourseStageLesson(Set<CourseStageContent> contentHashSet) {
        List<ResponseClientCourseStageLessonDTO> list = new ArrayList<>();
        for (CourseStageContent c : contentHashSet) {
            if (c.getActiveStatus()) {
                list.add(
                        ResponseClientCourseStageLessonDTO.builder()
                                .propertyId(c.getPropertyId())
                                .title(c.getTitle())
                                .description(c.getDescription())
                                .orderIndex(c.getOrderIndex())
                                .build()
                );
            }
        }
        list.sort(Comparator.comparing(ResponseClientCourseStageLessonDTO::getOrderIndex));
        return list;
    }

    private List<ResponseAuthenticatedCourseStageLessonDTO> convertToResponseCourseStageAuthenticatedLesson(Set<CourseStageContent> contentHashSet) {
        List<ResponseAuthenticatedCourseStageLessonDTO> list = new ArrayList<>();
        for (CourseStageContent c : contentHashSet) {
            if (c.getActiveStatus()) {
                list.add(
                        ResponseAuthenticatedCourseStageLessonDTO.builder()
                                .propertyId(c.getPropertyId())
                                .title(c.getTitle())
                                .description(c.getDescription())
                                .orderIndex(c.getOrderIndex())
                                .build()
                );
            }
        }
        list.sort(Comparator.comparing(ResponseAuthenticatedCourseStageLessonDTO::getOrderIndex));
        return list;
    }

    @Override
    public PaginateResponseCourseStageDTO findAllByCourseAndStageContentType(String courseId, String courseContentTypeId, int pageNo, int pageSize) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntryNotFoundException("Course not found"));

        CourseContentType courseContentType = courseContentTypeRepository.findById(courseContentTypeId)
                .orElseThrow(() -> new EntryNotFoundException("Course content type not found"));

        Page<CourseStage> page = courseStageRepository.findAllByCourseAndCourseContentType(
                course, courseContentType, PageRequest.of(pageNo, pageSize, Sort.by("orderIndex").ascending())
        );

        return PaginateResponseCourseStageDTO.builder()
                .count(page.getTotalElements())
                .dataList(page.getContent().stream().map(courseStage -> ResponseCourseStageDTO.builder()
                        .propertyId(courseStage.getPropertyId())
                        .title(courseStage.getTitle())
                        .description(courseStage.getDescription())
                        .createdDate(courseStage.getCreatedDate())
                        .updatedDate(courseStage.getUpdatedDate())
                        .orderIndex(courseStage.getOrderIndex())
                        .build()).collect(Collectors.toList()))
                .build();
    }


    private int generateOrderIndex(Course course, CourseContentType courseContentType) {
        return (int) (courseStageRepository.countCourseStageByCourseAndCourseContentType(course, courseContentType) + 1);
    }

    private CourseStage createCourseStage(RequestCourseStageDTO requestCourseStageDTO) {
        Course course = courseRepository.findById(requestCourseStageDTO.getCourseId())
                .orElseThrow(() -> new EntryNotFoundException("Course not found"));

        CourseContentType courseContentType = courseContentTypeRepository.findById(requestCourseStageDTO.getCourseContentType())
                .orElseThrow(() -> new EntryNotFoundException("Course content type not found"));

        return CourseStage.builder()
                .propertyId(UUID.randomUUID().toString())
                .title(requestCourseStageDTO.getTitle())
                .description(requestCourseStageDTO.getDescription())
                .activeStatus(true)
                .createdDate(Instant.now())
                .orderIndex(generateOrderIndex(course, courseContentType))
                .course(course)
                .courseContentType(courseContentType)
                .build();
    }
}
