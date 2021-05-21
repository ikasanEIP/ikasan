package org.ikasan.component.endpoint.amazon.s3.client;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import org.ikasan.spec.component.endpoint.EndpointException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;

/**
 * Used to connect to and perform operations against Amazon S3
 */
public class AmazonS3Client {

    private AmazonS3 s3;

    private AmazonS3Configuration configuration;

    private static Logger logger = LoggerFactory.getLogger(AmazonS3Client.class);

    public AmazonS3Client(){
    }

    public void setConfiguration(AmazonS3Configuration configuration){
        this.configuration = configuration;
    }

    /**
     * Connects to Amazon S3 and performs ping checks so the component will fail at startup if authentication,
     * bucket or region configuration is wrongly specified
     *
     * @throws EndpointException if cant connect or the client is wrongly configured
     */
    public void startup() throws EndpointException{
        logger.debug("Initializing Amazon S3 Producer with configuration [{}]", configuration);
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(configuration.getAccessKey(),
            configuration.getSecretKey());
        try {
            s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(Regions.valueOf(configuration.getRegion())).build();
            // check to see if the client can connect - will fail if credentials are wrong
            boolean bucketExists = s3.doesBucketExistV2(configuration.getDefaultBucketName());
            if (!bucketExists){
                throw new EndpointException("The configured default bucket " + configuration.getDefaultBucketName() +
                    " does not exist");
            }
        } catch (AmazonClientException amazonClientException){
            throw new EndpointException(amazonClientException);
        }
        String bucketLocation = s3.getBucketLocation(configuration.getDefaultBucketName());
        if (!Regions.valueOf(configuration.getRegion()).getName().equals(bucketLocation)){
            throw new EndpointException("The configured default bucket " + configuration.getDefaultBucketName() +
                " does not live in configured region " + configuration.getRegion());
        }
    }

    public void shutdown(){
        s3.shutdown();
    }

    /**
     * Upload a file
     *
     * This is normally used for large datasets which cant be loaded into memory. This will transfer data in the most
     * efficient way possible delegating to the S3 Transfer Manger.
     *
     * @param filePath the full path to the file to upload
     * @param keyName the key id of the file to upload
     * @param bucketName the amazon s3 bucket to upload to
     */
    public void uploadFile(String filePath, String keyName, String bucketName){
        TransferManager tx = TransferManagerBuilder.standard().withS3Client(s3)
            .build();
        logger.info("About to upload file [{}] with key [{}] to Amazon S3 bucket [{}]",
            filePath, keyName, bucketName);
        try {
            Upload xfer = tx.upload(bucketName, keyName, new File(filePath));
            TransferManagerProgressLogger.showTransferProgress(xfer);
            xfer.waitForCompletion();
        } catch (AmazonClientException | InterruptedException ace) {
            throw new EndpointException(ace);
        } finally {
            tx.shutdownNow(false);
        }

    }

    /**
     * Use this to upload a byte array object to amazon s3. This is used for datasets that can be contained in memory
     * and are generally intended to be a few k in size.
     */
    public void uploadByteArray(byte[] contents, String keyName, String bucketName){
        TransferManager tx = TransferManagerBuilder.standard().withS3Client(s3)
            .build();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contents);
        ObjectMetadata om = new ObjectMetadata();
        om.setContentLength(contents.length);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, byteArrayInputStream, om);
        logger.info("About to upload byte array of size [{}] bytes with key [{}] to Amazon S3 bucket [{}]",
            contents.length, keyName, bucketName);
        try {
            Upload xfer = tx.upload(putObjectRequest);
            TransferManagerProgressLogger.showTransferProgress(xfer);
            xfer.waitForCompletion();
        } catch (AmazonServiceException | InterruptedException ace){
            throw new EndpointException(ace);
        }
        finally {
            tx.shutdownNow(false);
        }
    }
}
