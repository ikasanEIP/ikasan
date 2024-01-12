package org.ikasan.component.endpoint.amazon.s3.producer;

import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Takes a byte array and writes it to an S3 bucket. Use this for small datasets only so as to not blow the heap. Use
 * {@link AmazonS3FileProducer} and {@link AmazonS3FilePayload} for streaming large datasets direct from the file system
 *
 * @see AmazonS3FileProducer
 * @see AmazonS3FilePayload
 */
public class AmazonS3ByteArrayProducer extends AbstractAmazonS3Producer<AmazonS3ByteArrayPayload> {

    private static Logger logger = LoggerFactory.getLogger(AmazonS3ByteArrayProducer.class);

    public AmazonS3ByteArrayProducer(AmazonS3Client s3Client) {
        super(s3Client);
    }

    @Override
    public void invoke(AmazonS3ByteArrayPayload payload) throws EndpointException {
        if (configuration.getEnabled()) {
            super.invoke(payload);
            s3Client.uploadByteArray(payload.getContents(), getKeyName(payload), getBucketName(payload));
        } else {
            logger.debug("Configuration is not enabled, so component will do nothing");
        }
    }
}
