package com.lezord.system_api.service.impl;

import com.amazonaws.services.dlm.model.InternalServerException;
import com.lezord.system_api.dto.response.ResponseCourseThumbnailDTO;
import com.lezord.system_api.entity.Course;
import com.lezord.system_api.entity.CourseThumbnail;
import com.lezord.system_api.exception.EntryNotFoundException;
import com.lezord.system_api.repository.CourseRepository;
import com.lezord.system_api.repository.CourseThumbnailRepository;
import com.lezord.system_api.service.CourseThumbnailService;
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
public class CourseThumbnailServiceImpl implements CourseThumbnailService {

    private final CourseThumbnailRepository courseThumbnailRepository;
    private final CourseRepository courseRepository;
    private final FileServiceImpl fileService;
    private final FileDataHandler fileDataHandler;

    @Value("${aws.bucketName}")
    private String bucket;


    @Override
    public void create(MultipartFile file, String courseId) {
        if (file.isEmpty()) throw new EntryNotFoundException("File is empty");
        Course selectedCourse = courseRepository.findById(courseId).orElseThrow(() -> new EntryNotFoundException(String.format("Course %s not found", courseId)));

        UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = fileService.create(file, bucket, "courses/thumbnails");

        CourseThumbnail.builder()
                .propertyId(UUID.randomUUID().toString())
                .createdDate(Instant.now())
                .course(selectedCourse)
                .hash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()))
                .directory(uploadedResourceBinaryDataDTO.getDirectory().getBytes())
                .fileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()))
                .resourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()))
                .build();
    }

    @Override
    public void delete(String courseId) {

    }

    @Override
    public ResponseCourseThumbnailDTO findByCourseId(String courseId) {return null;}

    @Override
    public void update(MultipartFile file, String courseId) {
        if (file.isEmpty()) throw new EntryNotFoundException("File is empty");
        Course selectedCourse = courseRepository.findById(courseId).orElseThrow(() -> new EntryNotFoundException(String.format("Course %s not found", courseId)));
        Optional<CourseThumbnail> selectedCourseThumbnail = courseThumbnailRepository.findByCourseId(courseId);

        try {
            if(selectedCourseThumbnail.isPresent()) {
                fileService.delete(
                        fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.get().getFileName())),
                        bucket,
                        fileDataHandler.blobToString(new SerialBlob(selectedCourseThumbnail.get().getFileName()))
                );
                UploadedResourceBinaryDataDTO uploadedResourceBinaryDataDTO = fileService.create(file, bucket, "courses/thumbnails");
                selectedCourseThumbnail.get().setHash(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getHash()));
                selectedCourseThumbnail.get().setDirectory(uploadedResourceBinaryDataDTO.getDirectory().getBytes());
                selectedCourseThumbnail.get().setFileName(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getFilename()));
                selectedCourseThumbnail.get().setResourceUrl(fileDataHandler.blobToByteArray(uploadedResourceBinaryDataDTO.getResourceUrl()));
            }else {
                create(file, courseId);
            }
        }catch (SQLException e){
            throw new InternalServerException("Error while update an image");
        }




    }
}
