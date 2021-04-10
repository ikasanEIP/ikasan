package org.ikasan.component.endpoint.amazon.s3.producer;

import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.component.endpoint.amazon.s3.validation.BeanValidator;
import org.ikasan.component.endpoint.amazon.s3.validation.InvalidAmazonS3PayloadException;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writes from the local file system to an amazon s3 bucket
 */
public class AmazonS3FileProducer extends AbstractAmazonS3Producer<AmazonS3FilePayload> {

    private static Logger logger = LoggerFactory.getLogger(AmazonS3FileProducer.class);

    public AmazonS3FileProducer(AmazonS3Client s3Client) {
        super(s3Client);
    }

    @Override
    public void invoke(AmazonS3FilePayload payload) throws EndpointException {
        super.invoke(payload);
        s3Client.uploadFile(payload.getFilePath(), getKeyName(payload), getBucketName(payload));
    }
}
