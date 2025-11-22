package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestCourseStageContentDTO;
import com.lezord.system_api.dto.request.RequestUpdateCourseStageContentDTO;
import com.lezord.system_api.dto.response.ResponseCourseStageContentDTO;
import com.lezord.system_api.dto.response.paginate.PaginateCourseStageContentDTO;
import com.lezord.system_api.entity.CourseStage;
import com.lezord.system_api.entity.CourseStageContent;
import com.lezord.system_api.entity.enums.ProgramStatus;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.CourseStageContentRepository;
import com.lezord.system_api.repository.CourseStageRepository;
import com.lezord.system_api.service.CourseStageContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CourseStageContentServiceImpl implements CourseStageContentService {

    private final CourseStageContentRepository courseStageContentRepository;
    private final CourseStageRepository courseStageRepository;

    @Override
    public void create(RequestCourseStageContentDTO dto) {
        CourseStage courseStage = courseStageRepository.findById(dto.getCourseStageId())
                .orElseThrow(() -> new EntryNotFoundException("CourseStage not found"));

//        int orderIndex = courseStageContentRepository.findByCourseStagePropertyIdOrderByOrderIndexAsc(courseStage.getPropertyId()).size() + 1;


        courseStageContentRepository.save(CourseStageContent.builder()
                .propertyId(UUID.randomUUID().toString())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .activeStatus(true)
                .createdDate(Instant.now())
                .orderIndex(generateCourseStageContentNumber(courseStage.getPropertyId()))
                .courseStage(courseStage)
                .status(ProgramStatus.PENDING)
                .build()
        );
    }

    @Override
    public void update(RequestUpdateCourseStageContentDTO dto, String propertyId) {
        CourseStageContent content = courseStageContentRepository.findById(propertyId)
                .orElseThrow(() -> new EntryNotFoundException("Content not found"));

        content.setTitle(dto.getTitle());
        content.setDescription(dto.getDescription());
        content.setUpdatedDate(Instant.now());
        if (dto.getStatus() != null) {
            content.setStatus(dto.getStatus());
        }

        courseStageContentRepository.save(content);
    }

    @Override
    public void delete(String courseStageContentId) {
        CourseStageContent selectedCurseStageContent = courseStageContentRepository.findById(courseStageContentId).orElseThrow(() -> new EntryNotFoundException("Course stage content not found"));

        int orderIndex = selectedCurseStageContent.getOrderIndex();
        CourseStage courseStage = selectedCurseStageContent.getCourseStage();

        courseStageContentRepository.delete(selectedCurseStageContent);
        courseStageRepository.flush();

        List<CourseStageContent> updatedCourseStageContentList = courseStageContentRepository.findByCourseStageAndOrderIndexGreaterThanOrderByOrderIndex(courseStage, orderIndex).stream().peek(courseStageContent -> courseStageContent.setOrderIndex(courseStageContent.getOrderIndex() - 1)).toList();

        courseStageContentRepository.saveAll(updatedCourseStageContentList);
    }

    @Override
    public void changeStatus(String propertyId) {
        CourseStageContent content = courseStageContentRepository.findById(propertyId)
                .orElseThrow(() -> new EntryNotFoundException("Content not found"));

        content.setActiveStatus(!content.getActiveStatus());
        courseStageContentRepository.save(content);
    }

    @Override
    public PaginateCourseStageContentDTO getById(String courseStageId, int page, int size) {

        Page<CourseStageContent> intakePage = courseStageContentRepository.findByCourseStagePropertyIdOrderByOrderIndexAsc(courseStageId, PageRequest.of(page, size, Sort.by("orderIndex").ascending()));

        return PaginateCourseStageContentDTO.builder()
                .count(intakePage.getTotalElements())
                .dataList(intakePage.getContent().stream().map(
                        courseStageContent -> ResponseCourseStageContentDTO.builder()
                        .propertyId(courseStageContent.getPropertyId())
                        .title(courseStageContent.getTitle())
                        .description(courseStageContent.getDescription())
                        .activeStatus(courseStageContent.getActiveStatus())
                        .createdDate(courseStageContent.getCreatedDate())
                        .orderIndex(courseStageContent.getOrderIndex())
                        .status(courseStageContent.getStatus().toString())
                        .build()).toList())
                .build();

//        return courseStageContentRepository.findByCourseStagePropertyIdOrderByOrderIndexAsc(courseStageId, PageRequest.of(page, size,Sort.by("intakeStartDate").descending()))
//                .stream()
//                .map(content -> ResponseCourseStageContentDTO.builder()
//                        .propertyId(content.getPropertyId())
//                        .title(content.getTitle())
//                        .description(content.getDescription())
//                        .activeStatus(content.getActiveStatus())
//                        .createdDate(content.getCreatedDate())
//                        .orderIndex(content.getOrderIndex())
//                        .status(content.getStatus().toString())
//                        .build())
//                .collect(Collectors.toList());
    }

    private int generateCourseStageContentNumber(String courseStageId) {
        int last = courseStageContentRepository.getLastCourseStageContentNumber(courseStageId);
        return last + 1;
    }

}
