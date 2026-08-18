[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Housekeeping Service
 
The Ikasan Harvesting Service provides default HarvestingJob implementation and Scheduled based service responsible
for scheduling various harvesting jobs. The default set of jobs auto-configured by ikasan-harvesting project includes:

- replayHarvestingJob
- wiretapHarvestingJob
- errorReportingHarvestingJob
- exclusionHarvestingJob
- messageHistoryHarvestingJob
- systemEventHarvestingJob
- moduleFlowStateJob
- moduleMetaDataHarvestJob
- configurationMetaDataHarvestJob

Each and every one of the jobs can be tuned and configured by following set of properties:
- {jobName}-harvestBatchSize defaults to 200
- {jobName}-cronExpression defaults to '0/10 * * * * ?' (except metadata jobs which default to '0 0/5 * * * ?')
- {jobName}-enabled defaults to true

## Cron Expression Randomization

To prevent resource contention when multiple modules are deployed, harvesting job cron expressions are automatically randomized
using the module name and job name as a seed. This ensures that jobs are distributed across the time interval while remaining
deterministic and predictable.

**How it works:**
- The configured cron expression defines the interval (e.g., every 5 minutes, every 10 seconds)
- The randomizer adjusts the execution time within that interval based on the module and job name
- The same module and job will always get the same randomized schedule (deterministic)
- Different modules will have different randomized schedules (distributed load)

**Examples:**
- `0 0/5 * * * ?` (every 5 minutes) → might become `17 2/5 * * * ?` (runs at second 17, minute offset 2 within each 5-minute window)
- `0/10 * * * * ?` (every 10 seconds) → might become `3/10 * * * * ?` (runs at second offset 3 within each 10-second window)
- `0 0 0/1 * * ?` (every hour) → might become `42 23 0/1 * * ?` (runs at 23 minutes 42 seconds past each hour)

**Note:** Wildcard fields (e.g., `*`, `?`) remain unchanged during randomization.


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

# System Event harvesting settings
systemEventHarvestingJob-harvestBatchSize=200
systemEventHarvestingJob-cronExpression=0 0/1 * * * ?
systemEventHarvestingJob-enabled=true

# Module Flow State harvesting settings
# Note - batch size is irrelevant as all flow states reported to dashboard.
moduleFlowStateJob-harvestBatchSize=200
moduleFlowStateJob-cronExpression=0 0/1 * * * ?
moduleFlowStateJob-enabled=true

# Module Metadata harvesting settings
# Note - batch size is irrelevant as there is only one module.
# Defaults to every 5 minutes to reduce dashboard update frequency
moduleMetaDataHarvestJob-harvestBatchSize=-1
moduleMetaDataHarvestJob-cronExpression=0 0/5 * * * ?
moduleMetaDataHarvestJob-enabled=true

# Configuration Metadata harvesting settings
# Note - batch size is irrelevant as there is only one module and configuration is derived from that module.
# Defaults to every 5 minutes to reduce dashboard update frequency
configurationMetaDataHarvestJob-harvestBatchSize=-1
configurationMetaDataHarvestJob-cronExpression=0 0/5 * * * ?
configurationMetaDataHarvestJob-enabled=true


```
