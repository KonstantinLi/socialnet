package com.socialnet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("client")
public class ClientProperties {
    private String local;
    private String remote;
}
