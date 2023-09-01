![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Job Plan Instance Orchestration

## Introduction
Job Plan Instance Orchestration is performed by a component in the Ikasan Enterprise Scheduler called the ContextMachine. 
The ContextMachine employs recursion in order to process received events, and determine based on the state of the event,
whether there are any jobs can be instructed to run.

## What is Recursion?
Recursion is a programming and mathematical concept where a function or algorithm calls itself in order to solve a problem. 
Instead of solving the entire problem at once, a recursive function breaks it down into smaller, more manageable sub-problems 
and solves each of these sub-problems recursively. These sub-problems should be similar in nature to the original problem but 
with smaller inputs.

A recursive function typically consists of two parts:

- Base Case: This is the termination condition that specifies when the recursion should stop. When the base case is met, the function stops calling itself and returns a result.
- Recursive Case: In this part of the function, the function calls itself with a modified or smaller version of the original problem. This step is essential to make progress toward reaching the base case.

Recursion is often used in solving problems that exhibit a natural recursive structure, such as problems involving trees, graphs, 
or nested data structures. Common examples of recursive algorithms include computing factorials, traversing binary trees, and 
implementing various sorting and searching algorithms like quicksort and binary search.

## What events are received by the ContextMachine?
The context machine receives [ContextualisedScheduledProcessEvents](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ContextualisedScheduledProcessEvent.java). 
The Ikasan Enterprise Scheduler Dashboard Inbound [sequence diagram](../../architecture.md#ikasan-enterprise-scheduler-dashboard-inbound)
provides details of how ContextualisedScheduledProcessEvents are received from Ikasan Enterprise Scheduler Agents, and 
subsequently processed by the context machine. These events are received via the [Scheduled Event Service](../../rest/scheduled-process-event-service.md#scheduled-event-service)
that is exposed by the Ikasan Enterprise Scheduler Dashboard.

## How does the ContextMachine instruct Ikasan Enterprise Scheduler Agents to run jobs?
After the ContextMachine assesses the inbound ContextualisedScheduledProcessEvents, one or more 
[ContextualisedSchedulerJobInitiationEvents](../../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/event/model/ContextualisedSchedulerJobInitiationEvent.java)
are created and these events are published to the relevant listener as seen in the following [sequence diagram](../../architecture.md#ikasan-enterprise-scheduler-dashboard-inbound).
The listener then publishes the ContextualisedSchedulerJobInitiationEvents to the relevant Ikasan Enterprise Scheduler Agent
via the [Scheduler Job Initiation Service](../../rest/scheduler-job-initiation-service.md#scheduler-job-initiation-service).

## How does the ContextMachine determine if there are any job initiation events to raise?
In order to gain an understanding of how the Job Plan Instance Orchestration works, it is incredibly important to understand
the following core data models:
- [Job Plan Data Model](../model/job-plan-data-model.md)
- [Job Plan Instance Data Model](../model/job-plan-instance-data-model.md)

It is also important to understand the data structures within the Job Plan Data Model:
- [Logical Grouping](../model/job-plan-data-model.md#logical-grouping)
- [Job Dependency](../model/job-plan-data-model.md#job-dependency)
- [Job Dependencies](../model/job-plan-data-model.md#job-dependencies)

```java
public class AbstractLogicMachine<STATEFUL_ENTITY extends StatefulEntity> {

    /**
     * Assess the outcome of the And grouping within the LogicalGrouping.
     *
     * @param logicalGrouping
     * @param statefulEntityMap
     * @return
     */
    protected boolean assessAnd(LogicalGrouping logicalGrouping, Map<String, STATEFUL_ENTITY> statefulEntityMap) {
        AtomicBoolean and = new AtomicBoolean(false);

        if(logicalGrouping.getAnd() != null && !logicalGrouping.getAnd().isEmpty()) {
            and.set(true);
            logicalGrouping.getAnd().forEach(operator -> {
                if(operator.getLogicalGrouping() != null) {
                    if(!this.assessBaseLogic(operator.getLogicalGrouping(), statefulEntityMap)) {
                        and.set(false);
                    }
                }
                else {
                    STATEFUL_ENTITY statefulEntity = statefulEntityMap.get(operator.getIdentifier());
                    if (statefulEntity == null) {
                        throw new ContextMachineException(String.format("Could not locate stateful entity[%s] when trying to assess logical group and[%s]",
                            operator.getIdentifier(), logicalGrouping));
                    }

                    if (!statefulEntity.getStatus().equals(InstanceStatus.COMPLETE) && !statefulEntity.getStatus().equals(InstanceStatus.SKIPPED_COMPLETE)) {
                        and.set(false);
                    }
                }
            });
        }

        return and.get();
    }

    /**
     * Assess the outcome of the Or grouping within the LogicalGrouping.
     *
     * @param logicalGrouping
     * @param statefulEntityMap
     * @return
     */
    protected boolean assessOr(LogicalGrouping logicalGrouping, Map<String, STATEFUL_ENTITY> statefulEntityMap) {
        AtomicBoolean or = new AtomicBoolean(false);
        if(logicalGrouping.getOr() != null && !logicalGrouping.getOr().isEmpty()) {
            logicalGrouping.getOr().forEach(operator -> {
                if(operator.getLogicalGrouping() != null) {
                    if(this.assessBaseLogic(operator.getLogicalGrouping(), statefulEntityMap)) {
                        or.set(true);
                    }
                }
                else {
                    STATEFUL_ENTITY statefulEntity = statefulEntityMap.get(operator.getIdentifier());
                    if (statefulEntity == null) {
                        throw new ContextMachineException(String.format("Could not locate stateful entity[%s] when trying to assess logical group or[%s]",
                            operator.getIdentifier(), logicalGrouping));
                    }

                    if (statefulEntity.getStatus().equals(InstanceStatus.COMPLETE) || statefulEntity.getStatus().equals(InstanceStatus.SKIPPED_COMPLETE)) {
                        or.set(true);
                    }
                }
            });
        }

        return or.get();
    }

    /**
     * Assess the outcome of the Not grouping within the LogicalGrouping.
     *
     * @param logicalGrouping
     * @param statefulEntityMap
     * @return
     */
    protected boolean assessNot(LogicalGrouping logicalGrouping, Map<String, STATEFUL_ENTITY> statefulEntityMap) {
        AtomicBoolean not = new AtomicBoolean(false);
        if(logicalGrouping.getNot() != null && !logicalGrouping.getNot().isEmpty()) {
            logicalGrouping.getNot().forEach(operator -> {
                if(operator.getLogicalGrouping() != null) {
                    if(this.assessBaseLogic(operator.getLogicalGrouping(), statefulEntityMap)) {
                        not.set(true);
                    }
                }
                else {
                    STATEFUL_ENTITY statefulEntity = statefulEntityMap.get(operator.getIdentifier());
                    if (statefulEntity == null) {
                        throw new ContextMachineException(String.format("Could not locate stateful entity[%s] when trying to assess logical group or[%s]",
                            operator.getIdentifier(), logicalGrouping));
                    }

                    if (statefulEntity.getStatus().equals(InstanceStatus.COMPLETE) || statefulEntity.getStatus().equals(InstanceStatus.SKIPPED_COMPLETE)) {
                        not.set(true);
                    }
                }
            });
        }

        return not.get();
    }

    /**
     * This method allows us to have an infinite depth of logical groupings and facilitates the recursion that supports that.
     *
     * @param logicalGrouping
     * @param statefulEntityMap
     * @return
     */
    protected boolean assessBaseLogic(LogicalGrouping logicalGrouping, Map<String, STATEFUL_ENTITY> statefulEntityMap) {
        // Here we assess the logic at the current level of the recursion.
        boolean andAssessment = this.assessAnd(logicalGrouping, statefulEntityMap);
        boolean orAssessment = this.assessOr(logicalGrouping, statefulEntityMap);
        boolean notAssessment = this.assessNot(logicalGrouping, statefulEntityMap);

        // Now apply a very simple logical statement to feed back to either the
        // originator or the recursive level above.
        return ((andAssessment || orAssessment) && !notAssessment);
    }
}
```
