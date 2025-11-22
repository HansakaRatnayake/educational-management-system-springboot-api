package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestLessonAssignmentTempDTO;
import com.lezord.system_api.dto.response.ResponseAssignmentQuestionTempDTO;
import com.lezord.system_api.dto.response.ResponseLessonAssignmentTempDTO;
import com.lezord.system_api.dto.response.paginate.PaginatedAssignmentQuestionTempOrderIndexDTO;
import com.lezord.system_api.entity.enums.LessonAssignmentTempDidTypes;
import com.lezord.system_api.service.AssignmentQuestionTempService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api/v1/student-assignment-temp-questions")
@RequiredArgsConstructor
public class StudentAssignmentTempQuestionController {
    private final AssignmentQuestionTempService assignmentQuestionTempService;

    @PostMapping
    public ResponseEntity<StandardResponseDTO> createTempQuestions(
            @RequestBody RequestLessonAssignmentTempDTO dto,
            @RequestParam String studentId,
            @RequestParam String assignmentId
    ) {
        assignmentQuestionTempService.createQuestionsTemp(dto, studentId, assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Temp Questions Created",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/assignment-data")
    public ResponseEntity<StandardResponseDTO> getAllAssignmentData(
            @RequestParam String studentId,
            @RequestParam String assignmentId
    ) {
        ResponseLessonAssignmentTempDTO allAssignmentData = assignmentQuestionTempService.getAllAssignmentData(studentId, assignmentId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All assignment data",
                        allAssignmentData
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/one-by-one")
    public ResponseEntity<StandardResponseDTO> getQuestionsOneByOne(
            @RequestParam String assignmentTempId,
            @RequestParam int index
    ) {
        ResponseAssignmentQuestionTempDTO questionsOneByOne = assignmentQuestionTempService.getQuestionsOneByOne(assignmentTempId, index);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Data",
                        questionsOneByOne
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/question-count/{assignmentTempId}")
    public ResponseEntity<StandardResponseDTO> getQuestionCount(
            @PathVariable String assignmentTempId
    ) {
        PaginatedAssignmentQuestionTempOrderIndexDTO questionCount = assignmentQuestionTempService.getQuestionCount(assignmentTempId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Question Count",
                        questionCount
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/current-index/{assignmentTempId}")
    public ResponseEntity<StandardResponseDTO> getCurrentIndex(
            @PathVariable String assignmentTempId
    ) {
        int currentIndex = assignmentQuestionTempService.getCurrentIndex(assignmentTempId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "Current index found",
                        currentIndex
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/all-question-marks/{assignmentTempId}")
    public ResponseEntity<StandardResponseDTO> getAllAssignmentQuestionMarks(
            @PathVariable String assignmentTempId
    ){
        AtomicReference<Double> allData = assignmentQuestionTempService.getAllAssignmentQuestionMarks(assignmentTempId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All Assignment question marks found",
                        allData
                ),
                HttpStatus.OK
        );
    }

    @PatchMapping("/current-time")
    public ResponseEntity<StandardResponseDTO> changeCurrentTime(
            @RequestParam String assignmentTempId,
            @RequestParam Double currentTime
    ) {
        assignmentQuestionTempService.changeCurrentTime(assignmentTempId, currentTime);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Current time changed",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/current-index")
    public ResponseEntity<StandardResponseDTO> changeCurrentIndex(
            @RequestParam String assignmentTempId,
            @RequestParam int currentIndex
    ) {
        assignmentQuestionTempService.changeCurrentIndex(assignmentTempId, currentIndex);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Current time changed",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/filled-status")
    public ResponseEntity<StandardResponseDTO> changeFilledStatus(
            @RequestParam String assignmentTempId,
            @RequestParam int orderIndex,
            @RequestParam LessonAssignmentTempDidTypes type
    ) {
        assignmentQuestionTempService.changeFilledStatus(assignmentTempId, orderIndex, type);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Filled status changed",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/answer-selection/{answerId}")
    public ResponseEntity<StandardResponseDTO> changeAnswerSelection(
            @PathVariable String answerId
    ) {
        assignmentQuestionTempService.changeAnswerSelectState(answerId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Current answer selection",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @PatchMapping("/change-question-marks")
    public ResponseEntity<StandardResponseDTO> changeAssignmentQuestionMarks(
            @RequestParam String questionId,
            @RequestParam Double marks
    ) {
        assignmentQuestionTempService.changeAssignmentQuestionMarks(questionId,marks);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Question marks updated",
                        null
                ),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{assignmentTempId}")
    public ResponseEntity<StandardResponseDTO> deleteTempAssignment(
            @PathVariable String assignmentTempId
    ){
        assignmentQuestionTempService.deleteTempLessonAssignment(assignmentTempId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        200,
                        "All temp assignment data deleted",
                        null
                ),
                HttpStatus.OK
        );
    }

    @PatchMapping("change-bookmark/{assignmentTempQuestionId}")
    public ResponseEntity<StandardResponseDTO> changeBookmarkStatus(
            @PathVariable String assignmentTempQuestionId
    ){
        assignmentQuestionTempService.changeBookmarkStatus(assignmentTempQuestionId);
        return new ResponseEntity<>(
                new StandardResponseDTO(
                        201,
                        "Change Bookmark Status",
                        null
                ),
                HttpStatus.CREATED
        );
    }
}
