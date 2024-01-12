package org.ikasan.component.endpoint.amazon.s3.client;

import jakarta.validation.constraints.NotNull;

/**
 * The Amazon S3 Configuration
 */
public class AmazonS3Configuration {

    /**
     * The default bucket to upload to - will be overridden by the payload bucket if set
     */
    @NotNull
    private String defaultBucketName;

    /**
     * The BasicAWSCredentials access key - account must have write access to all required buckets
     */
    @NotNull
    private String accessKey;

    /**
     * The BasicAWSCredentials secret key - account must have write access to all required buckets
     */
    @NotNull
    private String secretKey;


    /**
     * The AWS Region of the default bucket and any payload buckets
     */
    @NotNull
    private String region;

    /**
     * The files key prefix - allows a "base directory" to be specified
     */
    private String keyPrefix;

    /**
     * Allows this component to be enabled or disabled if Amazon S3 not yet setup
     */
    private Boolean enabled = true;

    public String getDefaultBucketName() {
        return defaultBucketName;
    }

    public void setDefaultBucketName(String defaultBucketName) {
        this.defaultBucketName = defaultBucketName;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "AmazonS3ProducerConfiguration{" +
            "defaultBucketName='" + defaultBucketName + '\'' +
            ", accessKey='********" +'\'' +
            ", secretKey='********" + '\'' +
            ", region='" + region + '\'' +
            ", keyPrefix='" + keyPrefix + '\'' +
            ", enabled='" + enabled + '\'' +
            '}';
    }
}
