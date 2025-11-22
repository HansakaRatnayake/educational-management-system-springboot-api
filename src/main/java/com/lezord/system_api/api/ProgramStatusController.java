package com.lezord.system_api.api;


import com.lezord.system_api.service.impl.StatusServiceImpl;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/statuses")
@RequiredArgsConstructor
public class ProgramStatusController {

    private final StatusServiceImpl statusService;


    @GetMapping("/lessons")
    public ResponseEntity<StandardResponseDTO> getAllProgramStatus(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                        StandardResponseDTO.builder()
                                .code(200)
                                .message("Statuses found")
                                .data(statusService.getAllProgramStatus())
                                .build()
                );
    }

}
