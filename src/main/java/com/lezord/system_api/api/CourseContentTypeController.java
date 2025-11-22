package com.lezord.system_api.api;

import com.lezord.system_api.dto.response.ResponseCourseContentTypeDTO;
import com.lezord.system_api.service.CourseContentTypeService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/course-content-types")
@RequiredArgsConstructor
public class CourseContentTypeController {

    private final CourseContentTypeService courseContentTypeService;


    @GetMapping
    public ResponseEntity<StandardResponseDTO> findAll() {
        List<ResponseCourseContentTypeDTO> allContentTypes = courseContentTypeService.findAll();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Course content types fetched successfully")
                                .data(allContentTypes)
                                .build()

                );

    }
}
