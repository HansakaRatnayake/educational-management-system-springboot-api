package com.lezord.system_api.service;

public interface S3BucketService {

    void createBucket(String bucketName);
    String generatePresignedUrl(String fileName, String contentType, String bucketName);

}
