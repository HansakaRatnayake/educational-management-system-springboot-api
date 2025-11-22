package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestLectureResourceLinkDTO;

import java.sql.SQLException;

public interface LectureResourceLinkService {

    void create(RequestLectureResourceLinkDTO dto, String recordId) throws SQLException;

    void delete(String resourceId);
}
