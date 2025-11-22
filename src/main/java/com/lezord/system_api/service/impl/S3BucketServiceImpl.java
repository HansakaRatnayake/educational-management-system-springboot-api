package com.lezord.system_api.service.impl;

import com.lezord.system_api.service.S3BucketService;
import com.lezord.system_api.util.ResourceNameGenerator;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Service
@RequiredArgsConstructor
public class S3BucketServiceImpl implements S3BucketService {

    private final S3Client s3Client;
    private final ResourceNameGenerator resourceNameGenerator;
    private final S3Presigner s3Presigner;

    private static final Logger logger = LoggerFactory.getLogger(S3BucketServiceImpl.class);

    @Override
    public void createBucket(String bucketName) {

        try {
            if (!doesBucketExist(bucketName)) {
                CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                        .bucket(bucketName)
                        .build();

                CreateBucketResponse response = s3Client.createBucket(bucketRequest);
                logger.info("Successfully created S3 bucket: {}., Location: {}", bucketName, response.location());
            }
        } catch (S3Exception e) {
            logger.error("Failed to create S3 bucket. Error: {}", e.awsErrorDetails().errorMessage());
//            throw new AmazonS3ServiceException("Failed to Create S3 Bucket");

        }

    }

    @Override
    public String generatePresignedUrl(String fileName, String contentType, String bucketName) {
      return "";
    }


    private boolean doesBucketExist(String bucketName) {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            return true; // No exception means the bucket exists
        } catch (NoSuchBucketException e) {
            return false; // Bucket does not exist
        } catch (S3Exception e) {
            if (e.statusCode() == 403) {
                // You don't have access to the bucket, but it exists
                return true;
            }
            // Handle other errors appropriately
            throw e;
        }
    }

}
