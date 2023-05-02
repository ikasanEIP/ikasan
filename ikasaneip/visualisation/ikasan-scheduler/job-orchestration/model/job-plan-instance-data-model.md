![IKASAN](../../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Enterprise Scheduler - Job Plan Instance Data Model

## Introduction
Scheduler Context Instantiation
Fields
contextInstantiation
A wrapper of a context with extra metadata relating to the instantiation.

Field	Type	Description	Example
id	String	The unique identifier of the context instance.	
{
  "id": "123453",
  "createdTime": 1633167000000,
  "updatedTime": 1633177000000,
  "startTime": 1633167000000,
  "endTime": 1633177000000,
  "timezone": "europe/london",
  "status": "running",
  "context": {....}
}
createdTime	Long	Time that the instance was created in milliseconds since epoch.
updatedTime	Long	Last time that the instance updated in milliseconds since epoch.
startTime	Long	Start time of the instance in milliseconds since epoch.
endTime	Long	End time of the instance in milliseconds since epoch.
timezone	String	Timezone in which the context instance was run.
status	String	
The overall status of the instance.

waiting
running
complete
error
context
A context instance is an extension of a context with the following additional fields.

Field	Type	Description	Example
startTime	String	Start time of the instance in milliseconds since epoch.	
"context": {
    "startTime": 1633167000000,
    "endTime": 1633177000000,
    "name": "Context1",
    "status": "waiting",
    "contextParameters": [....],
    "scheduledJobs": [....],
    "jobDependencies": [....],
    "contexts": [....],
    "contextDependencies": [....]
  }
endTime	Long	End time of the instance in milliseconds since epoch.
status	String	
The overall status of the instance.

waiting
running
complete
error
contextParameter
A contextParameter instance is an extension of a contextParameter with the following additional fields.

Field	Type	Description	Example
value	String	The value of the contextParameter instance.	
{
  "name": "name",
  "type": "type",
  "value": "value"
}
scheduledJob
A contextParameter instance is an extension of a contextParameter with the following additional fields.

Field	Type	Description	Example
held	Boolean	Boolean value to indicate if the job is held.	
{
  "identifier": "agentName15-jobName15",
  "agentName": "agentName15",
  "jobName": "jobName15",
  "held": false,
  "disabled": false,
  "scheduledProcessEvent": {
    "agentName": "agentName1",
    "jobName": "jobName1",
    "jobGroup": "group",
    "jobDescription": "description",
    "commandLine": "ls -la",
    "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
    "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
    "pid": 7427,
    "user": null,
    "fireTime": 1633166700001,
    "nextFireTime": 1633167000000,
    "successful": true,
    "completionTime": 1633166700022,
    "returnCode": 0,
    "outcome": "EXECUTION_INVOKED"
  }
}
disabled	Boolean	Boolean value to indicate if the job is disabled.
scheduledProcessEvent	scheduledProcessEvent	The event representing the state of the job after it has been executed.
Putting it All Together
{
  "id": "123453",
  "createdTime": 1633167000000,
  "updatedTime": 1633177000000,
  "startTime": 1633167000000,
  "endTime": 1633177000000,
  "timezone": "europe/london",
  "status": "running",
  "context": {
    "id": "123453",
    "timezone": "europe/london",
    "startTime": 1633167000000,
    "endTime": 1633177000000,
    "name": "Context1",
    "status": "waiting",
    "contextParameters": [
      {
        "name": "name",
        "type": "type",
        "value": "value"
      },
      {
        "name": "name",
        "type": "type",
        "value": "value"
      },
      {
        "name": "name",
        "type": "type",
        "value": "value"
      }
    ],
    "scheduledJobs": [
    ],
    "jobDependencies": [
    ],
    "contexts": [
      {
        "name": "Context2",
        "startTime": 1633167000000,
        "endTime": 1633177000000,
        "status": "complete",
        "contextParameters": [
          {
            "name": "name",
            "type": "type",
            "value": "value"
          },
          {
            "name": "name",
            "type": "type",
            "value": "value"
          },
          {
            "name": "name",
            "type": "type",
            "value": "value"
          }
        ],
        "scheduledJobs": [
        ],
        "jobDependencies": [
        ],
        "contexts": [
          {
            "name": "Context3",
            "startTime": 1633167000000,
            "endTime": 1633177000000,
            "contextParameters": [
              {
                "name": "name",
                "type": "type",
                "value": "value"
              },
              {
                "name": "name",
                "type": "type",
                "value": "value"
              },
              {
                "name": "name",
                "type": "type",
                "value": "value"
              }
            ],
            "scheduledJobs": [
              {
                "identifier": "agentName1-jobName1",
                "agentName": "agentName1",
                "jobName": "jobName1",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName2-jobName2",
                "agentName": "agentName2",
                "jobName": "jobName2",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName3-jobName3",
                "agentName": "agentName3",
                "jobName": "jobName3",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName4-jobName4",
                "agentName": "agentName4",
                "jobName": "jobName4",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName5-jobName5",
                "agentName": "agentName5",
                "jobName": "jobName5",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName6-jobName6",
                "agentName": "agentName6",
                "jobName": "jobName6",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName7-jobName7",
                "agentName": "agentName7",
                "jobName": "jobName7",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName8-jobName8",
                "agentName": "agentName8",
                "jobName": "jobName8",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName9-jobName9",
                "agentName": "agentName9",
                "jobName": "jobName9",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName10-jobName10",
                "agentName": "agentName10",
                "jobName": "jobName10",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName11-jobName11",
                "agentName": "agentName11",
                "jobName": "jobName11",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName12-jobName12",
                "agentName": "agentName12",
                "jobName": "jobName12",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName13-jobName13",
                "agentName": "agentName13",
                "jobName": "jobName13",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName14-jobName14",
                "agentName": "agentName14",
                "jobName": "jobName14",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName15-jobName15",
                "agentName": "agentName15",
                "jobName": "jobName15",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              },
              {
                "identifier": "agentName16-jobName16",
                "agentName": "agentName16",
                "jobName": "jobName16",
                "held": false,
                "disabled": false,
                "scheduledProcessEvent": {
                  "agentName": "agentName1",
                  "jobName": "jobName1",
                  "jobGroup": "group",
                  "jobDescription": "description",
                  "commandLine": "ls -la",
                  "resultOutput": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/out1",
                  "resultError": "/opt/platform/runtime/scheduler-agent-1.0.0-SNAPSHOT/err1",
                  "pid": 7427,
                  "user": null,
                  "fireTime": 1633166700001,
                  "nextFireTime": 1633167000000,
                  "successful": true,
                  "completionTime": 1633166700022,
                  "returnCode": 0,
                  "outcome": "EXECUTION_INVOKED"
                }
              }
            ],
            "jobDependencies": [
              {
                "jobDependency": {
                  "job": "agentName1-jobName1"
                },
                "jobDependency": {
                  "job": "agentName2-jobName2"
                },
                "jobDependency": {
                  "job": "agentName3-jobName3"
                },
                "jobDependency": {
                  "job": "agentName4-jobName4"
                },
                "jobDependency": {
                  "job": "agentName5-jobName5",
                  "logicalGrouping": {
                    "logicalGrouping": {
                      "and": [
                        "agentName1-jobName1",
                        "agentName2-jobName2"
                      ],
                      "or": [
                        "agentName3-jobName3"
                      ]
                    }
                  }
                },
                "jobDependency": {
                  "job": "agentName6-jobName6",
                  "logicalGrouping": {
                    "and": [
                      "agentName5-jobName4",
                      "agentName5-jobName5"
                    ]
                  }
                }
              }
            ],
            "contexts": [
            ],
            "contextDependencies": [
              {
                "contextDependency": {
                  "group": "jobGroup3",
                  "contextDependencies": [
                    {
                      "contextDependency": {
                        "context": "jobGroup1",
                        "contextDependencyName": "jobDependencyGroupName1"
                      },
                      "contextDependency": {
                        "context": "jobGroup2",
                        "contextDependencyName": "jobDependencyGroupName2",
                        "logicalGrouping": {
                          "and": [
                            "jobGroup1"
                          ]
                        }
                      }
                    }
                  ]
                },
                "contextDependency": {
                  "context": "jobGroup4",
                  "contextDependencyName": "jobDependencyGroupName3",
                  "logicalGrouping": {
                    "and": [
                      "jobGroup3"
                    ]
                  }
                }
              }
            ]
          },
          {
            "name": "Context4",
            "startTime": 1633167000000,
            "endTime": 1633177000000,
            "status": "running",
            "contextParameters": [
              {
                "name": "name",
                "type": "type",
                "value": "value"
              },
              {
                "name": "name",
                "type": "type",
                "value": "value"
              },
              {
                "name": "name",
                "type": "type",
                "value": "value"
              }
            ],
            "scheduledJobs": [
              {
                "identifier": "agentName7-jobName7",
                "agentName": "agentName7",
                "jobName": "jobName7",
                "held": false,
                "disabled": false
              },
              {
                "identifier": "agentName8-jobName8",
                "agentName": "agentName8",
                "jobName": "jobName8",
                "held": false,
                "disabled": false
              },
              {
                "identifier": "agentName9-jobName9",
                "agentName": "agentName9",
                "jobName": "jobName9",
                "held": false,
                "disabled": false
              },
              {
                "identifier": "agentName10-jobName10",
                "agentName": "agentName10",
                "jobName": "jobName10",
                "held": false,
                "disabled": false
              },
              {
                "identifier": "agentName11-jobName11",
                "agentName": "agentName11",
                "jobName": "jobName11",
                "held": false,
                "disabled": false
              }
            ],
            "jobDependencies": [
              {
                "jobDependency": {
                  "job": "agentName7-jobName7"
                },
                "jobDependency": {
                  "job": "agentName8-jobName8"
                },
                "jobDependency": {
                  "job": "agentName9-jobName9",
                  "logicalGrouping": {
                    "or": [
                      "agentName8-jobName8",
                      "agentName7-jobName7"
                    ]
                  }
                },
                "jobDependency": {
                  "job": "agentName10-jobName10",
                  "logicalGrouping": {
                    "and": [
                      "agentName7-jobName7"
                    ]
                  }
                },
                "jobDependency": {
                  "job": "agentName11-jobName11",
                  "logicalGrouping": {
                    "and": [
                      "agentName9-jobName9",
                      "agentName10-jobName10"
                    ]
                  }
                }
              }
            ],
            "contexts": [
            ],
            "contextDependencies": [
            ]
          }
        ],
        "contextDependencies": [
          {
            "contextDependency": {
              "context": "Context4",
              "logicalGrouping": {
                "and": [
                  "Context3"
                ]
              }
            }
          }
        ]
      },
      {
        "name": "Context5",
        "startTime": 1633167000000,
        "endTime": 1633177000000,
        "status": "waiting",
        "contextParameters": [
          {
            "name": "name",
            "type": "type",
            "value": "value"
          },
          {
            "name": "name",
            "type": "type",
            "value": "value"
          },
          {
            "name": "name",
            "type": "type",
            "value": "value"
          }
        ],
        "scheduledJobs": [
          {
            "identifier": "agentName12-jobName12",
            "agentName": "agentName12",
            "jobName": "jobName12",
            "held": false,
            "disabled": false
          },
          {
            "identifier": "agentName13-jobName13",
            "agentName": "agentName13",
            "jobName": "jobName13",
            "held": false,
            "disabled": false
          },
          {
            "identifier": "agentName14-jobName14",
            "agentName": "agentName14",
            "jobName": "jobName14",
            "held": false,
            "disabled": false
          },
          {
            "identifier": "agentName15-jobName15",
            "agentName": "agentName15",
            "jobName": "jobName15",
            "held": false,
            "disabled": false
          },
          {
            "identifier": "agentName16-jobName16",
            "agentName": "agentName16",
            "jobName": "jobName16",
            "held": false,
            "disabled": false
          }
        ],
        "jobDependencies": [
          {
            "jobDependency": {
              "job": "agentName12-jobName12"
            },
            "jobDependency": {
              "job": "agentName13-jobName13"
            },
            "jobDependency": {
              "job": "agentName14-jobName14",
              "logicalGrouping": {
                "and": [
                  "agentName12-jobName12",
                  "agentName13-jobName13"
                ]
              }
            },
            "jobDependency": {
              "job": "agentName15-jobName15"
            },
            "jobDependency": {
              "job": "agentName16-jobName16",
              "logicalGrouping": {
                "and": [
                  "agentName14-jobName14",
                  "agentName15-jobName15"
                ]
              }
            }
          }
        ],
        "contexts": [
        ],
        "contextDependencies": [
        ]
      }
    ],
    "contextDependencies": [
      {
        "contextDependency": {
          "context": "Context5",
          "logicalGrouping": {
            "and": [
              "Context2"
            ]
          }
        }
      }
    ]
  }
}  

