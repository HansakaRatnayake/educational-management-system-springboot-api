package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestGetInTouchDTO;
import com.lezord.system_api.dto.response.ResponseGetInTouchDTO;
import com.lezord.system_api.dto.response.paginate.PaginateGetInTouchDTO;
import com.lezord.system_api.entity.GetInTouch;
import com.lezord.system_api.entity.enums.GetInTouchStatus;
import com.lezord.system_api.repository.GetInTouchRepository;
import com.lezord.system_api.service.GetInTouchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GetInTouchServiceImpl implements GetInTouchService {

    private final GetInTouchRepository getInTouchRepository;

    @Override
    public void create(RequestGetInTouchDTO dto) {
        GetInTouch getInTouch = GetInTouch.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .message(dto.getMessage())
                .phone(dto.getPhone())
                .activeStatus(true)
                .status(GetInTouchStatus.UNSEEN)
                .createdAt(Instant.now())
                .build();

        getInTouchRepository.save(getInTouch);
    }

    @Override
    public void delete(String messageId) {
        GetInTouch getInTouch = getInTouchRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        getInTouchRepository.deleteById(messageId);
    }

    @Override
    public void changeStatus(GetInTouchStatus status, String messageId) {
        GetInTouch getInTouch = getInTouchRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        getInTouch.setStatus(status);
        getInTouchRepository.save(getInTouch);
    }

    @Override
    public long totalMessages() {
        return getInTouchRepository.count();
    }

    @Override
    public long unseenMessages() {
        return getInTouchRepository.countByStatus(GetInTouchStatus.UNSEEN);
    }

    @Override
    public PaginateGetInTouchDTO getSeenMessages(String searchText, int page, int size) {
        return getPaginatedMessages(GetInTouchStatus.SEEN, page, size);
    }

    @Override
    public PaginateGetInTouchDTO getUnSeenMessages(String searchText, int page, int size) {
        return getPaginatedMessages(GetInTouchStatus.UNSEEN, page, size);
    }

    @Override
    public PaginateGetInTouchDTO getAll(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GetInTouch> pageResult = getInTouchRepository.findGetInTouchByEmail(email,pageable);
        return buildPaginateDTO(pageResult);
    }

    private PaginateGetInTouchDTO getPaginatedMessages(GetInTouchStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<GetInTouch> pageResult = getInTouchRepository
                .findGetInTouchByStatus(status, pageable);
        return buildPaginateDTO(pageResult);
    }

    private PaginateGetInTouchDTO buildPaginateDTO(Page<GetInTouch> page) {
        List<ResponseGetInTouchDTO> dataList = page.getContent().stream().map(getInTouch ->
                ResponseGetInTouchDTO.builder()
                        .propertyId(getInTouch.getPropertyId())
                        .fullName(getInTouch.getFullName())
                        .email(getInTouch.getEmail())
                        .message(getInTouch.getMessage())
                        .phone(getInTouch.getPhone())
                        .activeStatus(getInTouch.isActiveStatus())
                        .status(getInTouch.getStatus())
                        .createdAt(getInTouch.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());

        return PaginateGetInTouchDTO.builder()
                .count(page.getTotalElements())
                .dataList(dataList)
                .build();
    }

}
