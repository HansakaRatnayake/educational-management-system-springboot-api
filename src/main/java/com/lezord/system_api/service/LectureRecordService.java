package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestLectureRecordDTO;
import com.lezord.system_api.dto.response.ResponseLectureRecordDetailsDTO;
import com.lezord.system_api.dto.response.ResponseRecordPlayDto;
import com.lezord.system_api.dto.response.paginate.PaginatedResponseLectureRecordDetailsDTO;
import com.lezord.system_api.dto.response.paginate.PlayerDataResponseDto;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

public interface LectureRecordService {

    void addRecord(RequestLectureRecordDTO dto, MultipartFile thumbnail, MultipartFile video) throws SQLException;

    List<ResponseRecordPlayDto> getLessonLectureRecordData(String intakeId, String lessonId) throws SQLException;

    ResponseEntity<InputStreamResource> getRecordData(String id, String range) throws SQLException, IOException;

    ResponseEntity<InputStreamResource> downloadRecord(String id) throws SQLException, IOException;

    long getVideoFileSize(String id) throws SQLException, IOException;

    PaginatedResponseLectureRecordDetailsDTO getLatestRecordsForUser() throws SQLException, IOException;

    PaginatedResponseLectureRecordDetailsDTO findAll(int page, int size, String intakeId);

    void updateFreeAvailability(String id, boolean status);

    void delete(String recordId) throws SQLException, UnsupportedEncodingException;

    List<ResponseLectureRecordDetailsDTO> findAllForStudent(String intakeId, String lessonId);

    PlayerDataResponseDto loadPlayerData(String intakeId, String contentId);

    ResponseRecordPlayDto getVideo(String recordId) throws Exception;

    void changeDownloadOptionStatus(String recordId);
}
