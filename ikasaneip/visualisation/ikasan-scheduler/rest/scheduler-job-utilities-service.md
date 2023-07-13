![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Job Utility Services
The Ikasan Enterprise Scheduler Job Utility Services contains utilities that can be used against
scheduler jobs.
- [Process Kill Service](#process-kill-service)

## Process Kill Service

| Parameter                            | Value                                                                 | 
|--------------------------------------|-----------------------------------------------------------------------|
| Request Method                       | GET                                                                   |
| Service Name                         | Process Kill Service                                                  |
| Service Description                  | This service accepts a string value representing the process to kill. |
| Service Context                      | /rest/jobUtils/kill/{pid}                                             |
| Sample                               | https://localhost:9090/rest/jobUtils/kill/{pid}                       |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                         |
| Requires Path parameter pid          | The process id of the process to kill.                                |
| Returns                              | HTTP 200 status                                                       |
