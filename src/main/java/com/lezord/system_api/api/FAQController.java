package com.lezord.system_api.api;

import com.lezord.system_api.dto.request.RequestFAQDTO;
import com.lezord.system_api.dto.response.ResponseFAQDTO;
import com.lezord.system_api.service.FAQService;
import com.lezord.system_api.util.StandardResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/faqs")
public class FAQController {

    private final FAQService faqService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> createFAQ(@Valid @RequestBody RequestFAQDTO dto) {
        faqService.create(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("FAQ created")
                                .data(null)
                                .build()
                );
    }

    @PutMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> updateFAQ(@Valid @RequestBody RequestFAQDTO dto, @PathVariable String faqId) {
        faqService.update(dto, faqId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("FAQ updated")
                                .data(null)
                                .build()
                );
    }

    @DeleteMapping("/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> deleteFAQ(@Valid @PathVariable String faqId) {
        faqService.delete(faqId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .body(
                        StandardResponseDTO.builder()
                                .code(204)
                                .message("FAQ deleted")
                                .data(null)
                                .build()
                );
    }

    @PatchMapping("/change-status/{faqId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeFAQStatus(@PathVariable String faqId) {
        faqService.changeStatus(faqId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("FAQ status changed")
                                .data(null)
                                .build()
                );
    }

    @GetMapping("/{faqId}")
    public ResponseEntity<StandardResponseDTO> getFAQById(@PathVariable String faqId) {
        ResponseFAQDTO result = faqService.findById(faqId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("FAQ found")
                                .data(result)
                                .build()
                );
    }

    @GetMapping
    public ResponseEntity<StandardResponseDTO> getAllFAQs(
            @RequestParam(defaultValue = "") String searchText,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("FAQs found")
                                .data(faqService.findAll(searchText, page, size))
                                .build()
                );
    }
}
