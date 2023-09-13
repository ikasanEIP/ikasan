![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Job Plan Lifecycle

## Introduction
This page outlines the life cycle of a job plan in the scheduler dashboard and scheduler agent.

## Core Concepts
There are a number of core concepts relating to the Ikasan Scheduler.

### Job Plan
[Job Plan Data Model](../model/job-plan-data-model.md) outlines how complex job orchestrations are organised.

### Job Plan Instance
[Job Plan Instance Data Model](../model/job-plan-instance-data-model.md) outlines what a job plan looks like when it is instantiated.

### Scheduler Jobs
[Scheduler Job Data Model](../model/scheduler-job-data-model.md) outlines the different jobs that are supported by the Ikasan Scheduler.

### Context Machine
The ContextMachine is at the core of the Ikasan Scheduler. There is an individual context machine created for each job plan instance. 
The context machine is responsible for managing a job plan instance. It does this by listening for scheduler events and applying logic 
against the job plan instance to determine if there are any jobs that can be initiated as a result of the event received. If there are, 
the context machine notifies any listeners of those job initiations.

As each event is received and a state change occurs against the underlying job plan instance, the job plan instance 
is updated and persisted, as well as there being an audit record created for each transition in order to provide complete transparency 
and auditability of the behaviour of the Ikasan Scheduler. 

The [job plan orchestration](./job-plan-orchestration.md) readme provides further details on how the context machine orchestrates job plans.

![context-machine](../../../images/context-machine-white.png)

*Ikasan Enterprise Scheduler context machine event processing*

## Why is a Job Plan Lifecycle Necessary?
Ikasan employs a distributed micro service architecture, with which comes many benefits such as a highly decoupled system, robustness, 
resiliency, scalability and reliability.

However, a distributed system is also burdened with more complexity due to:

- nodes in the architecture may need to send state to one another
- nodes may need to adhere to certain startup and shut down protocols in order make sure that data integrity is maintaned.

More specifically, the Ikasan Scheduler has some challenges relating to job plan instances and the way that details of a job plan instance, 
such as parameters are shared between the dashboard and it's agents. This is even more exaggerated by the fact two of the Ikasan Scheduler 
jobs types, Quartz Driven Jobs and File Received Driven Jobs are completely decoupled from the lifecycle of the underlying job plan within 
which they are being executed. 

## How is the Job Plan Lifecycle implemented?
The Ikasan Enterprise Scheduler has an embedded scheduler component that each managed `job plan` is registered with using 
the `timeWindowStart` cron expression, along with the `timezone`. When the `timeWindowStart` cron expression fires a new
instance of the `job plan`, along with an instance of all of its constituent `scheduler jobs` are created. Another task is
registered with the embedded scheduler that will tear down the newly created instance once the `contextTtlMilliseconds` expires. 

```json
{
    "name": "MyJobPlan",
    "description": "Context Template Description",
    "timeWindowStart" : "0 0 1 ? * * *",
    "contextTtlMilliseconds" : 86400000,
    "timezone": "Europe/London",
    "ableToRunConcurrently": true,

```
The Ikasan Enterprise Scheduler supports concurrent execution of a `job plan`, a feature which can be enabled/disabled using the
`ableToRunConcurrently` flag.

![img.png](../../../images/job-plan-lifecycle-white.png)

*Ikasan Enterprise Scheduler Dashboard job plans and job plan instances*

The Ikasan Enterprise Scheduler Dashboard has rest services exposed that allows agents to request `job plan instances` from it. It 
also has an internal `context machine cache`, that holds context machines holding all job plans that are currently active.

![img.png](../../../images/ikasan-scheduler-dashboard-with-context-machine-cache-white.png)

*The Ikasan Enterprise Scheduler Dashboard with the job plan instances cache and exposed rest service*

The Ikasan Enterprise Scheduler Agent has rest services exposed that allows `job plan instances` to be published to it, as well
as `job plan instances` to be removed. Each agent also has an internal `job plan instance cache`, that holds all job plans that
are currently active.

![img.png](../../../images/agent-context-instance-cache-white.png)

*The Ikasan Enterprise Scheduler Agent with the job plan instances cache and exposed rest service*

## Understanding the Job Plan Lifecycle
Below we can see a number of use cases that must be fulfilled in order to provide a robust job plan lifecycle, along with 
solutions for each use case. 

The solution is predicated on the following:

- File and time based events cannot fire until the agent has been provided with an active job plan instance.

### Use Case 1: Dashboard restart occurs prior to job plan start time window or after end time window

![dashboaed-restart-happy-path](../../../images/dashboard-restart-lifecycle-white.png)

*Ikasan Enterprise Scheduler Dashboard restart with job plan registration*

### Use Case 2: Dashboard restart occurs after context start time window, but prior to job plan end time window

![dashboaed-restart-happy-path-2](../../../images/dashboard-restart-after-context-start-white.png)

*Ikasan Enterprise Scheduler Dashboard restart with job plan recovery*

### User Case 3: Agent restart occurs

![agent-restart](../../../images/agent-restart-white.png)

*Ikasan Enterprise Scheduler Agent restart*