[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Housekeeping Service
The Ikasan Housekeeping Service provides default HousekeepingJob implementation and Scheduled based service responsible 
for scheduling various housekeeping jobs. The default set of jobs auto-configrued by ikasan-housekeeping project includes:

- replayHousekeepingJob
- wiretapHousekeepingJob
- errorReportingHousekeepingJob
- systemEventServiceHousekeepingJob 
- duplicateFilterHousekeepingJob
- messageHistoryHousekeepingJob

Each and every one of the jobs can be tuned and configured by following set of properties: 
- {jobName}-houseKeepingBatchSize defaults to 2500
- {jobName}-transactionBatchSize defaults to 200
- {jobName}-cronExpression defaults to '0 0/1 * * * ?'
- {jobName}-deleteOnceHarvested defaults to false (NB applicable for replayHousekeepingJob, wiretapHousekeepingJob, errorReportingHousekeepingJob, systemEventServiceHousekeepingJob)
- {jobName}-enabled defaults to true


<br/>
<br/>

**Full set of properties can be included in application.properties**
```properties
# Replay  housekeeping settings
replayHousekeepingJob-houseKeepingBatchSize=2500
replayHousekeepingJob-transactionBatchSize defaults=200
replayHousekeepingJob-cronExpression=0 0/1 * * * ?
replayHousekeepingJob-deleteOnceHarvested=false
replayHousekeepingJob-enabled=true

# Wiretap housekeeping settings
wiretapHousekeepingJob-houseKeepingBatchSize=2500
wiretapHousekeepingJob-transactionBatchSize defaults=200
wiretapHousekeepingJob-cronExpression=0 0/1 * * * ?
wiretapHousekeepingJob-deleteOnceHarvested=false
wiretapHousekeepingJob-enabled=true

# Error housekeeping settings
errorReportingHousekeepingJob-houseKeepingBatchSize=2500
errorReportingHousekeepingJob-transactionBatchSize defaults=200
errorReportingHousekeepingJob-cronExpression=0 0/1 * * * ?
errorReportingHousekeepingJob-deleteOnceHarvested=false
errorReportingHousekeepingJob-enabled=true

# SystemEvents housekeeping settings
systemEventServiceHousekeepingJob-houseKeepingBatchSize=2500
systemEventServiceHousekeepingJob-transactionBatchSize defaults=200
systemEventServiceHousekeepingJob-cronExpression=0 0/1 * * * ?
systemEventServiceHousekeepingJob-deleteOnceHarvested=false
systemEventServiceHousekeepingJob-enabled=true

# Duplicate Filter housekeeping settings
duplicateFilterHousekeepingJob-houseKeepingBatchSize=2500
duplicateFilterHousekeepingJob-transactionBatchSize defaults=200
duplicateFilterHousekeepingJob-cronExpression=0 0/1 * * * ?
duplicateFilterHousekeepingJob-enabled=true


# Message History Filter housekeeping settings
messageHistoryHousekeepingJob-houseKeepingBatchSize=2500
messageHistoryHousekeepingJob-transactionBatchSize defaults=200
messageHistoryHousekeepingJob-cronExpression=0 0/1 * * * ?
messageHistoryHousekeepingJob-enabled=true


```

<br/>