package com.lezord.system_api.api;

import com.lezord.system_api.service.PurchaseDetailService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/purchase-details")
public class PurchaseDetailController {

    private final PurchaseDetailService purchaseDetailService;

    @GetMapping("/{studentId}")
    public ResponseEntity<StandardResponseDTO> getPurchaseDetails(
            @PathVariable String studentId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Purchase detail found")
                                .data(purchaseDetailService.getPurchaseDetails(studentId))
                                .build()
                );
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<StandardResponseDTO> getAllPendingSlipVerifiedData(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String searchText
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Purchase detail found")
                                .data(purchaseDetailService.getAllPendingSlipVerifiedData(searchText, page, size))
                                .build()
                );
    }
}
