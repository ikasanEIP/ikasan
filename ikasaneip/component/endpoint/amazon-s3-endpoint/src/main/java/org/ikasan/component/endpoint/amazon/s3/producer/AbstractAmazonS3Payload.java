package org.ikasan.component.endpoint.amazon.s3.producer;

import jakarta.validation.constraints.NotNull;

/**
 * The data commmon to all S3 Payloads
 */
public abstract class AbstractAmazonS3Payload {

    @NotNull
    private  String keyName;

    private String bucketName;

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
