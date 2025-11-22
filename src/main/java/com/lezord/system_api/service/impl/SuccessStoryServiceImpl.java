package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestSuccessStoryDTO;
import com.lezord.system_api.dto.response.ResponseSuccessStoryDTO;
import com.lezord.system_api.dto.response.paginate.PaginateSuccessStoryDTO;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.SuccessStory;
import com.lezord.system_api.entity.enums.SuccessStoryStatus;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.ApplicationUserRepository;
import com.lezord.system_api.repository.SuccessStoryRepository;
import com.lezord.system_api.service.SuccessStoryService;
import com.lezord.system_api.util.FileDataHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class SuccessStoryServiceImpl implements SuccessStoryService {

    private final SuccessStoryRepository storyRepo;
    private final ApplicationUserRepository userRepo;
    private final FileDataHandler fileDataHandler;

    @Override
    public void create(@Valid RequestSuccessStoryDTO dto) {
        ApplicationUser user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new EntryNotFoundException("User not found"));

        if (storyRepo.findByApplicationUser_UserId(user.getUserId()).isPresent()) throw new DuplicateEntryException("Already exist a success story from this user");
        SuccessStory story = mapToSuccessStory(dto, user);
        storyRepo.save(story);
    }

    @Override
    public void update(@Valid RequestSuccessStoryDTO dto, String userId) {
        SuccessStory story = storyRepo.findByApplicationUser_UserId(userId)
                .orElseThrow(() -> new EntryNotFoundException("Story not found"));
        story.setRating(dto.getRating());
        story.setStory(dto.getStory());
        story.setTitle(dto.getTitle());
        storyRepo.save(story);
    }

    @Override
    public void delete(String storyId) {
        SuccessStory story = storyRepo.findById(storyId).orElseThrow(() -> new EntryNotFoundException("Story not found"));
        storyRepo.deleteById(story.getPropertyId());
    }

    @Override
    public void changeStatus(SuccessStoryStatus status ,String storyId) {
        SuccessStory story = storyRepo.findById(storyId)
                .orElseThrow(() -> new EntryNotFoundException("Story not found"));

        switch (status) {
            case APPROVED -> {
                story.setStatus(status);
                story.setActiveStatus(true);
            }
            case REJECTED -> {
              storyRepo.deleteById(story.getPropertyId());
            }

            default -> {
                story.setStatus(SuccessStoryStatus.PENDING);
                story.setActiveStatus(false);
            }
        }

        storyRepo.save(story);
    }

    @Override
    public long totalSuccessStory() {
        return storyRepo.countSuccessStoryByActiveStatus(true);
    }

    @Override
    public ResponseSuccessStoryDTO getByUserId(String userId) {
        return storyRepo.findByApplicationUser_UserId(userId)
                .map(this::mapToResponseSuccessStoryDTO)
                .orElseThrow(() -> new EntryNotFoundException("Story not found"));
    }

    @Override
    public PaginateSuccessStoryDTO getPendingStories(String searchText, int page, int size) {
        Page<SuccessStory> successStoryPage = storyRepo.findByStatusOrderByCreatedAtDesc(SuccessStoryStatus.PENDING, searchText, PageRequest.of(page, size));

        return PaginateSuccessStoryDTO.builder()
                .count(successStoryPage.getTotalElements())
                .dataList(successStoryPage.getContent().stream().map(this::mapToResponseSuccessStoryDTO).toList())
                .build();

    }

    @Override
    public PaginateSuccessStoryDTO getAll(String searchText, int page, int size) {
        Page<SuccessStory> pageResult = storyRepo.findByStatusOrderByCreatedAtDesc(SuccessStoryStatus.APPROVED, searchText, PageRequest.of(page, size));
        return PaginateSuccessStoryDTO.builder()
                .count(pageResult.getTotalElements())
                .dataList(pageResult.getContent().stream().map(this::mapToResponseSuccessStoryDTO).collect(Collectors.toList()))
                .build();
    }



    private SuccessStory mapToSuccessStory(RequestSuccessStoryDTO dto, ApplicationUser user) {
        return SuccessStory.builder()
                .title(dto.getTitle())
                .createdAt(Instant.now())
                .rating(dto.getRating())
                .story(dto.getStory())
                .applicationUser(user)
                .activeStatus(true)
                .status(SuccessStoryStatus.PENDING)
                .build();
    }

    private ResponseSuccessStoryDTO mapToResponseSuccessStoryDTO(SuccessStory story) {
        return ResponseSuccessStoryDTO.builder()
                .title(story.getTitle())
                .propertyId(story.getPropertyId())
                .rating(story.getRating())
                .story(story.getStory())
                .status(story.getStatus().toString())
                .activeStatus(story.isActiveStatus())
                .userName(story.getApplicationUser().getFullName())
                .userAvatar(story.getApplicationUser().getApplicationUserAvatar() != null ? fileDataHandler.byteArrayToString(story.getApplicationUser().getApplicationUserAvatar().getResourceUrl()) : null)
                .build();
    }
}
