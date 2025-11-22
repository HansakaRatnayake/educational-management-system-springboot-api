package com.lezord.system_api.service.impl;

import com.lezord.system_api.entity.AssignmentQuestion;
import com.lezord.system_api.entity.AssignmentQuestionImage;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.AssignmentQuestionImageRepository;
import com.lezord.system_api.repository.AssignmentQuestionRepository;
import com.lezord.system_api.service.AssignmentQuestionImageService;
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
public class AssignmentQuestionImageServiceImpl implements AssignmentQuestionImageService {
    private final AssignmentQuestionImageRepository assignmentQuestionImageRepository;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    private final FileServiceImpl fileService;
    private final FileDataHandler fileDataHandler;

    @Value("${aws.bucketName}")
    private String bucket;
    @Override
    public void uploadImage(MultipartFile image, String questionId) {

        if ((image == null || image.isEmpty()) && (image == null)) {
            throw new EntryNotFoundException("Image must be provided");
        }

        Optional<AssignmentQuestion> selectedQuestion = assignmentQuestionRepository.findById(questionId);
        if (selectedQuestion.isEmpty()) throw new EntryNotFoundException("Question Not Found");

        AssignmentQuestionImage assignmentQuestionImage = null;

        if (!image.isEmpty()) {
            UploadedResourceBinaryDataDTO uploadedImage = fileService.create(image, bucket, "assignments/question-images");

            assignmentQuestionImage = assignmentQuestionImageRepository.save(
                    AssignmentQuestionImage.builder()
                            .propertyId(UUID.randomUUID().toString())
                            .createdDate(Instant.now().toString())
                            .hash(fileDataHandler.blobToByteArray(uploadedImage.getHash()))
                            .directory(uploadedImage.getDirectory().getBytes())
                            .fileName(fileDataHandler.blobToByteArray(uploadedImage.getFilename()))
                            .resourceUrl(fileDataHandler.blobToByteArray(uploadedImage.getResourceUrl()))
                            .build()
            );
        }

        AssignmentQuestion finalQuestion = selectedQuestion.get();
        finalQuestion.setAssignmentQuestionImage(assignmentQuestionImage);

        assignmentQuestionRepository.save(finalQuestion);
    }

    @Override
    public void updateImage(MultipartFile imageFile, String imageId) throws SQLException {
        if (imageFile.isEmpty()) throw new EntryNotFoundException("Image file is empty");
        if (imageId.isEmpty()) throw new EntryNotFoundException("Image id not found");

        Optional<AssignmentQuestionImage> selectedImageData = assignmentQuestionImageRepository.findById(imageId);
        if (selectedImageData.isEmpty()) throw new EntryNotFoundException("Image data not found and please check your question id");

        AssignmentQuestionImage image = selectedImageData.get();
        String originalDirectory = fileDataHandler.blobToString(new SerialBlob(image.getDirectory()));
        String originalFileName = fileDataHandler.blobToString(new SerialBlob(image.getFileName()));

        fileService.delete(originalFileName, bucket, originalDirectory);

        UploadedResourceBinaryDataDTO uploadedResource = fileService.create(imageFile, bucket, originalDirectory);

        image.setCreatedDate(Instant.now().toString());
        image.setHash(fileDataHandler.blobToByteArray(uploadedResource.getHash()));
        image.setFileName(fileDataHandler.blobToByteArray(uploadedResource.getFilename()));
        image.setDirectory(fileDataHandler.stringToByteArray(uploadedResource.getDirectory()));
        image.setResourceUrl(fileDataHandler.blobToByteArray(uploadedResource.getResourceUrl()));

        assignmentQuestionImageRepository.save(image);
    }
}
