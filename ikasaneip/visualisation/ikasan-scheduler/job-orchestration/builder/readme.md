![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Builder

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

```java
ContextTemplateBuilder contextTemplateBuilder = new ContextTemplateBuilder();

SchedulerJob job1 = contextTemplateBuilder.getSchedulerJobBuilder()
    .withJobName("Job1")
    .withAgentName("AgentName")
    .withDescription("Job1 Description")
    .build();

SchedulerJob job2 = contextTemplateBuilder.getSchedulerJobBuilder()
    .withJobName("Job2")
    .withAgentName("AgentName")
    .withDescription("Job2 Description")
    .build();

SchedulerJob job3 = contextTemplateBuilder.getSchedulerJobBuilder()
    .withJobName("Job3")
    .withAgentName("AgentName")
    .withDescription("Job3 Description")
    .build();

SchedulerJob job4 = contextTemplateBuilder.getSchedulerJobBuilder()
    .withJobName("Job4")
    .withAgentName("AgentName")
    .withDescription("Job4 Description")
    .build();

ContextTemplate contextTemplate1 = contextTemplateBuilder.withName("Context Template Name")
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
    .addSchedulerJob(job1)
    .addSchedulerJob(job2)
    .addSchedulerJob(job3)
    .addSchedulerJob(job4)
    // job locks
    .addJobLocks(contextTemplateBuilder.getJobLockBuilder().withLockName("TEST-LOCK-1")
        .withJob("context1", job1)
        .withJob("context2", job2)
        .withLockCount(1).build())
    .addJobLocks(contextTemplateBuilder.getJobLockBuilder().withLockName("TEST-LOCK-2")
        .withJob("context1", job3)
        .withJob("context2", job4).withLockCount(1).build())
    // scheduler jobs
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("Job5")
        .withAgentName("AgentName")
        .withDescription("Job5 Description")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("Job6")
        .withAgentName("AgentName")
        .withDescription("Job6 Description")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("Job7")
        .withAgentName("AgentName")
        .withDescription("Job7 Description")
        .build())
    .addSchedulerJob(contextTemplateBuilder.getSchedulerJobBuilder()
        .withJobName("Job8")
        .withAgentName("AgentName")
        .withDescription("Job8 Description")
        .build())

    // Add the job dependencies
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("Job2")
        .withAgentName("AgentName")
        .build())
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("Job3")
        .withAgentName("AgentName")
        .build())
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("Job5")
        .withAgentName("AgentName")
        .build())
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("Job1")
        .withAgentName("AgentName")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withAgentName("AgentName")
                .withJobName("Job2")
                .build())
            .addAnd(contextTemplateBuilder.getJobAndBuilder()
                .withAgentName("AgentName")
                .withJobName("Job3")
                .build()).build()).build())
    .addJobDependency(contextTemplateBuilder.getJobDependencyBuilder()
        .withJobName("Job8")
        .withAgentName("AgentName")
        .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
            .addOr(contextTemplateBuilder.getJobOrBuilder()
                .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
                    .addAnd(contextTemplateBuilder.getJobAndBuilder()
                        .withAgentName("AgentName")
                        .withJobName("Job4")
                        .build())
                    .addAnd(contextTemplateBuilder.getJobAndBuilder()
                        .withAgentName("AgentName")
                        .withJobName("Job5")
                        .build()).build()).build())
            .addOr(contextTemplateBuilder.getJobOrBuilder()
                .withLogicalGrouping(contextTemplateBuilder.getLogicalGroupingBuilder()
                    .addAnd(contextTemplateBuilder.getJobAndBuilder()
                        .withAgentName("AgentName")
                        .withJobName("Job6")
                        .build())
                    .addAnd(contextTemplateBuilder.getJobAndBuilder()
                        .withAgentName("AgentName")
                        .withJobName("Job7")
                        .build()).build()).build()).build())
        .build())
    .build();
```

```json
{
  "contexts" : [ ],
  "scheduledJobs" : [ {
    "agentName" : "scheduler-agent",
    "jobName" : "File Arrive Job",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "Job1 Description",
    "identifier" : "scheduler-agent-File Arrive Job"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-0",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-0"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-1",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-1"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-2",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-2"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-3",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-3"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-4",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-4"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-5",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-5"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-6",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-6"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-7",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-7"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-8",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-8"
  }, {
    "agentName" : "scheduler-agent",
    "jobName" : "job-9",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-job-9"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-0",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-0"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-1",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-1"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-2",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-2"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-3",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-3"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-4",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-4"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-5",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-5"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-6",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-6"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-7",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-7"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-8",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-8"
  }, {
    "agentName" : "scheduler-agent-2",
    "jobName" : "job-9",
    "childContextNames" : [ ],
    "startupControlType" : "AUTOMATIC",
    "skippedContexts" : { },
    "heldContexts" : { },
    "jobDescription" : "A simple internal job!",
    "identifier" : "scheduler-agent-2-job-9"
  } ],
  "jobLocks" : [ ],
  "name" : "test-context",
  "description" : "Context Template Description",
  "blackoutWindowDateTimeRanges" : { },
  "blackoutWindowCronExpressions" : [ ],
  "jobDependencies" : [ {
    "jobIdentifier" : "scheduler-agent-File Arrive Job"
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-0",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-File Arrive Job"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-0",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-0"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-1",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-0"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-1",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-1"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-2",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-1"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-2",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-2"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-3",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-2"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-3",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-3"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-4",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-3"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-4",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-4"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-5",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-4"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-5",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-5"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-6",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-5"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-6",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-6"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-7",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-6"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-7",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-7"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-8",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-7"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-8",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-8"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-2-job-9",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-job-8"
      } ]
    }
  }, {
    "jobIdentifier" : "scheduler-agent-job-9",
    "logicalGrouping" : {
      "and" : [ {
        "identifier" : "scheduler-agent-2-job-9"
      } ]
    }
  } ],
  "contextDependencies" : [ ],
  "contextParameters" : [ {
    "name" : "param1",
    "defaultValue" : "value"
  }, {
    "name" : "param2",
    "defaultValue" : "value"
  }, {
    "name" : "param3",
    "defaultValue" : "value"
  }, {
    "name" : "param4",
    "defaultValue" : "value"
  }, {
    "name" : "param5",
    "defaultValue" : "value"
  } ],
  "timeWindowStart" : "* * 6 ? * * *",
  "contextTtlMilliseconds" : 100000,
  "disabled" : false,
  "quartzScheduleDrivenJobsDisabledForContext" : false
}
```