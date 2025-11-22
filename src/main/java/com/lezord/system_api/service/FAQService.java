package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestFAQDTO;
import com.lezord.system_api.dto.response.ResponseFAQDTO;

import java.util.List;

public interface FAQService {

    void create(RequestFAQDTO dto);

    void update(RequestFAQDTO dto, String faqId);

    void delete(String faqId);

    void changeStatus(String faqId);

    ResponseFAQDTO findById(String faqId);

    List<ResponseFAQDTO> findAll(String searchText, int page, int size);
}
