package org.ikasan.component.endpoint.amazon.s3.producer;

import jakarta.validation.constraints.NotNull;

/**
 * Used to transfer a byte array payload to S3.
 *
 * NOTE - should be used with small size data sets as can potentially blow the heap with large objects. Use the
 * {@link AmazonS3FilePayload} for large data sets so data can be streamed from the local file system without
 * having a memory overhead.
 */
public class AmazonS3ByteArrayPayload extends AbstractAmazonS3Payload {

    @NotNull
    private byte[] contents;

    public byte[] getContents() {
        return contents;
    }

    public void setContents(byte[] contents) {
        this.contents = contents;
    }
}
