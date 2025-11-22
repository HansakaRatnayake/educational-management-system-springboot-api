package com.lezord.system_api.util.listener;

import com.lezord.system_api.entity.AssignmentQuestionRecording;
import jakarta.persistence.PostRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class AssignmentQuestionAudioListener extends AbstractS3ResourceListener<AssignmentQuestionRecording> {
    private static final Logger logger = LoggerFactory.getLogger(AssignmentQuestionAudioListener.class);

    @Value("${aws.bucketName}")
    private String bucket;
    @PostRemove
    public void onPostRemove(AssignmentQuestionRecording audio) {
        deleteS3Resource(audio, bucket);
    }

    @Override
    protected byte[] getFileName(AssignmentQuestionRecording entity) {
        return entity.getFileName();
    }

    @Override
    protected byte[] getDirectory(AssignmentQuestionRecording entity) {
        return entity.getDirectory();
    }

    @Override
    protected String getEntityId(AssignmentQuestionRecording entity) {
        return entity.getPropertyId();
    }
}
