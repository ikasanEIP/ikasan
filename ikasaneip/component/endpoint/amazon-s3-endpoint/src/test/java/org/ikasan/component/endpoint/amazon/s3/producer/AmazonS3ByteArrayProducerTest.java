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

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verify;

public class AmazonS3ByteArrayProducerTest {

    private AmazonS3Configuration configuration;

    private AmazonS3ByteArrayProducer amazonS3ByteArrayProducer;

    private AmazonS3ByteArrayPayload amazonS3ByteArrayPayload;

    private byte[] contents;

    @Before
    public void setup(){
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
    public void testInvoke(){
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

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testWithNonNullConfigurationPropertiesMissing(){
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("""
            Instance of AmazonS3Configuration has the following \
            constraint violations :- [accessKey must not be null, defaultBucketName must not be null, secretKey must not be null]\
            """);
        AmazonS3Configuration producerConfiguration = new AmazonS3Configuration();
        producerConfiguration.setRegion("Region");
        amazonS3ByteArrayProducer.setConfiguration(producerConfiguration);
    }

    @Test
    public void testWithNonNullPayloadPropertiesMissing(){
        expectedException.expect(InvalidAmazonS3PayloadException.class);
        expectedException.expectMessage("""
            Instance of AmazonS3ByteArrayPayload has the following constraint \
            violations :- [contents must not be null, keyName must not be null]\
            """);
        amazonS3ByteArrayPayload.setKeyName(null);
        amazonS3ByteArrayPayload.setContents(null);
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
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
        amazonS3ByteArrayProducer.startManagedResource();
    }

    @Test
    public void testWithComponentDisabled() {
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
    @Ignore
    public void testAgainstS3Env(){
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
    }

    @Test
    @Ignore
    public void testAgainstS3EnvWithNonExistentBucket(){
        expectedException.expect(EndpointException.class);
        expectedException.expectMessage("The configured default bucket idontexist does not exist");
        configuration.setDefaultBucketName("idontexist");
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
    }

    @Test
    @Ignore
    public void testAgainstS3EnvWithBucketInPayloadAndUsingKeyPrefix(){
        configuration.setDefaultBucketName("ikasan-test-bucket-2");
        configuration.setKeyPrefix("this/is/a/subdirectory/");
        amazonS3ByteArrayPayload.setBucketName("ikasan-test-bucket");
        amazonS3ByteArrayProducer.startManagedResource();
        amazonS3ByteArrayProducer.invoke(amazonS3ByteArrayPayload);
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
        amazonS3ByteArrayProducer.startManagedResource();
    }

}