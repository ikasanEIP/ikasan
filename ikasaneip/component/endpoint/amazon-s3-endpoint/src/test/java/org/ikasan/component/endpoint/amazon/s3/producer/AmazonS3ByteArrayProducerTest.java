package org.ikasan.component.endpoint.amazon.s3.producer;

import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Client;
import org.ikasan.component.endpoint.amazon.s3.client.AmazonS3Configuration;
import org.ikasan.component.endpoint.amazon.s3.validation.InvalidAmazonS3PayloadException;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class AmazonS3ByteArrayProducerTest {

    private AmazonS3Configuration configuration;

    private AmazonS3ByteArrayProducer amazonS3ByteArrayProducer;

    private AmazonS3ByteArrayPayload amazonS3ByteArrayPayload;

    private byte[] contents;

    @BeforeEach
    void setup(){
        contents = new String("hello world").getBytes(StandardCharsets.UTF_8);
        configuration = new AmazonS3Configuration();
        configuration.setDefaultBucketName(BUCKET_NAME);
        configuration.setAccessKey(ACCESS_KEY);
        configuration.setSecretKey(SECRET_KEY);
        configuration.setRegion(REGION);
        amazonS3ByteArrayProducer = new AmazonS3ByteArrayProducer(new AmazonS3Client());
        amazonS3ByteArrayProducer.setConfiguration(configuration);
        amazonS3ByteArrayPayload = new AmazonS3ByteArrayPayload();
        amazonS3ByteArrayPayload.setContents(contents);
        amazonS3ByteArrayPayload.setKeyName("hello-world.txt");
    }

    @Test
    void testInvoke(){
        AmazonS3Client amazonS3Client = Mockito.mock(AmazonS3Client.class);

        amazonS3ByteArrayProducer = new AmazonS3ByteArrayProducer(amazonS3Client);
        amazonS3ByteArrayProducer.setConfiguration(configuration);
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
        amazonS3ByteArrayProducer.stopManagedResource();

        verify(amazonS3Client).setConfiguration(configuration);
        verify(amazonS3Client).startup();
        verify(amazonS3Client).uploadByteArray(contents,
            "hello-world.txt", BUCKET_NAME );
        verify(amazonS3Client).shutdown();
    }

    @Test
    void testWithNonNullConfigurationPropertiesMissing(){
        Throwable exception = assertThrows(EndpointException.class, () -> {
            AmazonS3Configuration producerConfiguration = new AmazonS3Configuration();
            producerConfiguration.setRegion("Region");
            amazonS3ByteArrayProducer.setConfiguration(producerConfiguration);
        });
        assertTrue(exception.getMessage().contains("""
            Instance of AmazonS3Configuration has the following \
            constraint violations :- [accessKey must not be null, defaultBucketName must not be null, secretKey must not be null]\
            """));
    }

    @Test
    void testWithNonNullPayloadPropertiesMissing(){
        Throwable exception = assertThrows(InvalidAmazonS3PayloadException.class, () -> {
            amazonS3ByteArrayPayload.setKeyName(null);
            amazonS3ByteArrayPayload.setContents(null);
            amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
        });
        assertTrue(exception.getMessage().contains("""
            Instance of AmazonS3ByteArrayPayload has the following constraint \
            violations :- [contents must not be null, keyName must not be null]\
            """));
    }

    @Test
    void testAgainstWithBadAuthenticationDetails() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setSecretKey("wrong");
            configuration.setAccessKey("wrong");
            amazonS3ByteArrayProducer.startManagedResource();
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
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
        amazonS3ByteArrayProducer.stopManagedResource();
    }



    /**
     *  The tests below are for testing against a LIVE Amazon S3 Account - and are ignored for this reason
     *  Please provide details below and unignore if you want to test this
     */

     private static final String ACCESS_KEY="REPLACEME";
     private static final String SECRET_KEY="REPLACEME";
     private static final String REGION="EU_WEST_2";
     private static final String BUCKET_NAME="ikasan-test-bucket";

    @Test
    @Disabled
    void testAgainstS3Env(){
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithNonExistentBucket(){
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setDefaultBucketName("idontexist");
            amazonS3ByteArrayProducer.startManagedResource();
            amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
        });
        assertTrue(exception.getMessage().contains("The configured default bucket idontexist does not exist"));
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithBucketInPayloadAndUsingKeyPrefix(){
        configuration.setDefaultBucketName("ikasan-test-bucket-2");
        configuration.setKeyPrefix("this/is/a/subdirectory/");
        amazonS3ByteArrayPayload.setBucketName("ikasan-test-bucket");
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
    }

    @Test
    @Disabled
    void testAgainstS3EnvWithBadRegion() {
        Throwable exception = assertThrows(EndpointException.class, () -> {
            configuration.setRegion("EU_WEST_1");
            amazonS3ByteArrayProducer.startManagedResource();
        });
        assertTrue(exception.getMessage().contains("""
            The configured default bucket ikasan-test-bucket does not \
            live in configured region EU_WEST_1\
            """));
    }

}