![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Agent Job Provision Services

- [Provision Scheduler Jobs on an Agent](#provision-scheduler-jobs-on-an-agent)
- [Remove Scheduler Jobs from an Agent for a Job Plan](#remove-scheduler-jobs-from-an-agent-for-a-job-plan)

## Managing Job Provisioning with an Agent
Ikasan Enterprise Scheduler Agents are no code standard Ikasan modules whose flows are configurable based on the 
job configurations that are passed to the job provisioning service. 

### Provision Scheduler Jobs on an Agent 
An agent always need to be aware of job plan instances that are currently running as part of the job plan lifecycle.
This service is exposed to allow the Ikasan Enterprise Scheduler Dashboard to publish job plan instances to it when they are
created or recovered.

| Parameter                            | Value                                                                                                                                               | 
|--------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                 |
| Service Name                         | Save                                                                                                                                                |
| Service Description                  | This service publishes scheduler jobs to an agent in order for the agent to configure itself.                                                       |
| Service Context                      | /scheduler-agent/rest/jobProvision                                                                                                                  |
| Sample                               | https://localhost:9090/scheduler-agent/rest/jobProvision                                                                                            |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                       |
| Payload                              | A json serialised [SchedulerJobWrapper](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/job/model/SchedulerJobWrapper.java) |
| Returns                              | HTTP 200 status                                                                                                                                     |

### Remove Scheduler Jobs from an Agent for a Job Plan
If a Job Plan is disabled or deleted from the Ikasan Scheduler Dashboard, this service allows for all jobs that have
been configured on the agent, to be removed.

| Parameter                            | Value                                                             | 
|--------------------------------------|-------------------------------------------------------------------|
| Request Method                       | DELETE                                                            |
| Service Name                         | Remove                                                            |
| Service Description                  | This service removes all job configurations for a given job plan. |
| Service Context                      | /scheduler-agent/rest/jobProvision/remove                         |
| Sample                               | https://localhost:9090/scheduler-agent/rest/jobProvision/remove   |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                     |
| Payload                              | A String representation of the job plan name.                     |
| Returns                              | HTTP 200 status                                                   |
