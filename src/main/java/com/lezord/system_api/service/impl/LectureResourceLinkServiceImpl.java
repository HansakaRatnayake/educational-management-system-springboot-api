package com.lezord.system_api.service.impl;

import com.lezord.system_api.dto.request.RequestLectureResourceLinkDTO;
import com.lezord.system_api.entity.LectureRecord;
import com.lezord.system_api.entity.LectureResourceLink;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.LectureRecordRepository;
import com.lezord.system_api.repository.LectureResourceLinkRepo;
import com.lezord.system_api.service.LectureResourceLinkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class LectureResourceLinkServiceImpl implements LectureResourceLinkService {
    private final LectureResourceLinkRepo lectureResourceLinkRepo;
    private final LectureRecordRepository lectureRecordRepository;

    @Override
    public void create(RequestLectureResourceLinkDTO dto, String recordId) throws SQLException {
        Optional<LectureRecord> selectedRecord = lectureRecordRepository.findById(recordId);

        if (selectedRecord.isEmpty()) {
            throw new EntryNotFoundException("Lecture record not found");
        }

        lectureResourceLinkRepo.save(LectureResourceLink.builder()
                .propertyId(UUID.randomUUID().toString())
                .activeState(true)
                .resourceUrl(new SerialBlob(dto.getResourceUrl().getBytes()))
                .resourceDate(Instant.now())
                .lectureRecord(selectedRecord.get())
                .build());
    }

    @Override
    public void delete(String resourceId) {
        Optional<LectureResourceLink> lectureResourceLink = lectureResourceLinkRepo.findById(resourceId);

        if (lectureResourceLink.isEmpty()) {
            throw new EntryNotFoundException("Lecture Resource Links not found");
        }
        lectureResourceLinkRepo.delete(lectureResourceLink.get());
    }
}
