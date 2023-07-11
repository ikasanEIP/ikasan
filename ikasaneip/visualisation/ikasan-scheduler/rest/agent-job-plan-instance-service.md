![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Agent Job Plan Instance Services

- [Publish Job Plan Instance to Agent](#publish-job-plan-instance-to-agent)
- [Remove a Job Plan Instance from an Agent](#remove-a-job-plan-instance-from-an-agent)
- [Managing queues through the Scheduler Agent](#managing-queues-through-the-scheduler-agent)

## Managing Job Plan Instances Associated with an Agent

### Publish Job Plan Instance to Agent
An agent always need to be aware of job plan instances that are currently running as part of the job plan lifecycle.
This service is exposed to allow the Ikasan Enterprise Scheduler Dashboard to publish job plan instances to it when they are 
created or recovered.

| Parameter                            | Value                                                                                                                                            | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                              |
| Service Name                         | Save                                                                                                                                             |
| Service Description                  | This service publishes job plan instance to an agent.                                                                                            |
| Service Context                      | /scheduler-agent/rest/contextInstance/save                                                                                                       |
| Sample                               | https://localhost:9090/scheduler-agent/rest/contextInstance/save                                                                                 |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                    |
| Payload                              | A json serialised [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java) |
| Returns                              | HTTP 200 status                                                                                                                                  |

### Remove a Job Plan Instance from an Agent
When a job plan is ended, the Ikasan Enterprise Scheduler Dashboard removes the job plan instance from the agent. This service
is responsible for removing a job plan instance based upon its identifier.

| Parameter                            | Value                                                                       | 
|--------------------------------------|-----------------------------------------------------------------------------|
| Request Method                       | DELETE                                                                      |
| Service Name                         | Remove                                                                      |
| Service Description                  | This service removes a job plan instance from an agent.                     |
| Service Context                      | /scheduler-agent/rest/contextInstance/remove/{job-plan-instance-identifier} |
| Sample                               | https://localhost:9090/scheduler-agent/rest/contextInstance/remove/         |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                               |
| Payload                              | A String representation of the job plan instance identifier                 |
| Returns                              | HTTP 200 status                                                             |


### Remove all Job Plan Instances from an Agent
This service allows the Ikasan Enterprise Scheduler Dashboard to remove all job plan insances from an agent
as part of the job plan instance lifecycle.

| Parameter                            | Value                                                                 | 
|--------------------------------------|-----------------------------------------------------------------------|
| Request Method                       | DELETE                                                                |
| Service Name                         | Remove All                                                            |
| Service Description                  | This service removes a job plan instance from an agent.               |
| Service Context                      | /scheduler-agent/rest/contextInstance/removeAll                       |
| Sample                               | https://localhost:9090/scheduler-agent/rest/contextInstance/removeAll |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                         |
| Payload                              | N/A                                                                   |
| Returns                              | HTTP 200 status                                                       |