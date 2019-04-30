![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# FTP Consumer

This consumer is variation of Scheduled Consumer which is a &quot;time event&quot; based consumer configured to be either an absolute or relative time schedule, backed by (S)FTP Message provider. The (S)FTP Message provider is under pined with persistent store which allow us to store meta information about the files we are processing.
Read more about EIP [Polling Consumer](http://www.enterpriseintegrationpatterns.com/patterns/messaging/PollingConsumer.html)

##### Configuration Options

| Option | Type | Purpose |
| --- | --- | --- |
| cronExpression | String | Cron based expression dictating the callback schedule for this component. Example, \* \* \* \* ?? |
| ignoreMisfire | boolean |   |
| isEager | boolean |  Flag indicating whether if scheduled consumer should trigger(run) again, immediately after first(previous) timely run was successful   |
| timezone | String | Timezone used by quartz scheduler |
| sourceDirectory | String | Remote directory from which to discover files |
| filenamePattern | String | Regular expression for matching file names |
| sourceDirectoryURLFactory | DirectoryURLFactory | Classname for source directories URLs factory. The factory provides more flexible way of defining source directory. Most common use case would be when source directory changes names for instance based on date|
| filterDuplicates | boolean | Default(True) Flag indicating whether to filter out duplicates files based on previously persisted meta information. When value set to false no meta data is persisted hence same file could be processed over and over again.  |
| filterOnFilename | boolean | Default(True) Flag indicating whether to include file name when persisting meta information about processed file.  |
| filterOnLastModifiedDate | boolean | Default(True) Flag indicating whether to include last modified date of the file when persisting meta information about processed file and whether to use the last modified date for filtering. If filterOnFilename=true and filterOnLastModifiedDate=false any modifications to the files would not be detected and file wouldn’t be reprocessed.   |
| renameOnSuccess | boolean | Default(False) Flag indicating whether to rename the processed file after successful consumption |
| renameOnSuccessExtension | String | Optional only applicable when renameOnSuccess=true, renameOnSuccessExtension is suffixed to the processed fileName |
| moveOnSuccess | boolean | Default(False) Flag indicating whether to move the processed file after successful consumption to different location defined by moveOnSuccessNewPath  configuration. |
| moveOnSuccessNewPath | String | Optional only applicable when moveOnSuccess=true, it provides new directory path when the processed file is moved to. |
| destructive | boolean | Default(False) Flag indicating whether the processed file should be deleted after successful consumption |
| chronological | boolean | Default(False) Flag indicating whether the file processing should be based on chronological order of file latest updates. |
| chunking | boolean | Default(False) Flag indicating whether the file download should be performed in smaller distinguished data chunks of size defined by chunkSize configuration. |
| chunkSize | integer | Optional only applicable when chunking=true. Default(1048576) 1MB. |
| checksum | boolean | Default(False) Flag indicating whether to verify integrity of retrieved file by comparing with a checksum supplied by the remote system. |
| isRecursive | boolean | Default(False) Flag indicating whether the sourceDirectory file read should be performed in recursive manner. The option can be useful if once consumes files from top level directory without knowing the lower lever dir structure. |
| minAge | integer | Default(120) file filter related option, expressed in seconds, used to indicate minimum age of the file on the remote filesystem before file can be processed. This setting is in place to prevent (S)FTP consumer from picking up file which is still being written to.|
| maxRows | integer | Default(-1) file filter related option. Given that meta data of processed files is being collected on every successful file consumptions, the maxRows option relates to housekeeping of the meta information. On every successful file consumption as part of post commit process file (S)FTP consumer will attempt to delete maxRows records from file filter persistence table. The operation is skipped when maxRows=-1 |
| ageOfFiles | integer | Default(-1) file filter related option expressed in days. Given that meta data of processed files is being collected on every successful file consumptions, the ageOfFiles option relates to housekeeping of the meta information. On every successful file consumption as part of post commit process file (S)FTP consumer will attempt to delete records older than ageOfFiles records from file filter persistence table. The operation is skipped when ageOfFiles=-1 |
| clientId | String | file filter related option identifying consumer. clientId is stored as part of the meta information persisted about the processed file.  |
| cleanupJournalOnComplete | boolean | Default(true) Existing (S)FTP consumer is using DB persistence tables to establish different operations it is performing as part of the usage of command pattern (FileDiscovery, FileRename, FileRetrive). That persistent information is be default cleaned up when cleanupJournalOnComplete=true. It can be occasionally useful to cleanupJournalOnComplete=false when performing some debugging. |
| remoteHost | String | Default(‘localhost’) host name of the remote (S)FTP server where consumer needs to connect.|
| remotePort | integer | Default(22) port of the remote (S)FTP server where consumer needs to connect.|
| username | String | User name used to login to (S)FTP server where consumer needs to connect.|
| password | String | password used to login to (S)FTP server where consumer needs to connect. Takes precedences over privateKeyFilename. If both provided user/password combination will be used to login rather then user/privateKeyFilename. |
| maxRetryAttempts | integer | Default(3) internal (S)FTP connector retry count. |
| connectionTimeout | integer | Default(60000) expressed in milliseconds. Internal (S)FTP connector connection timeout value. |
| privateKeyFilename | String | Optional only available on SFTP consumer. Allows authentication to remote server with private/public key set given the exchange of the keys and connectivity setup were performed upfront. |
| knownHostsFilename | String | Optional only available on SFTP consumer. Works in combination with private/public key set. |
| preferredKeyExchangeAlgorithm | String | Optional only available on SFTP consumer. Allowing to explicitly provide KeyExchange Algorithm used by the remote server. |
| active | boolean | Optional only available on FTP consumer. Default(False) Flag indicating whether the FTP connection is active or passive | 
| dataTimeout | integer | Optional only available on FTP consumer. Default(300000) expressed in milliseconds. Internal FTP connector data connection timeout value. |
| socketTimeout | integer | Optional only available on FTP consumer. Default(300000) expressed in milliseconds. Internal FTP connector socket connection timeout value. |
| systemKey | String | Optional only available on FTP consumer.  |
| passwordFilePath | String | Optional only available on FTP consumer. The path of the file that contains the password. |
| FTPS | boolean | Optional only available on FTP consumer. Default(false) used to determine if connection is using FTPs |
| ftpsPort | integer | Optional only available on FTP consumer. Default(21) only applicable when FTPS=true. The remote port of FTPs server where consumer needs to connect. |
| ftpsProtocol | String | Optional only available on FTP consumer. Default(‘SSL’) only applicable when FTPS=true. The protocol used for remote FTPs connection. |
| ftpsIsImplicit | booleans | Optional only available on FTP consumer. Default(false) only applicable when FTPS=true. |
| ftpsKeyStoreFilePath | String | Optional only available on FTP consumer. Only applicable when FTPS=true. |
| ftpsKeyStoreFilePassword | String | Optional only available on FTP consumer. Only applicable when FTPS=true. |


##### Sample Usage - builder pattern

```java
public class ModuleConfig {


  @Resource
  private BuilderFactory builderFactory;

  public Consumer getFileConsumer()
  {
      return builderFactory.getComponentBuilder().componentBuilder.sftpConsumer()
        .setCronExpression(sftpConsumerCronExpression)
        .setClientID(sftpConsumerClientID)
        .setUsername(sftpConsumerUsername)
        .setPassword(sftpConsumerPassword)
        .setRemoteHost(sftpConsumerRemoteHost)
        .setRemotePort(sftpConsumerRemotePort)
        .setSourceDirectory(sftpConsumerSourceDirectory)
        .setFilenamePattern(sftpConsumerFilenamePattern)
        .setKnownHostsFilename(sftpConsumerKnownHosts)
        .setChronological(true)
        .setAgeOfFiles(30)
        .setMinAge(60l)
        .setFilterDuplicates(true)
        .setFilterOnLastModifiedDate(true)
        .setRenameOnSuccess(false)
        .setRenameOnSuccessExtension(".tmp")
        .setDestructive(false)
        .setChunking(false)
        .setConfiguredResourceId("configuredResourceId")
        .setScheduledJobGroupName("SftpToLogFlow")
        .setScheduledJobName("SftpConsumer")
        .build();
      
  }
}

```

# Document Info

| Authors | Ikasan Development Team |
| --- | --- |
| Contributors | n/a |
| Date | April 2019 |
| Email | info@ikasan.org |
| WebSite | [http://www.ikasan.org](http://www.ikasan.org/) |
