package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestAssignmentSubQuestionDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentSubQuestionDTO;
import com.lezord.system_api.service.AssignmentSubQuestionService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignment-sub-question")
@RequiredArgsConstructor
public class AssignmentSubQuestionController {
    private final AssignmentSubQuestionService assignmentSubQuestionService;

    @PostMapping("/{questionId}")
    public ResponseEntity<StandardResponseDTO> createSubQuestion(
            @RequestBody RequestAssignmentSubQuestionDTO dto,
            @PathVariable String questionId
            ){
        assignmentSubQuestionService.createSubQuestion(dto,questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Sub question created successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponseDTO> getAllBySubAnswers(
            @RequestParam String questionId,
            @RequestParam int page,
            @RequestParam int size
    ){
        PaginatedAssignmentSubQuestionDTO allData = assignmentSubQuestionService.getAllByQuestionId(questionId, page, size);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Sub Questions Data",
                        allData
                ),
                HttpStatus.OK
        );
    }
}
