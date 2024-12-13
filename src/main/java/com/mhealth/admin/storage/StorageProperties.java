package com.mhealth.admin.storage;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(value = "storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    @Value("${storage.location}")
    private String location = "upload-dir";

}
