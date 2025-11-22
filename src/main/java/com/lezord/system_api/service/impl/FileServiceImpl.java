package com.lezord.system_api.service.impl;

import com.amazonaws.util.IOUtils;
import com.lezord.system_api.exception.AmazonS3ServiceException;
import com.lezord.system_api.service.FileService;
import com.lezord.system_api.util.UploadedResourceBinaryDataDTO;
import com.lezord.system_api.util.ResourceNameGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final ResourceNameGenerator resourceNameGenerator;
    private final S3Client s3Client;

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public UploadedResourceBinaryDataDTO create(MultipartFile file, String bucket, String directory) {
        try {
            // Normalize directory path - ensure it ends with a slash
            String normalizedDirectory = directory.endsWith("/") ? directory : directory + "/";

            // Generate new file name
            String newFileName = resourceNameGenerator.generateResourceName(file.getOriginalFilename());
            String filePath = normalizedDirectory + newFileName;

            // Calculate MD5 hash
            byte[] fileBytes = file.getBytes(); // Read file content once
            byte[] md5Digest = calculateMD5(fileBytes);
            String contentMd5Base64 = Base64.getEncoder().encodeToString(md5Digest);

            // Create PutObjectRequest
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            // Upload the file to S3
            PutObjectResponse putObjectResponse = s3Client.putObject(
                    putObjectRequest, RequestBody.fromBytes(fileBytes)
            );


            logger.info("Successfully uploaded file to S3. Bucket: {}, File: {}", bucket, filePath);

            // Generate the S3 file URL
            String fileUrl = s3Client.utilities().getUrl(builder -> builder.bucket(bucket).key(filePath)).toString();

            // Create and return the response DTO with serial blobs for database storage
            return UploadedResourceBinaryDataDTO.builder()
                    .hash(new SerialBlob(contentMd5Base64.getBytes()))
                    .directory(new SerialBlob(normalizedDirectory.getBytes()).toString())
                    .filename(new SerialBlob(newFileName.getBytes()))
                    .resourceUrl(new SerialBlob(fileUrl.getBytes()))
                    .size((long) fileBytes.length / (1024 * 1024))
                    .build();

        } catch (S3Exception | IOException | SQLException | NoSuchAlgorithmException e) {
            logger.error("Failed to upload file to S3. File: {}, Bucket: {}, Error: {}",
                    file.getOriginalFilename(), bucket, e.getMessage());
            throw new AmazonS3ServiceException("Failed to upload file to S3.");
        }
    }

    @Override
    public void delete(String fileName, String bucket, String directory) {
        try {
            // Normalize directory path
            String normalizedDirectory = directory.endsWith("/") ? directory : directory + "/";
            String filePath = normalizedDirectory + fileName;

            // Delete the file from S3
            s3Client.deleteObject(builder -> builder
                    .bucket(bucket)
                    .key(filePath)
                    .build());

            logger.info("Successfully deleted file from S3. Bucket: {}, File: {}", bucket, filePath);
        } catch (Exception e) {
            logger.error("Failed to delete file from S3. Bucket: {}, File: {}, Error: {}",
                    bucket, fileName, e.getMessage());
            throw new AmazonS3ServiceException("Failed to delete file from S3.");
        }
    }

    @Override
    public byte[] download(String fileName, String bucket, String directory) {
        try {
            // Normalize directory path
            String normalizedDirectory = directory.endsWith("/") ? directory : directory + "/";
            String filePath = normalizedDirectory + fileName;

            // Get the object from S3
            ResponseInputStream<GetObjectResponse> object = s3Client.getObject(builder -> builder
                    .bucket(bucket)
                    .key(filePath)
                    .build());

            // Read the input stream into a byte array
            byte[] content = IOUtils.toByteArray(object);

            logger.info("Successfully downloaded file from S3. Bucket: {}, File: {}", bucket, filePath);

            return content;
        } catch (IOException e) {
            logger.error("Failed to download file from S3. Bucket: {}, File: {}, Error: {}",
                    bucket, fileName, e.getMessage());
            throw new AmazonS3ServiceException("Failed to download file from S3.");
        }
    }

    // Helper method to calculate the MD5 hash of the file
    private byte[] calculateMD5(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        return md.digest(data);
    }

    // Overload to calculate MD5 from an input stream
    private byte[] calculateMD5(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] buffer = new byte[8192];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            md.update(buffer, 0, bytesRead);
        }
        return md.digest();
    }
}