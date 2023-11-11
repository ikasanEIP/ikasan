package org.ikasan.component.endpoint.amazon.s3.producer;

import jakarta.validation.constraints.NotNull;

/**
 * Use this to upload a file from the local fileSystem
 */
public class AmazonS3FilePayload extends AbstractAmazonS3Payload {

    /**
     * The full file path use to create a File object to stream data to s3 with
     */
    @NotNull
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
