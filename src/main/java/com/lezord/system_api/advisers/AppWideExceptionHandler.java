package com.lezord.system_api.advisers;

import com.lezord.system_api.exception.*;
import com.nozomi.system_api.exception.*;
import com.lezord.system_api.util.StandardResponseDTO;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class AppWideExceptionHandler {

    @ExceptionHandler(EntryNotFoundException.class)
    public ResponseEntity<StandardResponseDTO> handleEntryNorFoundException(EntryNotFoundException exception) {
        return new ResponseEntity<>(
               new StandardResponseDTO(404, exception.getMessage(), exception),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(DuplicateEntryException.class)
    public ResponseEntity<StandardResponseDTO> handleDuplicateEntryException(DuplicateEntryException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(409, exception.getMessage(), exception),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<StandardResponseDTO> handleBadRequestException(BadRequestException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(400, exception.getMessage(), exception),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardResponseDTO> handleUnauthorizedException(UnauthorizedException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(401, exception.getMessage(), exception),
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<StandardResponseDTO> handleEmailServiceException(EmailServiceException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(500, exception.getMessage(), exception),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(AmazonS3ServiceException.class)
    public ResponseEntity<StandardResponseDTO> handleAmazonS3ServiceException(AmazonS3ServiceException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(500, exception.getMessage(), exception),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<StandardResponseDTO> handleInternalServerException(InternalServerException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(500, exception.getMessage(), exception),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<StandardResponseDTO> handleValidationExceptions(MethodArgumentNotValidException exception) {

        List<String> errors = new ArrayList<>();

        // Field-level errors (e.g., @NotNull, @Future)
        errors.addAll(
                exception.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(error -> error.getField() + ": " + error.getDefaultMessage())
                        .toList()
        );

        // Class-level errors (e.g., @ValidIntakeDates)
        errors.addAll(
                exception.getBindingResult()
                        .getGlobalErrors()
                        .stream()
                        .map(error -> error.getObjectName() + ": " + error.getDefaultMessage())
                        .toList()
        );

        return ResponseEntity.badRequest().body(
                StandardResponseDTO.builder()
                        .code(400)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }


    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<StandardResponseDTO> handleConstraintViolationException(ConstraintViolationException exception) {

        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(constraintViolation -> {
                    String path = constraintViolation.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf(".") + 1) : path;
                    return field + ": " + constraintViolation.getMessage();
                })
                .toList();

        return ResponseEntity.badRequest().body(
                StandardResponseDTO.builder()
                        .code(400)
                        .message("Validation failed")
                        .data(errors)
                        .build()
        );
    }

    @ExceptionHandler(InvalidAccessException.class)
    public ResponseEntity<StandardResponseDTO> handleInvalidLoginAccessException(InvalidAccessException exception) {
        return new ResponseEntity<>(
                new StandardResponseDTO(403, exception.getMessage(), exception),
                HttpStatus.FORBIDDEN
        );
    }




}
