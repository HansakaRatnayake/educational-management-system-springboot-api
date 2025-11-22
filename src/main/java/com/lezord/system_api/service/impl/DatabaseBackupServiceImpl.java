package com.lezord.system_api.service.impl;

import com.lezord.system_api.service.DatabaseBackupService;
import com.lezord.system_api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Service
@RequiredArgsConstructor
public class DatabaseBackupServiceImpl implements DatabaseBackupService {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseBackupServiceImpl.class);

    @Value("${db.backup.user}")
    private String dbUser;

    @Value("${db.backup.password}")
    private String dbPassword;

    @Value("${db.backup.name}")
    private String dbName;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.accessKey}")
    private String accessKey;

    @Value("${aws.secretKey}")
    private String secretKey;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${business.email}")
    private String businessEmail;

    private final EmailService emailService;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Colombo")
    public void performScheduledBackup() {
        logger.info("📦 Starting database backup task...");
        File sqlFile = null;
        File zipFile = null;

        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String sqlFileName = dbName + "_" + timestamp + ".sql";
            String zipFileName = dbName + "_" + timestamp + ".zip";

            sqlFile = File.createTempFile("backup_", ".sql");
            zipFile = File.createTempFile("backup_", ".zip");

            // Step 1: Run mysqldump
            String command = String.format("mysqldump -u%s -p%s %s -r %s", dbUser, dbPassword, dbName, sqlFile.getAbsolutePath());
            Process process = Runtime.getRuntime().exec(command);
            int result = process.waitFor();
            if (result != 0) {
                logger.error("❌ Database dump failed.");
                return;
            }

            // Step 2: Compress SQL into ZIP
            try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
                 FileInputStream fis = new FileInputStream(sqlFile)) {

                zos.putNextEntry(new ZipEntry(sqlFileName));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            }

            // Step 3: Upload to S3
            try {
                uploadToS3(zipFile, "backups/db" + zipFileName);
                logger.info("Backup uploaded to S3: {}", zipFileName);
            } catch (Exception ex) {
                logger.error("S3 upload failed: {}", ex.getMessage());
                sendBackupToEmail(zipFile, zipFileName,adminEmail);
                sendBackupToEmail(zipFile, zipFileName,businessEmail);
            }

        } catch (Exception ex) {
            logger.error("Backup job failed: {}", ex.getMessage());
        } finally {
            if (sqlFile != null) sqlFile.delete();
        }
    }

    private void uploadToS3(File file, String s3Key) {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                )).build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .build();

        s3.putObject(request, RequestBody.fromFile(file));
    }

    private void sendBackupToEmail(File file, String fileName,String sendEmail) {
        try (FileInputStream input = new FileInputStream(file)) {
            MultipartFile multipartFile = new MockMultipartFile(fileName, fileName, "application/zip", input);
            boolean sent = emailService.sendGenericFileAttachment(
                    sendEmail,
                    "MySQL Backup Failed to Upload to S3",
                    "Automatic backup failed to upload to S3. The backup is attached.",
                    multipartFile
            );
            if (sent) {
                logger.info("Backup sent to admin via email.");
            }
        } catch (IOException e) {
            logger.error("Failed to send backup email: {}", e.getMessage());
        }
    }

}
