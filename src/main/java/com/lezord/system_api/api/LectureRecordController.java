package com.lezord.system_api.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lezord.system_api.dto.request.RequestLectureRecordDTO;
import com.lezord.system_api.service.LectureRecordService;
import com.lezord.system_api.util.StandardResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

@RestController
@RequestMapping("/api/v1/lecture-records")
@RequiredArgsConstructor
public class LectureRecordController {

    private final LectureRecordService lectureRecordService;

    @PostMapping("/trainer/create")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> addRecord(
            @RequestParam("data") String data,
            @RequestParam("thumbnail") MultipartFile thumbnail,
            @RequestParam("video") MultipartFile video
            ) throws SQLException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        RequestLectureRecordDTO dto = objectMapper.readValue(data, RequestLectureRecordDTO.class);
        lectureRecordService.addRecord(dto, thumbnail, video);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        StandardResponseDTO.builder()
                                .code(201)
                                .message("Video Details Saved!")
                                .data(dto.getTitle())
                                .build()


        );
    }

    @GetMapping("/find-lecture-records")
    @PreAuthorize("hasAnyRole('STUDENT','TRAINER','ADMIN')")
    public ResponseEntity<StandardResponseDTO> getLectureRecordData(@RequestParam String intakeId, @RequestParam String lessonId) throws SQLException {

        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "Video Data!", lectureRecordService.getLessonLectureRecordData(intakeId,lessonId)),
                HttpStatus.OK
        );
    }

    @PatchMapping("/change-download-option/{recordId}")
    @PreAuthorize("hasAnyRole('TRAINER','ADMIN')")
    public ResponseEntity<StandardResponseDTO> changeDownloadOption(@PathVariable String recordId) throws SQLException {

        lectureRecordService.changeDownloadOptionStatus(recordId);
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "Video download status changed!", null),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/trainer/load-record/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<InputStreamResource> loadVideo(
            @PathVariable String id,
            @RequestHeader(value = "Range", required = false) String rangeHeader)  {
        try {
            ResponseEntity<InputStreamResource> recordData = lectureRecordService.getRecordData(id, rangeHeader);
            if (recordData == null) {
                return ResponseEntity.notFound().build();
            }
            return recordData;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(path = "/user/download-record/{id}")
    public ResponseEntity<InputStreamResource> downloadRecord(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable String id) throws SQLException, IOException {

        return lectureRecordService.downloadRecord(id);

    }

    @GetMapping(path = "/user/load-latest-records")
    public ResponseEntity<StandardResponseDTO> loadLatestRecords() throws SQLException, IOException {
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "Record Details List!", lectureRecordService.getLatestRecordsForUser()),
                HttpStatus.OK
        );

    }

    @GetMapping(path = "/trainer/video-size/{id}")
    public ResponseEntity<InputStreamResource> getVideoSize(
            @PathVariable String id) throws SQLException, IOException {
        long fileSize = lectureRecordService.getVideoFileSize(id); // Implement this service method
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-File-Size", String.valueOf(fileSize));
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @GetMapping("/user/list/{intakeId}")
    public ResponseEntity<StandardResponseDTO> getAll(
            @RequestParam int page,
            @RequestParam int size,
            @PathVariable String intakeId
    ) {
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "Record Details List!", lectureRecordService.findAll(page, size, intakeId)),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/record-list")
    @PreAuthorize("hasAnyRole('STUDENT','TRAINER','ADMIN')")
    public ResponseEntity<StandardResponseDTO> getAll(
            @RequestParam String lessonId,
            @RequestParam String intakeId
    ) {
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "Record Details List!", lectureRecordService.findAllForStudent(intakeId,lessonId)),
                HttpStatus.OK
        );
    }

    @GetMapping("/user/load-player-data/{intakeId}")
    public ResponseEntity<StandardResponseDTO> loadPlayerData(
            @PathVariable String intakeId,
            @RequestParam String lessonId
    ) {
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "player Details List!",
                        lectureRecordService.loadPlayerData(intakeId, lessonId)),
                HttpStatus.OK
        );
    }

  @GetMapping("/get-video/{recordId}")
    public ResponseEntity<StandardResponseDTO> getVideo(
            @PathVariable String recordId) throws Exception {
        return new ResponseEntity<>(
                new StandardResponseDTO(200,
                        "data!",
                        lectureRecordService.getVideo(recordId)),
                HttpStatus.OK
        );
    }

    @PutMapping("/trainer/update-availability/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> updateFreeAvailability(@RequestParam boolean status, @PathVariable String id) {
        lectureRecordService.updateFreeAvailability(id, status);
        return new ResponseEntity<>(
                new StandardResponseDTO(201,
                        "Record Status changed!", null),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/trainer/delete/{recordId}")
    @PreAuthorize("hasAnyRole('ADMIN','TRAINER')")
    public ResponseEntity<StandardResponseDTO> delete(@PathVariable String recordId) throws SQLException, UnsupportedEncodingException {
        lectureRecordService.delete(recordId);
        return new ResponseEntity<>(
                new StandardResponseDTO(204,
                        "Record was Deleted!", null),
                HttpStatus.NO_CONTENT
        );
    }
}
