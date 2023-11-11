package org.ikasan.component.endpoint.amazon.s3.producer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.CollectionUtils;
import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Configuration;
import org.ikasan.component.endpoint.amazon.s3.validation.BeanValidator;
import org.ikasan.component.endpoint.amazon.s3.validation.InvalidAmazonS3PayloadException;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.ikasan.spec.component.endpoint.Producer;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.management.ManagedResource;
import org.ikasan.spec.management.ManagedResourceRecoveryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Used to upload a file or a byte[] to S3 depending on the provided payload
 *
 * Note object uploads in S3 are guaranteed to be atomic such that a partially written file is not available for
 * reading.
 *
 * @param <T> the type of payload either {@link AmazonS3FilePayload} or {@link AmazonS3ByteArrayPayload}
 * @see AmazonS3FilePayload
 * @see AmazonS3ByteArrayPayload
 */
public abstract class AbstractAmazonS3Producer<T extends AbstractAmazonS3Payload> implements Producer<T>,
    ConfiguredResource<AmazonS3Configuration>,
    ManagedResource {

    private final static Logger logger = LoggerFactory.getLogger(AbstractAmazonS3Producer.class);

    private String configuredResourceId;

    protected AmazonS3Configuration configuration;

    protected final AmazonS3Client s3Client;

    private final BeanValidator<T> payloadValidator;

    private final BeanValidator<AmazonS3Configuration> configurationValidator;


    public AbstractAmazonS3Producer(AmazonS3Client s3Client){
        this.s3Client = s3Client;
        this.payloadValidator = new BeanValidator<>();
        this.configurationValidator = new BeanValidator<>();
    }

    public void invoke(T payload) throws EndpointException{
        payloadValidator.validateBean(payload, c->{ throw new InvalidAmazonS3PayloadException(
            "Instance of " + payload.getClass().getSimpleName() + " has the following constraint violations :- " +c);});
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String configuredResourceId) {
        this.configuredResourceId = configuredResourceId;
    }

    @Override
    public AmazonS3Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(AmazonS3Configuration configuration) {
        this.configuration = configuration;
        configurationValidator.validateBean(configuration, c->{ throw new EndpointException(
            "Instance of AmazonS3Configuration has the following constraint violations :- " +c);});
    }

    @Override
    public void startManagedResource() {
        if (configuration.getEnabled()) {
            logger.debug("Starting Amazon S3 Producer with configuration [{}]", configuration);
            this.s3Client.setConfiguration(configuration);
            s3Client.startup();
        } else {
            logger.debug("Not starting Amazon S3 Producer as configuration states its not to be enabled");
        }
    }

    @Override
    public void stopManagedResource() {
        if (configuration.getEnabled()) {
            logger.debug("Shutting down Amazon S3 Producer");
            s3Client.shutdown();
        }
    }

    @Override
    public void setManagedResourceRecoveryManager(ManagedResourceRecoveryManager managedResourceRecoveryManager) {

    }

    @Override
    public boolean isCriticalOnStartup() {
        return true;
    }

    @Override
    public void setCriticalOnStartup(boolean criticalOnStartup) {

    }

    protected String getKeyName(AbstractAmazonS3Payload payload){
        String keyName = configuration.getKeyPrefix() != null ? configuration.getKeyPrefix() + payload.getKeyName()
            : payload.getKeyName();
        return keyName;
    }

    protected String getBucketName(AbstractAmazonS3Payload payload){
        String bucketName = payload.getBucketName() != null ? payload.getBucketName()
            : configuration.getDefaultBucketName();
        return bucketName;
    }

}
