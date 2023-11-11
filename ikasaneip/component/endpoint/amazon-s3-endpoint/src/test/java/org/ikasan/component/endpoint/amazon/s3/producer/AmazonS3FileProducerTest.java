package org.ikasan.component.endpoint.amazon.s3.producer;

import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Configuration;
import org.ikasan.component.endpoint.amazon.s3.validation.InvalidAmazonS3PayloadException;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Provides integration tests (unignore to test against a live s3) and mocked tests
 */
class AmazonS3FileProducerTest {

    private AmazonS3Configuration configuration;

    private AmazonS3FileProducer amazonS3FileProducer;

    private AmazonS3FilePayload amazonS3FilePayload;

    @BeforeEach
    void setup() {
        configuration = new AmazonS3Configuration();
        configuration.setDefaultBucketName(BUCKET_NAME);
        configuration.setAccessKey(ACCESS_KEY);
        configuration.setSecretKey(SECRET_KEY);
        configuration.setRegion(REGION);
        amazonS3FileProducer = new AmazonS3FileProducer(new AmazonS3Client());
        amazonS3FileProducer.setConfiguration(configuration);
        amazonS3FilePayload = new AmazonS3FilePayload();
        amazonS3FilePayload.setFilePath("src/test/resources/test-file.txt");
        amazonS3FilePayload.setKeyName("test-file.txt");
    }

    @Test
    void testInvoke() {
        AmazonS3Client amazonS3Client = Mockito.mock(AmazonS3Client.class);

        amazonS3FileProducer = new AmazonS3FileProducer(amazonS3Client);
        amazonS3FileProducer.setConfiguration(configuration);
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
        amazonS3FileProducer.stopManagedResource();

        verify(amazonS3Client).setConfiguration(configuration);
        verify(amazonS3Client).startup();
        verify(amazonS3Client).uploadFile("src/test/resources/test-file.txt",
            "test-file.txt", BUCKET_NAME);
        verify(amazonS3Client).shutdown();
    }

    @Test
    void testWithNonNullConfigurationPropertiesMissing() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            AmazonS3Configuration producerConfiguration = new AmazonS3Configuration();
            producerConfiguration.setRegion("Region");
            amazonS3FileProducer.setConfiguration(producerConfiguration);
        });
        assertTrue(exception.getMessage().contains("""
            Instance of AmazonS3Configuration has the following \
            constraint violations :- [accessKey must not be null, defaultBucketName must not be null, secretKey must not be null]\
            """));
    }

    @Test
    void testWithNonNullPayloadPropertiesMissing() {
        Throwable exception = assertThrows(InvalidAmazonS3PayloadException.class, () -> {
            amazonS3FilePayload.setKeyName(null);
            amazonS3FilePayload.setFilePath(null);
            amazonS3FileProducer.invoke(amazonS3FilePayload);
        });
        assertTrue(exception.getMessage().contains("""
            Instance of AmazonS3FilePayload has the following constraint \
            violations :- [filePath must not be null, keyName must not be null]\
            """));
    }

    @Test
    void testWithFileNotExists() {
        Throwable exception = assertThrows(InvalidAmazonS3PayloadException.class, () -> {
            amazonS3FilePayload.setKeyName("iwontbeused");
            amazonS3FilePayload.setFilePath("idontexist");
            amazonS3FileProducer.invoke(amazonS3FilePayload);
        });
        assertTrue(exception.getMessage().contains("File at path idontexist does not exist"));
    }

    @Test
    void testAgainstWithBadAuthenticationDetails() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setSecretKey("wrong");
            configuration.setAccessKey("wrong");
            amazonS3FileProducer.startManagedResource();
        });
        assertTrue(exception.getMessage().contains("""
            com.amazonaws.services.s3.model.AmazonS3Exception: \
            The AWS Access Key Id you provided does not exist in our records\
            """));
    }

    @Test
    void testWithComponentDisabled() {
        configuration.setSecretKey("wrong");
        configuration.setAccessKey("wrong");
        configuration.setRegion("Region");
        configuration.setEnabled(false);
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
        amazonS3FileProducer.stopManagedResource();
    }

    /**
     * The tests below are for testing against a LIVE Amazon S3 Account - and are ignored for this reason
     * Please provide details below and unignore if you want to test this. You should create a test bucket called
     * ikasan-test-bucket
     */

    private static final String ACCESS_KEY = "REPLACEME";
    private static final String SECRET_KEY = "REPLACEME";
    private static final String REGION = "EU_WEST_2";
    private static final String BUCKET_NAME = "ikasan-test-bucket";

    @Test
    @Disabled
    void testAgainstS3Env() {
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithNonExistentBucket() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setDefaultBucketName("idontexist");
            amazonS3FileProducer.startManagedResource();
            amazonS3FileProducer.invoke(amazonS3FilePayload);
        });
        assertTrue(exception.getMessage().contains("The configured default bucket idontexist does not exist"));
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithBucketInPayloadAndUsingKeyPrefix() {
        configuration.setDefaultBucketName("ikasan-test-bucket-2");
        configuration.setKeyPrefix("this/is/a/subdirectory/");
        amazonS3FilePayload.setBucketName("ikasan-test-bucket");
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithBadRegion() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setRegion("EU_WEST_1");
            amazonS3FileProducer.startManagedResource();
        });
        assertTrue(exception.getMessage().contains("""
            The configured default bucket ikasan-test-bucket does not \
            live in configured region EU_WEST_1\
            """));
    }

}