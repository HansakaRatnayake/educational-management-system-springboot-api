package com.lezord.system_api.util.listener;

import com.lezord.system_api.service.FileService;
import com.lezord.system_api.util.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

public abstract class AbstractS3ResourceListener<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractS3ResourceListener.class);
    protected void deleteS3Resource(T entity, String bucketName) {
        try {
            // Get file name and directory from entity
            byte[] fileNameBytes = getFileName(entity);
            byte[] directoryBytes = getDirectory(entity);
            String entityId = getEntityId(entity);

            if (fileNameBytes == null || directoryBytes == null) {
                logger.warn("Cannot delete S3 file: fileName or directory is null for entity with ID: {}", entityId);
                return;
            }

            // Convert byte arrays to strings
            String fileName = new String(fileNameBytes, StandardCharsets.UTF_8);
            String directory = new String(directoryBytes, StandardCharsets.UTF_8);

            // Get FileService instance from Spring context
            FileService fileService = BeanUtil.getBean(FileService.class);

            // Delete the file from S3
            fileService.delete(fileName, bucketName, directory);

            logger.info("Successfully deleted S3 file after entity removal: {}/{}", directory, fileName);
        } catch (Exception e) {
            // Log error but don't re-throw (entity is already deleted from DB)
            logger.error("Failed to delete S3 file for entity with ID: {}. Error: {}",
                    getEntityId(entity), e.getMessage(), e);
        }
    }
    protected abstract byte[] getFileName(T entity);

    protected abstract byte[] getDirectory(T entity);

    protected abstract String getEntityId(T entity);
}
