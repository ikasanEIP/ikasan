![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Dry Run Services
- [Dry Run Mode Service](#dry-run-mode-service)
- [Job Dry Run Mode Service](#job-dry-run-mode-service)
- [File List Dry Run Mode Service](#file-list-dry-run-mode-service)

## Dry Run Mode Service
This service puts the agent hosting the service into dry run mode. 

| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                |
| Service Name                         | Dry Run Mode                                                                                                                                                       |
| Service Description                  | This service accepts a json String representation of [DryRunMode](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/dryrun/DryRunMode.java). |
| Service Context                      | /rest/dryRun/mode                                                                                                                                                  |
| Sample                               | https://localhost:9090/rest/dryRun/mode                                                                                                                            |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status                                                                                                                                                    |

### Sample Payload

```json
{
    "dryRunMode":true
}
```

## Job Dry Run Mode Service
This service puts a specific job on an agent hosting the service into dry run mode.

| Parameter                            | Value                                                                                                                                                                    | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                      |
| Service Name                         | Job Dry Run Mode                                                                                                                                                         |
| Service Description                  | This service accepts a json String representation of [JobDryRunMode](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/dryrun/JobDryRunMode.java). |
| Service Context                      | /rest/dryRun/jobmode                                                                                                                                                     |
| Sample                               | https://localhost:9090/rest/dryRun/jobmode                                                                                                                               |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                            |
| Returns                              | HTTP 200 status                                                                                                                                                          |

### Sample Payload

```json
{
    "dryRun":true,
    "jobName":"jobName"
}
```

## File List Dry Run Mode Service
This service puts a specific set of file watcher jobs on an agent hosting the service into dry run mode.

| Parameter                            | Value                                                                                                                                                                                        | 
|--------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                                          |
| Service Name                         | File List Dry Run Mode                                                                                                                                                                       |
| Service Description                  | This service accepts a json String representation of [DryRunFileListParameter](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/dryrun/DryRunFileListParameter.java). |
| Service Context                      | /rest/dryRun/fileList                                                                                                                                                                        |
| Sample                               | https://localhost:9090/rest/dryRun/fileList                                                                                                                                                  |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                                                |
| Returns                              | HTTP 200 status                                                                                                                                                                              |

### Sample Payload

```json
{"fileList": [
    {"jobName":"jobName1","fileName":"fileName1"},
    {"jobName":"jobName2","fileName":"fileName2"},
    {"jobName":"jobName3","fileName":"fileName3"}
]}
```