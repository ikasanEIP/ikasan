![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Job Plan Export Services

- [Job Plan Status Service](#job-plan-status-service)
- [Job Instance Status Service](#job-instance-status-service)

## Export Job Plan Service
| Parameter                            | Value                                                                                | 
|--------------------------------------|--------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                  |
| Service Name                         | Export Job Plan                                                                      |
| Service Description                  | This service returns a a zipped job plan bundle for the requested job plan.          |
| Service Context                      | /rest/export/context/{job-plan-name}                                                 |
| Sample                               | http://localhost:9090/rest/export/context/MyFirstJobPlan --output MyFirstJobPlan.zip |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                        |
| Returns                              | HTTP 200 status and zipped job plan                                                  |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/export/context/MyFirstJobPlan --output MyFirstJobPlan.zip
```

### Sample Response
[MyFirstJobPlan.zip](./MyFirstJobPlan.zip)

The basic zip layout is as follows:

```text
/context/
/notification/
/profiles/
/jobs/
/jobs/file/ 
/jobs/internal/  
/jobs/quartz/ 
/jobs/global/
/notification_details/
```

## Export Job Plan with Tokens Service
| Parameter                            | Value                                                                                                                       | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                         |
| Service Name                         | Export Job Plan                                                                                                             |
| Service Description                  | This service returns a a zipped job plan bundle for the requested job plan with string replacement tokens to support CI/CD. |
| Service Context                      | /rest/export/context/tokens/{job-plan-name}                                                                                 |
| Sample                               | http://localhost:9090/rest/export/context/tokens/MyFirstJobPlan --output MyFirstJobPlanWithTokens.zip                       |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                               |
| Returns                              | HTTP 200 status and zipped job plan                                                                                         |

### Sample Curl Command
``` text
curl -u <username>:<password> http://localhost:9090/rest/export/context/tokens/MyFirstJobPlan --output MyFirstJobPlanWithTokens.zip
```

### Sample Response
[MyFirstJobPlanWithTokens.zip](./MyFirstJobPlanWithTokens.zip)

The basic zip layout is as follows:

```text
/context/
/notification/
/profiles/
/jobs/
/jobs/file/ 
/jobs/internal/  
/jobs/quartz/ 
/jobs/global/
/notification_details/
```