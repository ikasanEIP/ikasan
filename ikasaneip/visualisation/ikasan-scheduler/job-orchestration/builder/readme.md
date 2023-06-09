![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Job Plan Builder

## Introduction
The builder pattern is a programming design pattern that helps simplify the process of creating complex objects. Think 
of it like a recipe or blueprint that guides the construction of an object.

In the builder pattern, you start with a builder object that defines the steps needed to create the final object. Each 
step in the process is defined as a method that sets a specific attribute or property of the object. For example, if you 
were building a car, you might have methods for setting the color, the engine type, the number of doors, and so on.

Once all the desired attributes have been set using the builder methods, the builder object is used to construct the 
final object. This final object is created with all the attributes and properties set according to the specifications 
defined in the builder object.

The builder pattern is particularly useful when you have complex objects with many attributes or properties that need 
to be set in a specific order. It also makes it easier to create variations of the same object, since you can simply 
modify the builder object to change the attributes that are set.

Overall, the builder pattern is a powerful tool for managing the complexity of object creation in software development, 
and can help make code more readable, maintainable, and efficient.

The Ikasan Enterprise Scheduler Job Plan Builder comprises to 2 elements:
- The Ikasan Enterprise Scheduler Job Plan Builder that this page refers to.
- The [Ikasan Enterprise Scheduler Scheduler Job Builder](./scheduler-job-builder.md) which builds all jobs that are required by the job plan.

## The Ikasan Enterprise Scheduler Job Plan Builder
The Ikasan Enterprise Scheduler Job Plan Builder provides a convenient way for developers to build the 
[job plan data model](../model/job-plan-data-model.md).

In order to use the Ikasan Enterprise Scheduler Job Plan Builder include the following POM in your project:

```xml
<dependency>
    <groupId>org.ikasan</groupId>
    <artifactId>ikasan-job-orchestration-builder</artifactId>
    <version>${version.ikasan}</version>
</dependency>
```

### Ikasan Enterprise Scheduler Job Plan Builder Sample
The code below demonstrates how to use the builder classes to build a job plan.

```java
ContextTemplateBuilder contextTemplateBuilder = new ContextTemplateBuilder();

ContextTemplate contextTemplate1 = contextTemplateBuilder.withName("test-context")
    .withDescription("Context Template Description")
    .withTimeWindowStartCronExpression("* * 6 ? * * *")
    .withContextTtlMilliseconds(100000L)

    // add some context parameters
    .addContextParameter(contextTemplateBuilder.getContextParameterBuilder().withName("param1").withDefaultValue("value").build())
    .addContextParameter(contextTemplateBuilder.getContextParameterBuilder().withName("param2").withDefaultValue("value").build())
    .addContextParameter(contextTemplateBuilder.getContextParameterBuilder().withName("param3").withDefaultValue("value").build())
    .addContextParameter(contextTemplateBuilder.getContextParameterBuilder().withName("param4").withDefaultValue("value").build())
    .addContextParameter(contextTemplateBuilder.getContextParameterBuilder().withName("param5").withDefaultValue("value").build())

    // add the scheduler jobs that will be orchestrated
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("File Arrive Job")
        .withAgentName("scheduler-agent")
        .withDescription("Job1 Description")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-0")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-1")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-2")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-3")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-4")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-5")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-6")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-7")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-8")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-9")
        .withAgentName("scheduler-agent")
        .withDescription("A simple internal job!")
        .build())

    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-0")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-1")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-2")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-3")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-4")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-5")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-6")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-7")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-8")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("job-9")
        .withAgentName("scheduler-agent-2")
        .withDescription("A simple internal job!")
        .build())

    // Add the job dependencies
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("File Arrive Job")
        .withAgentName("scheduler-agent")
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-0")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("File Arrive Job")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-0")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-0")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-1")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-0")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-1")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-1")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-2")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-1")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-2")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-2")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-3")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-2")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-3")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-3")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-4")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-3")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-4")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-4")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-5")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-4")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-5")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-5")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-6")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-5")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-6")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-6")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-7")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-6")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-7")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-7")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-8")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-7")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-8")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-8")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-9")
        .withAgentName("scheduler-agent-2")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-8")
                .withAgentName("scheduler-agent")
                .build())
            .build())
        .build())

    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("job-9")
        .withAgentName("scheduler-agent")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withJobName("job-9")
                .withAgentName("scheduler-agent-2")
                .build())
            .build())
        .build())

    .build();

```

#### Ikasan Enterprise Scheduler Job Plan JSON Sample
The JSON below is generated from the object model created by the above Ikasan Enterprise Scheduler Job Plan Builder example.

```json
{
    "contexts": [],
    "scheduledJobs": [
        {
            "agentName": "scheduler-agent",
            "jobName": "File Arrive Job",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "Job1 Description",
            "identifier": "scheduler-agent-File Arrive Job"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-0",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-0"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-1",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-1"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-2",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-2"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-3",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-3"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-4",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-4"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-5",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-5"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-6",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-6"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-7",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-7"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-8",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-8"
        },
        {
            "agentName": "scheduler-agent",
            "jobName": "job-9",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-job-9"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-0",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-0"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-1",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-1"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-2",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-2"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-3",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-3"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-4",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-4"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-5",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-5"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-6",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-6"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-7",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-7"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-8",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-8"
        },
        {
            "agentName": "scheduler-agent-2",
            "jobName": "job-9",
            "startupControlType": "AUTOMATIC",
            "jobDescription": "A simple internal job!",
            "identifier": "scheduler-agent-2-job-9"
        }
    ],
    "jobLocks": [],
    "name": "test-context",
    "description": "Context Template Description",
    "jobDependencies": [
        {
            "jobIdentifier": "scheduler-agent-File Arrive Job"
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-0",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-File Arrive Job"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-0",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-0"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-1",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-0"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-1",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-1"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-2",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-1"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-2",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-2"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-3",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-2"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-3",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-3"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-4",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-3"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-4",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-4"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-5",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-4"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-5",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-5"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-6",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-5"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-6",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-6"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-7",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-6"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-7",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-7"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-8",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-7"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-8",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-8"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-2-job-9",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-job-8"
                    }
                ]
            }
        },
        {
            "jobIdentifier": "scheduler-agent-job-9",
            "logicalGrouping": {
                "and": [
                    {
                        "identifier": "scheduler-agent-2-job-9"
                    }
                ]
            }
        }
    ],
    "contextParameters": [
        {
            "name": "param1",
            "defaultValue": "value"
        },
        {
            "name": "param2",
            "defaultValue": "value"
        },
        {
            "name": "param3",
            "defaultValue": "value"
        },
        {
            "name": "param4",
            "defaultValue": "value"
        },
        {
            "name": "param5",
            "defaultValue": "value"
        }
    ],
    "timeWindowStart": "* * 6 ? * * *",
    "contextTtlMilliseconds": 100000,
    "treeViewExpandLevel": 1,
    "ableToRunConcurrently": true,
    "disabled": false,
    "quartzScheduleDrivenJobsDisabledForContext": false
}
```