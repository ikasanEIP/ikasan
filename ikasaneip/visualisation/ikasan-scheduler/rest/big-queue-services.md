![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Big Queue Services
Big Queue is the underlying queuing mechanism that Ikasan employs to provide reliable messaging between agents and the dashboard.
Ikasan exposes a number of services that allows clients to inspect and manage biq queues associated with Scheduler Agents and the Scheduler Dashboard.

## Job Plan Status Service
| Parameter                            | Value                                                                                                                                                              | 
|--------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | GET                                                                                                                                                                |
| Service Name                         | Context Instance Status                                                                                                                                            |
| Service Description                  | This service returns a json payload containing the detailed state of a Job Plan or a nested context within it.                                                     |
| Service Context                      | /rest/context/status/json/{job-plan-name}/{context-name}                                                                                                           |
| Sample                               | https://localhost:9090/rest/context/status/json/-1793100514/CONTEXT-1799117601                                                                                     |
| Requires Path parameter job-plan-name   | The parent job plan name.                                                                                                                                          |
| Requires Path parameter context-name   | The nested context name. If this is the same as the job-plan-name, then the entire job plan will be return, otherwise the child will be.                           |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                      |
| Returns                              | HTTP 200 status and json serialised [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java)       |

### Sample Curl Command
``` text

```

### Sample Response
```json

```
### Context Instance Implementation
If your consuming client runs on the Java ecosystem, you can import an implementation of a [ContextInstance](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/instance/model/ContextInstance.java) into your project
using the following pom.
``` xml

```

The following snippet of code will de-serialise the response json.
``` java

```