package com.lezord.system_api.service;


import com.lezord.system_api.dto.request.RequestGetInTouchDTO;
import com.lezord.system_api.dto.response.paginate.PaginateGetInTouchDTO;
import com.lezord.system_api.entity.enums.GetInTouchStatus;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

@Validated
public interface GetInTouchService {

    void create(@Valid RequestGetInTouchDTO dto);
    void delete(String messageId);
    void changeStatus(GetInTouchStatus status, String messageId);

    long totalMessages();
    long unseenMessages();

    PaginateGetInTouchDTO getSeenMessages(String searchText, int page, int size);
    PaginateGetInTouchDTO getUnSeenMessages(String searchText, int page, int size);
    PaginateGetInTouchDTO getAll(String email, int page, int size);

}
