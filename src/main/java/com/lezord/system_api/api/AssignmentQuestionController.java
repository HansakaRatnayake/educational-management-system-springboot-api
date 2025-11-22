package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestAssignmentQuestionDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentQuestionDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionDTO;
import com.lezord.system_api.service.AssignmentQuestionService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/assignment-questions")
@RequiredArgsConstructor
public class AssignmentQuestionController {
    private final AssignmentQuestionService assignmentQuestionService;
    @PostMapping("/{assignmentId}")
    public ResponseEntity<StandardResponseDTO> createAssignmentQuestion(
            @RequestBody RequestAssignmentQuestionDTO dto,
            @PathVariable String assignmentId
            ){
        String question = assignmentQuestionService.createQuestions(dto, assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Assignment Question Create Successfully",
                        question
                ),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<StandardResponseDTO> updateQuestion(
            @RequestBody RequestAssignmentQuestionDTO dto,
            @PathVariable String questionId
    ){
        assignmentQuestionService.updateQuestion(dto,questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question Updated Successfully",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/all")
    public ResponseEntity<StandardResponseDTO> getAllQuestionsByAssignmentId(
            @RequestParam String assignmentId,
            @RequestParam int page,
            @RequestParam int size
    ){
        PaginatedAssignmentQuestionDTO allData = assignmentQuestionService.getAllQuestionsByAssignmentId(
                assignmentId,
                page,
                size
        );
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Assignment Question Data",
                        allData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/all-data/{assignmentId}")
    public ResponseEntity<StandardResponseDTO> getAllQuestionsByAssignmentIdWithoutPagination(
            @PathVariable String assignmentId
    ){
        PaginatedAssignmentQuestionDTO allData = assignmentQuestionService.getAllQuestionsByAssignmentIdWithoutPagination(assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Questions found",
                        allData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<StandardResponseDTO> findByQuestionId(
            @PathVariable String questionId
    ){
        ResponseAssignmentQuestionDTO data = assignmentQuestionService.findById(questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Question data found",
                        data
                ),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<StandardResponseDTO> deleteAssignmentQuestion(
            @PathVariable String questionId
    ){
        assignmentQuestionService.deleteAssignmentQuestion(questionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Question Delete Successfully",
                        null
                ),
                HttpStatus.OK
        );
    }
}
