package com.lezord.system_api.service.impl;

import com.amazonaws.services.dlm.model.InternalServerException;
import com.lezord.system_api.dto.response.ResponseApplicationUserAvatarDTO;
import com.lezord.system_api.entity.ApplicationUser;
import com.lezord.system_api.entity.ApplicationUserAvatar;
import com.lezord.system_api.exception.BadRequestException;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.ApplicationUserAvatarRepository;
import com.lezord.system_api.repository.ApplicationUserRepository;
import com.lezord.system_api.service.ApplicationUserAvatarService;
import com.lezord.system_api.service.FileService;
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
public class ApplicationUserAvatarServiceImpl implements ApplicationUserAvatarService {

    @Value("${aws.bucketName}")
    private String bucket;

    private final ApplicationUserAvatarRepository applicationUserAvatarRepository;
    private final ApplicationUserRepository applicationUserRepository;
    private final FileService fileService;
    private final FileDataHandler fileDataHandler;

    @Override
    public void create(MultipartFile file, String userId) {
        if (file.isEmpty()) throw new BadRequestException("File is empty");
        ApplicationUser selectedapplicationUser = applicationUserRepository.findById(userId).orElseThrow(() -> new EntryNotFoundException("User not found"));

        UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = fileService.create(file, bucket, "users/avatar");

        applicationUserAvatarRepository.save(
                ApplicationUserAvatar.builder()
                        .propertyId(UUID.randomUUID().toString())
                        .createdAt(Instant.now())
                        .applicationUser(selectedapplicationUser)
                        .hash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()))
                        .directory(uploadedResourceBinaryDataDTO.getDirectory().getBytes())
                        .fileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()))
                        .resourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()))
                        .build()
        );

    }

    @Override
    public void delete(String userAvatarId) {

        ApplicationUserAvatar selectedapplicationUserAvatar = applicationUserAvatarRepository.findApplicationUserAvatarByApplicationUserUserId(userAvatarId).orElseThrow(() -> new EntryNotFoundException("User not found"));

        try {
            fileService.delete(
                    fileDataHandler.blobToString(new SerialBlob(selectedapplicationUserAvatar.getFileName())),
                    bucket,
                    fileDataHandler.blobToString(new SerialBlob(selectedapplicationUserAvatar.getDirectory()))
            );
            ApplicationUser applicationUser = applicationUserRepository.findById(selectedapplicationUserAvatar.getApplicationUser().getUserId()).orElseThrow(() -> new EntryNotFoundException("User not found"));
            applicationUser.setApplicationUserAvatar(null);
            applicationUserRepository.save(applicationUser);
        }catch (SQLException e){
            throw new InternalServerException("Error while deleting an avatar");
        }


    }

    @Override
    public void update(MultipartFile file, String userId) {
        if (file.isEmpty()) throw new EntryNotFoundException("File is empty");
        ApplicationUser selectedApplicationuser = applicationUserRepository.findById(userId).orElseThrow(() -> new EntryNotFoundException("User %s not found"));
        Optional<ApplicationUserAvatar> selectedApplicationUserAvatar = applicationUserAvatarRepository.findApplicationUserAvatarByApplicationUserUserId(userId);

        try {
            if(selectedApplicationUserAvatar.isPresent()) {
                fileService.delete(
                        fileDataHandler.blobToString(new SerialBlob(selectedApplicationUserAvatar.get().getFileName())),
                        bucket,
                        fileDataHandler.blobToString(new SerialBlob(selectedApplicationUserAvatar.get().getDirectory()))
                );
                UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = fileService.create(file, bucket, "users/avatar");
                selectedApplicationUserAvatar.get().setHash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()));
                selectedApplicationUserAvatar.get().setDirectory(uploadedResourceBinaryDataDTO.getDirectory().getBytes());
                selectedApplicationUserAvatar.get().setFileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()));
                selectedApplicationUserAvatar.get().setResourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()));
                selectedApplicationUserAvatar.get().setUpdatedAt(Instant.now());

                applicationUserAvatarRepository.save(selectedApplicationUserAvatar.get());
            }else {
                create(file, userId);
            }
        }catch (SQLException e){
            throw new InternalServerException("Error while update an avatar");
        }
    }

    @Override
    public ResponseApplicationUserAvatarDTO findByUserId(String userId) {

        try {
            Optional<ApplicationUserAvatar> applicationUserAvatar = applicationUserAvatarRepository.findApplicationUserAvatarByApplicationUserUserId(userId);
            return ResponseApplicationUserAvatarDTO.builder()
                    .propertyId(applicationUserAvatar.map(ApplicationUserAvatar::getPropertyId).orElse(null))
                    .resourceUrl(applicationUserAvatar.isPresent() ? fileDataHandler.blobToString(new SerialBlob(applicationUserAvatar.get().getResourceUrl())) : null)
                    .build();
        }catch (SQLException e){
            throw new InternalServerException("Error while finding an avatar");
        }

    }
}
