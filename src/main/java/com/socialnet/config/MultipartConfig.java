package com.socialnet.config;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

@Configuration
public class MultipartConfig {

    @Value("${aws.max-image-file-size}")
    private String maxImageFileSize;

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        long maxFileSize = getMaxImageFileSize();
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofBytes(maxFileSize));
        factory.setMaxRequestSize(DataSize.ofBytes(maxFileSize));
        return factory.createMultipartConfig();
    }

    private Long getMaxImageFileSize() {
        return Long.parseLong(maxImageFileSize);
    }
}
