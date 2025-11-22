package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestPendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.dto.response.ResponsePendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.dto.response.paginate.PaginateResponsePendingInstructorRegistrationDetailDTO;
import com.lezord.system_api.entity.PendingInstructorRegistrationDetail;
import com.lezord.system_api.exception.DuplicateEntryException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.PendingInstructorRegistrationDetailRepository;
import com.lezord.system_api.service.PendingInstructorRegistrationDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PendingInstructorRegistrationDetailServiceImpl implements PendingInstructorRegistrationDetailService {

    private final PendingInstructorRegistrationDetailRepository pendingInstructorRegistrationDetailRepository;

    @Override
    public void create(RequestPendingInstructorRegistrationDetailDTO requestPendingInstructorRegistrationDetailDTO) {
        if (pendingInstructorRegistrationDetailRepository.findByUsername(requestPendingInstructorRegistrationDetailDTO.getUsername()).isPresent())
            throw new DuplicateEntryException(String.format("Registration request from Username %s already exists", requestPendingInstructorRegistrationDetailDTO.getUsername()));
        pendingInstructorRegistrationDetailRepository.save(createPendingInstructorRegistrationDetail(requestPendingInstructorRegistrationDetailDTO));
    }

    @Override
    public void delete(String pendingInstructorRegistrationId) {
        if (pendingInstructorRegistrationDetailRepository.findById(pendingInstructorRegistrationId).isPresent()) pendingInstructorRegistrationDetailRepository.deleteById(pendingInstructorRegistrationId);
        else throw new EntryNotFoundException(String.format("PendingInstructor Registration Id %s does not exist", pendingInstructorRegistrationId));
    }

    @Override
    public long count() {
        return pendingInstructorRegistrationDetailRepository.count();
    }

    @Override
    public PaginateResponsePendingInstructorRegistrationDetailDTO findAll(String searchText, Integer pageNum, Integer pageSize) {
        return PaginateResponsePendingInstructorRegistrationDetailDTO.builder()
                .dataList(pendingInstructorRegistrationDetailRepository.searchPendingInstructorRegistrationDetail(searchText, PageRequest.of(pageNum,pageSize))
                        .stream()
                        .map(this::covertToResponsePendingInstructorRegistrationDetailDTO).toList()
                )
                .count(pendingInstructorRegistrationDetailRepository.searchCountPendingInstructorRegistrationDetail(searchText))
                .build();
    }

    @Override
    public ResponsePendingInstructorRegistrationDetailDTO findById(String pendingInstructorRegistrationId) {
        PendingInstructorRegistrationDetail pendingInstructorRegistrationDetail = pendingInstructorRegistrationDetailRepository.findById(pendingInstructorRegistrationId).orElseThrow(() -> new EntryNotFoundException(String.format("PendingInstructor Registration Id %s does not exist", pendingInstructorRegistrationId)));
        return covertToResponsePendingInstructorRegistrationDetailDTO(pendingInstructorRegistrationDetail);
    }

    private PendingInstructorRegistrationDetail createPendingInstructorRegistrationDetail(RequestPendingInstructorRegistrationDetailDTO pendingInstructorRegistrationDetailDTO) {
        return PendingInstructorRegistrationDetail.builder()
                .propertyId(UUID.randomUUID().toString())
                .username(pendingInstructorRegistrationDetailDTO.getUsername())
                .fullName(pendingInstructorRegistrationDetailDTO.getFullName())
                .password(pendingInstructorRegistrationDetailDTO.getPassword())
                .countryCode(pendingInstructorRegistrationDetailDTO.getCountryCode())
                .phoneNumber(pendingInstructorRegistrationDetailDTO.getPhoneNumber())
                .requestDate(Instant.now())
                .build();
    }

    private ResponsePendingInstructorRegistrationDetailDTO covertToResponsePendingInstructorRegistrationDetailDTO(PendingInstructorRegistrationDetail pendingInstructorRegistrationDetail) {
        return ResponsePendingInstructorRegistrationDetailDTO.builder()
                .propertyId(pendingInstructorRegistrationDetail.getPropertyId())
                .username(pendingInstructorRegistrationDetail.getUsername())
                .fullName(pendingInstructorRegistrationDetail.getFullName())
                .phoneNumber(pendingInstructorRegistrationDetail.getPhoneNumber())
                .countryCode(pendingInstructorRegistrationDetail.getCountryCode())
                .requestDate(pendingInstructorRegistrationDetail.getRequestDate())
                .build();
    }
}
