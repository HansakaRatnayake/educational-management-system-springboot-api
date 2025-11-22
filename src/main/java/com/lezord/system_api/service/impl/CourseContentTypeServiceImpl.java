package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.response.ResponseCourseContentTypeDTO;
import com.lezord.system_api.entity.CourseContentType;
import com.lezord.system_api.repository.CourseContentTypeRepository;
import com.lezord.system_api.service.CourseContentTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lezord.system_api.entity.enums.Specialization.GRAMMAR;
import static com.lezord.system_api.entity.enums.Specialization.KANJI;
import static com.lezord.system_api.entity.enums.Specialization.READING;
import static com.lezord.system_api.entity.enums.Specialization.LISTENING;
import static com.lezord.system_api.entity.enums.Specialization.VOCABULARY;


@Service
@RequiredArgsConstructor
public class CourseContentTypeServiceImpl implements CourseContentTypeService {

    private final CourseContentTypeRepository courseContentTypeRepository;


    @Override
    public void initializeCourseContentType() {
        List<CourseContentType> courseContentTypes = courseContentTypeRepository.findAll();
        if(courseContentTypes.isEmpty()){

            CourseContentType grammar = CourseContentType.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .type(GRAMMAR.toString())
                    .build();

            CourseContentType kanji = CourseContentType.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .type(KANJI.toString())
                    .build();

            CourseContentType reading = CourseContentType.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .type(READING.toString())
                    .build();

            CourseContentType listening = CourseContentType.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .type(LISTENING.toString())
                    .build();

            CourseContentType vocabulary = CourseContentType.builder()
                    .propertyId(UUID.randomUUID().toString())
                    .type(VOCABULARY.toString())
                    .build();

            courseContentTypeRepository.saveAll(List.of(grammar, kanji, reading, listening, vocabulary));
        }
    }

    @Override
    public List<ResponseCourseContentTypeDTO> findAll() {
        return courseContentTypeRepository.findAll().stream().map(
                courseContentType -> ResponseCourseContentTypeDTO.builder()
                        .propertyId(courseContentType.getPropertyId())
                        .courseContentType(courseContentType.getType())
                        .build()
        ).collect(Collectors.toList());
    }
}
