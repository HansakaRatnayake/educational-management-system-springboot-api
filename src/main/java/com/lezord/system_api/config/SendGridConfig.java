package com.lezord.system_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "application.sendgrid")
public class SendGridConfig {

    private String fromEmail;
    private String emailKey;
}
