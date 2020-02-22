package com.toomuch2learn.springboot2.crud.catalogue.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {

    private String uploadLocation;
}
