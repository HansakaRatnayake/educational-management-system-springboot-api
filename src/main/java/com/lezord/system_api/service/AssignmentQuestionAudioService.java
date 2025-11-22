package com.lezord.system_api.service;

import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface AssignmentQuestionAudioService {
    void uploadAudio(MultipartFile audio,String questionId);
    void updateAudio(MultipartFile audio, String audioId) throws SQLException;
}
