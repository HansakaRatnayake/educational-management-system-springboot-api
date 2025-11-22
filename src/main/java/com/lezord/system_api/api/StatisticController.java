package com.lezord.system_api.api;

import com.lezord.system_api.service.StatisticService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/statistics")
public class StatisticController {

    private final StatisticService statisticService;

    @GetMapping("/visitors/institution-details")
    public ResponseEntity<StandardResponseDTO> getInstitutionDetails() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Statistics found")
                                .data(statisticService.getBasicStatistics())
                                .build()
                );
    }

}
