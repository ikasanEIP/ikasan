![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler Job Plan Provision Services
This Ikasan Enterprise Scheduler Job Plan Provision Services contains services that assist in the provisioning of 
job plans.
- [Job Plan Provision Service](#job-plan-provision-service)

## Job Plan Provision Service

| Parameter                            | Value                                                                                                                                                                           | 
|--------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Request Method                       | PUT                                                                                                                                                                             |
| Service Name                         | Job Plan Provision Service                                                                                                                                                      |
| Service Description                  | This service accepts a json String representation of [ContextBundle](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/context/model/ContextBundle.java). |
| Service Context                      | /rest/provision/context                                                                                                                                                         |
| Sample                               | https://localhost:9090/rest/provision/context                                                                                                                                   |
| Requires 'Authorization' HTTP Header | Basic {TOKEN}                                                                                                                                                                   |
| Returns                              | HTTP 200 status                                                                                                                                                                 |

### Sample Payload
<details>
    <summary>Click to view the sample JSON payload.</summary>
<p>

```json
[
    "org.ikasan.job.orchestration.model.context.ContextBundleImpl",
    {
        "contextTemplate": [
            "org.ikasan.job.orchestration.model.context.ContextTemplateImpl",
            {
                "contexts": [
                    "java.util.ArrayList",
                    [
                        [
                            "org.ikasan.job.orchestration.model.context.ContextTemplateImpl",
                            {
                                "contexts": [
                                    "java.util.ArrayList",
                                    []
                                ],
                                "scheduledJobs": [
                                    "java.util.ArrayList",
                                    [
                                        [
                                            "org.ikasan.job.orchestration.model.job.SchedulerJobImpl",
                                            {
                                                "agentName": "scheduler-agent",
                                                "jobName": "jobName1",
                                                "startupControlType": "AUTOMATIC",
                                                "identifier": "scheduler-agent-jobName1"
                                            }
                                        ],
                                        [
                                            "org.ikasan.job.orchestration.model.job.SchedulerJobImpl",
                                            {
                                                "agentName": "scheduler-agent",
                                                "jobName": "jobName2",
                                                "startupControlType": "AUTOMATIC",
                                                "identifier": "scheduler-agent-jobName2"
                                            }
                                        ],
                                        [
                                            "org.ikasan.job.orchestration.model.job.SchedulerJobImpl",
                                            {
                                                "agentName": "scheduler-agent",
                                                "jobName": "jobName3",
                                                "startupControlType": "AUTOMATIC",
                                                "identifier": "scheduler-agent-jobName3"
                                            }
                                        ]
                                    ]
                                ],
                                "jobLocks": [
                                    "java.util.ArrayList",
                                    []
                                ],
                                "name": "CONTEXT-NO-1",
                                "jobDependencies": [
                                    "java.util.ArrayList",
                                    [
                                        [
                                            "org.ikasan.job.orchestration.model.context.JobDependencyImpl",
                                            {
                                                "jobIdentifier": "scheduler-agent-jobName0",
                                                "logicalGrouping": [
                                                    "org.ikasan.job.orchestration.model.context.LogicalGroupingImpl",
                                                    {
                                                        "and": [
                                                            "java.util.ArrayList",
                                                            [
                                                                [
                                                                    "org.ikasan.job.orchestration.model.context.AndImpl",
                                                                    {
                                                                        "identifier": "scheduler-agent-jobName1"
                                                                    }
                                                                ],
                                                                [
                                                                    "org.ikasan.job.orchestration.model.context.AndImpl",
                                                                    {
                                                                        "identifier": "scheduler-agent-jobName2"
                                                                    }
                                                                ],
                                                                [
                                                                    "org.ikasan.job.orchestration.model.context.AndImpl",
                                                                    {
                                                                        "identifier": "scheduler-agent-jobName3"
                                                                    }
                                                                ]
                                                            ]
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    ]
                                ],
                                "contextTtlMilliseconds": 0,
                                "treeViewExpandLevel": 1,
                                "ableToRunConcurrently": true,
                                "disabled": false,
                                "quartzScheduleDrivenJobsDisabledForContext": false
                            }
                        ]
                    ]
                ],
                "scheduledJobs": [
                    "java.util.ArrayList",
                    []
                ],
                "jobLocks": [
                    "java.util.ArrayList",
                    []
                ],
                "name": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                "description": "Context Template Description",
                "timeWindowStart": "* * 6 ? * * *",
                "contextTtlMilliseconds": 0,
                "treeViewExpandLevel": 1,
                "ableToRunConcurrently": true,
                "disabled": false,
                "quartzScheduleDrivenJobsDisabledForContext": false
            }
        ],
        "schedulerJobs": [
            "java.util.ArrayList",
            [
                [
                    "org.ikasan.job.orchestration.model.job.FileEventDrivenJobImpl",
                    {
                        "agentName": "scheduler-agent",
                        "jobName": "jobName2",
                        "startupControlType": "AUTOMATIC",
                        "cronExpression": "0 0 * ? * * *",
                        "ignoreMisfire": true,
                        "eager": false,
                        "maxEagerCallbacks": 0,
                        "passthroughProperties": [
                            "java.util.HashMap",
                            {}
                        ],
                        "persistentRecovery": true,
                        "recoveryTolerance": 1800000,
                        "filenames": [
                            "java.util.ArrayList",
                            [
                                "/some/file/path/name.txt"
                            ]
                        ],
                        "includeHeader": false,
                        "includeTrailer": false,
                        "sortByModifiedDateTime": false,
                        "sortAscending": true,
                        "directoryDepth": 1,
                        "logMatchedFilenames": false,
                        "ignoreFileRenameWhilstScanning": true,
                        "minFileAgeSeconds": 0,
                        "dropEventOnBlackout": false,
                        "identifier": "scheduler-agent-jobName2"
                    }
                ],
                [
                    "org.ikasan.job.orchestration.model.job.InternalEventDrivenJobImpl",
                    {
                        "agentName": "scheduler-agent",
                        "jobName": "jobName1",
                        "startupControlType": "AUTOMATIC",
                        "minExecutionTime": 10,
                        "maxExecutionTime": 10000,
                        "contextParameters": [
                            "java.util.ArrayList",
                            []
                        ],
                        "targetResidingContextOnly": false,
                        "participatesInLock": false,
                        "jobRepeatable": false,
                        "identifier": "scheduler-agent-jobName1"
                    }
                ],
                [
                    "org.ikasan.job.orchestration.model.job.QuartzScheduleDrivenJobImpl",
                    {
                        "agentName": "scheduler-agent",
                        "jobName": "jobName3",
                        "startupControlType": "AUTOMATIC",
                        "cronExpression": "0 0 * ? * * *",
                        "ignoreMisfire": true,
                        "eager": false,
                        "maxEagerCallbacks": 0,
                        "passthroughProperties": [
                            "java.util.HashMap",
                            {}
                        ],
                        "persistentRecovery": true,
                        "recoveryTolerance": 1800000,
                        "dropEventOnBlackout": false,
                        "identifier": "scheduler-agent-jobName3"
                    }
                ]
            ]
        ],
        "contextProfiles": [
            "java.util.ArrayList",
            []
        ],
        "emailNotificationDetails": [
            "java.util.ArrayList",
            [
                [
                    "org.ikasan.job.orchestration.model.notification.EmailNotificationDetailsImpl",
                    {
                        "jobName": "jobName1",
                        "contextName": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                        "childContextName": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                        "monitorType": "ERROR",
                        "emailNotificationTemplateParameters": [
                            "java.util.HashMap",
                            {
                                "EMAIL_BODY_TEXT": "Testing1",
                                "EMAIL_SUBJECT_TEXT": "Testing1"
                            }
                        ],
                        "emailSendTo": [
                            "java.util.ArrayList",
                            [
                                "test@doesnotexist",
                                "test2@doesnotexist"
                            ]
                        ],
                        "emailSubject": "",
                        "emailBody": "",
                        "emailBodyTemplate": "",
                        "html": false
                    }
                ],
                [
                    "org.ikasan.job.orchestration.model.notification.EmailNotificationDetailsImpl",
                    {
                        "jobName": "jobName3",
                        "contextName": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                        "childContextName": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                        "monitorType": "ERROR",
                        "emailNotificationTemplateParameters": [
                            "java.util.HashMap",
                            {
                                "EMAIL_BODY_TEXT": "Testing3",
                                "EMAIL_SUBJECT_TEXT": "Testing3"
                            }
                        ],
                        "emailSendTo": [
                            "java.util.ArrayList",
                            [
                                "test@doesnotexist",
                                "test2@doesnotexist"
                            ]
                        ],
                        "emailSubject": "",
                        "emailBody": "",
                        "emailBodyTemplate": "",
                        "html": false
                    }
                ]
            ]
        ],
        "emailNotificationContext": [
            "org.ikasan.job.orchestration.model.notification.EmailNotificationContextImpl",
            {
                "contextName": "CONTEXT-NOT-SO-COMPLEX-WITH-NOTIFICATIONS",
                "monitorTypes": [
                    "java.util.ArrayList",
                    [
                        "START",
                        "COMPLETE",
                        "ERROR",
                        "OVERDUE",
                        "RUNNING_TIME"
                    ]
                ],
                "emailSendTo": [
                    "java.util.ArrayList",
                    [
                        "some@email.com"
                    ]
                ],
                "emailSendToByMonitorType": [
                    "java.util.HashMap",
                    {
                        "OVERDUE": [
                            "java.util.ArrayList",
                            [
                                "some@email.com"
                            ]
                        ]
                    }
                ],
                "emailSendCcByMonitorType": [
                    "java.util.HashMap",
                    {
                        "COMPLETE": [
                            "java.util.ArrayList",
                            [
                                "some@email.com"
                            ]
                        ]
                    }
                ],
                "emailSendBccByMonitorType": [
                    "java.util.HashMap",
                    {}
                ],
                "emailSubjectNotificationTemplate": [
                    "java.util.HashMap",
                    {
                        "COMPLETE": "/home/userHome/notification-complete-email-subject-template.txt",
                        "ERROR": "/home/userHome/notification-error-email-subject-template.txt",
                        "START": "/home/userHome/notification-start-email-subject-template.txt",
                        "RUNNING_TIME": "/home/userHome/notification-runningtimes-email-subject-template.txt",
                        "OVERDUE": "/home/userHome/notification-overdue-email-subject-template.txt"
                    }
                ],
                "emailBodyNotificationTemplate": [
                    "java.util.HashMap",
                    {
                        "COMPLETE": "/home/userHome/notification-complete-email-body-template.txt",
                        "ERROR": "/home/userHome/notification-error-email-body-template.txt",
                        "START": "/home/userHome/notification-start-email-body-template.txt",
                        "RUNNING_TIME": "/home/userHome/notification-runningtimes-email-body-template.txt",
                        "OVERDUE": "/home/userHome/notification-overdue-email-body-template.txt"
                    }
                ],
                "html": false
            }
        ]
    }
]
```
</p>
</details>