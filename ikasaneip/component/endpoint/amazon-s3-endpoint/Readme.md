[<< Component Quick Start](../../Readme.md)
![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Amazon S3 Components

## Amazon S3 Producer


### Configuration Options

| Option | Type |  Purpose | Required |
| --- | --- | --- | --- |
| defaultBucketName | String | The bucket to write to if this isnt specified on the payload  | Y |
| accessKey | String | The aws iam access key | Y |
| secretKey | String | The aws iam secret key | Y |
| region | String | The aws region the bucket is in | Y |
| keyPrefix | String | Used when need to write to a 'sub-directory' of the bucket e.g /write/to/this/sub/dir/ | N |

**Note** : If any required properties are missing an EndpointException will get thrown when the components 
```setConfiguration()``` method is called

### Resource Management

The producer components implements the ```ManagedResource``` interface. When ```startManagedResource``` is called :-

* The ```AmazonS3Client``` authenticates
* The ```AmazonS3Client``` issues a ping check to ensure the ```defaultBucketName``` and ```region``` specified is valid

On failure of these checks an ```EndpointException``` will get thrown. When ```stopManagedResource``` is called :-

* The AWS S3 client is shutdown cleanly.

### Sample Usage

Two Producers are provided. The ```AmazonS3ByteArrayProducer``` is used to upload byte[] data to an 
S3 bucket. It needs to be invoked with a ```AmazonS3ByteArrayPayload``` which specify the contents and the key to write
the data to in the bucket. This class is shown below, note a ```InvalidAmazonS3PayloadException``` will get thrown if
the contents field or keyName field is null.

```java

/**
 * The data commmon to all S3 Payloads
 */
public abstract class AbstractAmazonS3Payload {

    @NotNull
    private  String keyName;

    private String bucketName;

    //..
    
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
```
When creating the producer you need to pass in the ```AmazonS3Client``` as shown below. The client will be configured
when the components ```startManagedResource``` is called by Ikasan.

```java
  AmazonS3ByteArrayProducer amazonS3ByteArrayProducer = new AmazonS3ByteArrayProducer(new AmazonS3Client());
  amazonS3ByteArrayProducer.setConfiguration(configuration);
```

For larger data sets data should be streamed from the file system and the ```AmazonS3FileProducer``` used. In this case
the full file path to the data should be specified in the payload as well as the key. 
Note a ```InvalidAmazonS3PayloadException``` will get thrown if the filePath or keyName field is null.

```java
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
```
And you can create the component in a similar way to the byte array producer  :-

```java
  AmazonS3FileProducer amazonS3FileProducer = new AmazonS3FileProducer(new AmazonS3Client());
  amazonS3FileProducer.setConfiguration(configuration);
```

