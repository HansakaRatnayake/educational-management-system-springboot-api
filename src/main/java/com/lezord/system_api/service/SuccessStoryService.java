package com.lezord.system_api.service;

import com.lezord.system_api.dto.request.RequestSuccessStoryDTO;
import com.lezord.system_api.dto.response.ResponseSuccessStoryDTO;
import com.lezord.system_api.dto.response.paginate.PaginateSuccessStoryDTO;
import com.lezord.system_api.entity.enums.SuccessStoryStatus;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;


@Validated
public interface SuccessStoryService {

    void create(@Valid RequestSuccessStoryDTO dto);
    void update(@Valid RequestSuccessStoryDTO dto, String userId);
    void delete(String storyId);
    void changeStatus(SuccessStoryStatus status, String storyId);

    long totalSuccessStory();

    ResponseSuccessStoryDTO getByUserId(String userId);
    PaginateSuccessStoryDTO getPendingStories(String searchText, int page, int size);
    PaginateSuccessStoryDTO getAll(String userId, int page, int size);

}
