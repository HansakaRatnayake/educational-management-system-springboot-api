package com.lezord.system_api.service.impl;

import com.lezord.system_api.entity.AssignmentQuestion;
import com.lezord.system_api.entity.AssignmentQuestionRecording;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.AssignmentQuestionAudioRepository;
import com.lezord.system_api.repository.AssignmentQuestionImageRepository;
import com.lezord.system_api.repository.AssignmentQuestionRepository;
import com.lezord.system_api.service.AssignmentQuestionAudioService;
import com.lezord.system_api.util.FileDataHandler;
import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentQuestionAudioServiceImpl implements AssignmentQuestionAudioService {
    private final AssignmentQuestionImageRepository assignmentQuestionImageRepository;
    private final AssignmentQuestionAudioRepository assignmentQuestionAudioRepository;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final FileServiceImpl fileService;
    private final FileDataHandler fileDataHandler;

    @Value("${aws.bucketName}")
    private String bucket;

    @Override
    public void uploadAudio(MultipartFile audio,String questionId) {
        if ((audio == null || audio.isEmpty()) && (audio == null)) {
            throw new EntryNotFoundException("Image must be provided");
        }

        Optional<AssignmentQuestion> selectedQuestion = assignmentQuestionRepository.findById(questionId);
        if (selectedQuestion.isEmpty()) throw new EntryNotFoundException("Question Data Not Found");

        AssignmentQuestionRecording assignmentQuestionRecording = null;

        if (!audio.isEmpty()) {
            UploadedResourceBinaryDataDTO uploadedAudio = fileService.create(audio, bucket, "assignments/question-audios");

            assignmentQuestionRecording = assignmentQuestionAudioRepository.save(
                    AssignmentQuestionRecording.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .createdDate(Instant.now().toString())
                            .hash(fileDataHandler.blobToByteArray(uploadedAudio.getHash()))
                            .directory(uploadedAudio.getDirectory().getBytes())
                            .fileName(fileDataHandler.blobToByteArray(uploadedAudio.getFilename()))
                            .resourceUrl(fileDataHandler.blobToByteArray(uploadedAudio.getResourceUrl()))
                            .build()
            );
        }
        AssignmentQuestion finalQuestion = selectedQuestion.get();
        finalQuestion.setAssignmentQuestionRecording(assignmentQuestionRecording);

        assignmentQuestionRepository.save(finalQuestion);

    }

    @Override
    public void updateAudio(MultipartFile audioFile, String audioId) throws SQLException {
        if (audioFile.isEmpty()) throw new EntryNotFoundException("Audio file is empty");
        if (audioId.isEmpty()) throw new EntryNotFoundException("Audio id not found");

        Optional<AssignmentQuestionRecording> selectedAudioData = assignmentQuestionAudioRepository.findById(audioId);
        if (selectedAudioData.isEmpty()) throw new EntryNotFoundException("Audio data not found and please check your question id");

        AssignmentQuestionRecording audio = selectedAudioData.get();
        String originalDirectory = fileDataHandler.blobToString(new SerialBlob(audio.getDirectory()));
        String originalFileName = fileDataHandler.blobToString(new SerialBlob(audio.getFileName()));

        fileService.delete(originalFileName, bucket, originalDirectory);

        UploadedResourceBinaryDataDTO uploadedResource = fileService.create(audioFile, bucket, originalDirectory);

        audio.setCreatedDate(Instant.now().toString());
        audio.setHash(fileDataHandler.blobToByteArray(uploadedResource.getHash()));
        audio.setFileName(fileDataHandler.blobToByteArray(uploadedResource.getFilename()));
        audio.setDirectory(fileDataHandler.stringToByteArray(uploadedResource.getDirectory()));
        audio.setResourceUrl(fileDataHandler.blobToByteArray(uploadedResource.getResourceUrl()));

        assignmentQuestionAudioRepository.save(audio);
    }
}
