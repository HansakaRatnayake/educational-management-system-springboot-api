package com.lezord.system_api.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class EmailTemplateHelper {

    private static final Logger logger = LoggerFactory.getLogger(EmailTemplateHelper.class);

    public Optional<String> loadHtmlTemplate(String templateFileName) {
        try {
            ClassPathResource resource = new ClassPathResource(templateFileName);
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
//            String content = Files.readString(path, StandardCharsets.UTF_8);
            logger.info("Successfully loaded email template: {}", templateFileName);
            return Optional.of(content);
        } catch (IOException e) {
            logger.error("Failed to load email template: {}", templateFileName, e);
            return Optional.empty();
        }
    }

}
