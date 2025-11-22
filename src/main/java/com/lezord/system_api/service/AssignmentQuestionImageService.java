package com.lezord.system_api.service;


import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface AssignmentQuestionImageService {
    void uploadImage(MultipartFile image, String questionId);
    void updateImage(MultipartFile audio, String imageId) throws SQLException;
}
