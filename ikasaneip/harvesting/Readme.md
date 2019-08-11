[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Housekeeping Service
 
The Ikasan Harvesting Service provides default HarvestingJob implementation and Scheduled based service responsible 
for scheduling various harvesting jobs. The default set of jobs auto-configrued by ikasan-harvesting project includes:

- replayHarvestingJob
- wiretapHarvestingJob
- errorReportingHarvestingJob 
- exclusionHarvestingJob 
- messageHistoryHarvestingJob

Each and every one of the jobs can be tuned and configured by following set of properties: 
- {jobName}-harvestBatchSize defaults to 200
- {jobName}-cronExpression defaults to '/10 * * * * ?'
- {jobName}-enabled defaults to true


<br/>

**Full set of properties can be included in application.properties**
```properties
# Replay  harvesting settings
replayHarvestingJob-harvestBatchSize=200
replayHarvestingJob-cronExpression=0 0/1 * * * ?
replayHarvestingJob-enabled=true

# Wiretap harvesting settings
wiretapHarvestingJob-harvestBatchSize=200
wiretapHarvestingJob-cronExpression=0 0/1 * * * ?
wiretapHarvestingJob-enabled=true

# Error harvesting settings
errorReportingHarvestingJob-harvestBatchSize=200
errorReportingHarvestingJob-cronExpression=0 0/1 * * * ?
errorReportingHarvestingJob-enabled=true

# Exclusion harvesting settings
exclusionHarvestingJob-harvestBatchSize=200
exclusionHarvestingJob-cronExpression=0 0/1 * * * ?
exclusionHarvestingJob-enabled=true

# Message History harvesting settings
messageHistoryHarvestingJob-harvestBatchSize=200
messageHistoryHarvestingJob-cronExpression=0 0/1 * * * ?
messageHistoryHarvestingJob-enabled=true


```

<br/>
<br/>

All above jobs are using DashboardRestService which is part of topology project to publish respective data entities to ikasan dashboard. Publishing data to dashboard 
is switched off by default but can be enabled using properties. Following four properties are responsible for providing
dashboard coordinates

<br/>

```properties
# harvesting settings
ikasan.harvesting.enabled=true
ikasan.dashboard.base.url=http://localhost:8090/ikasan-dashboard
ikasan.dashboard.username=
ikasan.dashboard.password=

```

<br/>
