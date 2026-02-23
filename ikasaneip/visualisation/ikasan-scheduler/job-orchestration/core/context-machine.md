![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Context Machine

The `ContextMachine` is a central component within the Ikasan job orchestration framework, responsible for managing the lifecycle, state, and execution of job orchestration "Context Instances" and their associated "Scheduler Job Instances". It acts as a state machine for complex job flows, handling events, managing job statuses, and coordinating with other system components.

## Overview

At its core, the `ContextMachine` orchestrates the execution of jobs defined within a `ContextTemplate`. Each running instance of a `ContextTemplate` is represented by a `ContextInstance`, and the `ContextMachine` is dedicated to managing a single `ContextInstance`. It processes events, transitions job and context states, and ensures proper execution flow, including handling retries, skips, and holds.

## Key Responsibilities

The `ContextMachine` performs several critical functions:

*   **Context and Job State Management**: It maintains and updates the status of the `ContextInstance` and all its nested `SchedulerJobInstance`s (e.g., `WAITING`, `RUNNING`, `COMPLETE`, `ERROR`, `SKIPPED`, `ON_HOLD`, `DISABLED`).
*   **Event Processing**: It listens for incoming `ContextualisedScheduledProcessEvent` messages, which represent triggers or status updates from external systems or other jobs. Upon receiving an event, it evaluates the current state and determines which subsequent `SchedulerJobInitiationEvent`s should be raised.
*   **Job Lifecycle Control**: Provides mechanisms to:
    *   **Skip Jobs**: Mark jobs as skipped, preventing their execution.
    *   **Hold Jobs**: Pause the execution of jobs, preventing them from running until explicitly released.
    *   **Release Jobs**: Allow held jobs to resume their normal execution flow.
    *   **Reset Jobs**: Reset the status of jobs, typically to `WAITING`, allowing them to be re-executed.
    *   **Acknowledge Errors**: Mark job errors as acknowledged, which can influence subsequent job execution logic.
    *   **Kill Running Jobs**: Attempt to terminate external processes associated with running jobs.
*   **Asynchronous Communication (BigQueue)**: Utilizes Ikasan's `BigQueue` for robust, asynchronous communication:
    *   **Inbound Queue**: Receives incoming `ContextualisedScheduledProcessEvent` messages.
    *   **Outbound Queue**: Enqueues `SchedulerJobInitiationEvent` messages to be consumed by agents or other `ContextMachine` instances.
    *   **Dead Letter Queue (DLQ)**: Stores messages that fail processing from the inbound queue, allowing for later inspection and resubmission.
*   **Job Locking**: Integrates with a `JobLockCache` to manage distributed locks for jobs, preventing concurrent execution of critical sections.
*   **Agent Interaction**: Communicates with external "agents" (other Ikasan modules or services) to:
    *   Propagate the current state of the `ContextInstance`.
    *   Initiate the execution of jobs on those agents.
    *   Send commands like "kill job" to agents.
*   **Persistence**: Saves the state of the `ContextInstance` to a persistent store (via `ScheduledContextInstanceService`) after significant state changes.
*   **Notification and Monitoring**: Registers with `MonitorManagement` to allow external monitoring of context and job state changes.

## Core Components and Interactions

The `ContextMachine` interacts with several other key components:

*   **`ContextInstance`**: The data model representing the current state of a job orchestration flow, including its jobs, their statuses, and parameters.
*   **`JobLogicMachine`**: A sub-component responsible for evaluating the complex logical conditions and dependencies between jobs to determine which jobs are eligible to run based on incoming events.
*   **`ContextMachineCache`**: A singleton cache that stores and provides access to active `ContextMachine` instances by their context name or instance ID.
*   **`ScheduledContextInstanceService`**: Used for persisting and retrieving `ContextInstance` states.
*   **`SchedulerJobInstanceService`**: Manages the persistence and retrieval of individual `SchedulerJobInstance` records.
*   **`ModuleMetaDataService`**: Provides metadata about registered agents, including their URLs for communication.
*   **`JobLockCache`**: Manages distributed locks for jobs.

## Lifecycle

1.  **Initialization (`init()`):**
    *   Creates and initializes the inbound, outbound, and dead-letter `BigQueue` instances.
    *   Sets up listeners to continuously process messages from the inbound and outbound queues.
    *   Registers the `ContextMachine` with `MonitorManagement` for external monitoring.
    *   Loads the initial `ContextInstance` state.
2.  **Event Processing (`eventReceived()`):**
    *   An external event (e.g., a job completion, a scheduled trigger) arrives as a `ContextualisedScheduledProcessEvent` and is enqueued into the inbound `BigQueue`.
    *   The `InboundQueueMessageRunner` (an internal component) dequeues the event.
    *   The `JobLogicMachine` evaluates the event against the current `ContextInstance` state and job dependencies.
    *   If new jobs are eligible to run, `SchedulerJobInitiationEvent`s are created and enqueued into the outbound `BigQueue`.
    *   The `OutboundQueueMessageRunner` dequeues these initiation events and sends them to the appropriate agents.
    *   The `ContextInstance` state is updated and persisted.
3.  **Termination (`teardown()`):**
    *   Gracefully shuts down internal `ExecutorService`s.
    *   Closes and clears all `BigQueue` instances, deleting their underlying files.
    *   Unregisters from `MonitorManagement`.
    *   Releases all resources.

## Configuration

The behavior of the `ContextMachine` can be influenced by various configuration parameters, such as:

*   **`queueDir`**: The directory where `BigQueue` files are stored.
*   **`executorWaitTimeoutSeconds`**: Timeout for gracefully shutting down internal executors.
*   **`blackListedMessageMaxRetries`**: The maximum number of retries for messages that repeatedly cause errors before being moved to the DLQ.

## Example Usage (Conceptual)

A `ContextMachine` is typically instantiated and managed by a higher-level service (e.g., `ContextInstanceRegistrationServiceImpl`) when a new job orchestration context is deployed or recovered. It then operates autonomously, driven by incoming events and its internal logic, to execute the defined job flow.

```java
// Conceptual instantiation and initialization
ContextMachine contextMachine = new ContextMachine(
    contextTemplate,
    contextInstance,
    scheduledContextInstanceService,
    globalEventJobInstanceMap,
    quartzScheduleDrivenJobInstanceMap,
    internalEventDrivenJobInstances,
    contextStartJobInstanceMap,
    contextTerminalJobInstanceMap,
    localEventJobInstanceMap,
    bridgingJobInstanceMap,
    "/path/to/queue/dir",
    agents,
    moduleMetaDataService,
    jobLockCache,
    contextParametersInstanceService,
    scheduledContextService,
    schedulerJobInstanceService,
    jobLockCacheInitialisationService,
    contextInstancePublicationService,
    jobUtilsService
);

contextMachine.init();

// Later, an event is received
contextMachine.eventReceived(jsonEventString);

// To hold a job
contextMachine.holdJob("myJobIdentifier", "myChildContextName");

// To release a job
contextMachine.releaseJob("myJobIdentifier", "myChildContextName");

// To terminate
contextMachine.teardown();
```
