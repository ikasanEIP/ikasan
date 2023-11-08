package org.ikasan.component.endpoint.amazon.s3.producer;

import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Configuration;
import org.ikasan.component.endpoint.amazon.s3.validation.InvalidAmazonS3PayloadException;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

/**
 * Provides integration tests (unignore to test against a live s3) and mocked tests
 */
public class AmazonS3FileProducerTest {

    private AmazonS3Configuration configuration;

    private AmazonS3FileProducer amazonS3FileProducer;

    private AmazonS3FilePayload amazonS3FilePayload;

    @Before
    public void setup() {
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
    public void testInvoke() {
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWithNonNullConfigurationPropertiesMissing() {
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("""
            Instance of AmazonS3Configuration has the following \
            constraint violations :- [accessKey must not be null, defaultBucketName must not be null, secretKey must not be null]\
            """);
        AmazonS3Configuration producerConfiguration = new AmazonS3Configuration();
        producerConfiguration.setRegion("Region");
        amazonS3FileProducer.setConfiguration(producerConfiguration);
    }

    @Test
    public void testWithNonNullPayloadPropertiesMissing() {
        expectedException.expect(InvalidAmazonS3PayloadException.class);
        expectedException.expectMessage("""
            Instance of AmazonS3FilePayload has the following constraint \
            violations :- [filePath must not be null, keyName must not be null]\
            """);
        amazonS3FilePayload.setKeyName(null);
        amazonS3FilePayload.setFilePath(null);
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    public void testWithFileNotExists() {
        expectedException.expect(InvalidAmazonS3PayloadException.class);
        expectedException.expectMessage("File at path idontexist does not exist");
        amazonS3FilePayload.setKeyName("iwontbeused");
        amazonS3FilePayload.setFilePath("idontexist");
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    public void testAgainstWithBadAuthenticationDetails() {
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("""
            com.amazonaws.services.s3.model.AmazonS3Exception: \
            The AWS Access Key Id you provided does not exist in our records\
            """);
        configuration.setSecretKey("wrong");
        configuration.setAccessKey("wrong");
        amazonS3FileProducer.startManagedResource();
    }

    @Test
    public void testWithComponentDisabled() {
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
    @Ignore
    public void testAgainstS3Env() {
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    @Ignore
    public void testAgainstS3EnvWithNonExistentBucket() {
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("The configured default bucket idontexist does not exist");
        configuration.setDefaultBucketName("idontexist");
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    @Ignore
    public void testAgainstS3EnvWithBucketInPayloadAndUsingKeyPrefix() {
        configuration.setDefaultBucketName("ikasan-test-bucket-2");
        configuration.setKeyPrefix("this/is/a/subdirectory/");
        amazonS3FilePayload.setBucketName("ikasan-test-bucket");
        amazonS3FileProducer.startManagedResource();
        amazonS3FileProducer.invoke(amazonS3FilePayload);
    }

    @Test
    @Ignore
    public void testAgainstS3EnvWithBadRegion() {
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("""
            The configured default bucket ikasan-test-bucket does not \
            live in configured region EU_WEST_1\
            """);
        configuration.setRegion("EU_WEST_1");
        amazonS3FileProducer.startManagedResource();
    }

}