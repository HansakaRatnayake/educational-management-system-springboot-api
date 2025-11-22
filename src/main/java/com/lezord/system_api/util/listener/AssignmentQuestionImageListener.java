package com.lezord.system_api.util.listener;

import com.lezord.system_api.entity.AssignmentQuestionImage;
import jakarta.persistence.PostRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

/**
 * Entity listener that automatically deletes S3 files when an AssignmentQuestionImage
 * entity is removed from the database
 */
public class AssignmentQuestionImageListener extends AbstractS3ResourceListener<AssignmentQuestionImage> {

    @Value("${aws.bucketName}")
    private String bucket;
    private static final Logger logger = LoggerFactory.getLogger(AssignmentQuestionImageListener.class);

    @PostRemove
    public void onPostRemove(AssignmentQuestionImage image) {
        deleteS3Resource(image, bucket);
    }

    @Override
    protected byte[] getFileName(AssignmentQuestionImage entity) {
        return entity.getFileName();
    }

    @Override
    protected byte[] getDirectory(AssignmentQuestionImage entity) {
        return entity.getDirectory();
    }

    @Override
    protected String getEntityId(AssignmentQuestionImage entity) {
        return entity.getPropertyId();
    }
}
