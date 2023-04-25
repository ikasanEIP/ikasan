Introduction
This page describes how a context template data model is defined. This represents the scaffholding that defines how context instantiations will be created, as well as underpinning the scheduler designs that are created within the Ikasan Dashboard and the target document format that the Cronacle migration will produce.

Scheduler Context
name	String	The name of the scheduler context.	context-name
timeWindowStart	String	A cron expression defining when this context becomes active 	
"timeWindowStart" : "* * 6 ? * * *"

timeWindowEnd	String	A cron expression defining when this context becomes inactive	
"timeWindowEnd" : "* * 15 ? * * *"

contextParameters
The context parameters represents the template that describes any values that are passed to a scheduler context instantiation. 

Field	Type	Description	Example
name	String	The parameter name.	
"contextParameters": [
  {
    "name": "name",
    "type": "type"
  },
  {
    "name": "name",
    "type": "type"
  },
  {
    "name": "name",
    "type": "type"
  }
],
type	String	The parameter type.
scheduledJobs
The scheduledJobs element represents an array of scheduledJob deifinitions. As scheduled job is defined as seeen below.

Field	Type	Description	Example
identifier	String	The unique identifier for the job within the job schedule.	
"scheduledJobs": [
  {
    "identifier": "agentName1-jobName1",
    "agentName": "agentName1",
    "jobName": "jobName1"
  },
  {
    "identifier": "agentName2-jobName2",
    "agentName": "agentName2",
    "jobName": "jobName2"
  },
  {
    "identifier": "agentName3-jobName3",
    "agentName": "agentName3",
    "jobName": "jobName3"
  },
  {
    "identifier": "agentName4-jobName4",
    "agentName": "agentName4",
    "jobName": "jobName4"
  },
  {
    "identifier": "agentName5-jobName5",
    "agentName": "agentName5",
    "jobName": "jobName5"
  },
  {
    "identifier": "agentName6-jobName6",
    "agentName": "agentName6",
    "jobName": "jobName6"
  }
],
agentName	String	The name of the agent that will be managing the scheduled job.
jobName	String	The name of the scheduled job running on the agent.
logicalGrouping
A logicalGrouping allows us to model the types of logical relationships that exists between scheduld jobs who are dependant upon one another.

and	array(Object)	
The and represents an array of jobs that must have all executed sucessfully in order for true to be returned. For example and[job1, job2, job3] represents a construct by which job1, job2 and job3 would be required to have executed sucessfully to return true, thus indicating that any dependent job on the logical statement could progress.

The and array can be of 4 different types in order to support complex logic:

A strings representing a job name.
An and array representing a group of logical objects that must all equate to true in order to return true.
An or array representing a group of logical objects in which one must equate to true in order to return true.
A logicalGrouping allowing for more complex nesting of logical constructs.
or	array(Object)	
The or represents an array of jobs in which one must have executed sucessfully in order for true to be returned. For example or[job1, job2, job3] represents a construct by which either job1, job2 and job3 would be required to have executed sucessfully to return true, thus indicating that any dependent job on the logical statement could progress.

The or array can be of 4 different types in order to support complex logic:

A strings representing a job name.
An and array representing a group of logical objects that must all equate to true in order to return true.
An or array representing a group of logical objects in which one must equate to true in order to return true.
A logicalGrouping allowing for more complex nesting of logical constructs.
logicalGrouping	logicalGrouping	
It is possible for a logicalGrouping to be nested allowing for complex logical constructs to be defined. Some examples are:

(a and b)	
"logicalGrouping": {
  "and": [
    "a",
    "b"
  ]
}
and
A
B
(a or b)	
"logicalGrouping": {
  "or": [
    "a",
    "b"
  ]
}
or
A
B
((a and b) or c)	
"logicalGrouping": {
  "and": [
    "a",
    "b"
  ],
  "or": [
    "c"
  ]
}
or
and
A
B
C
(((a and b) or c) and d)	
"logicalGrouping": {
  "logicalGrouping": {
    "and": [
      "a",
      "b"
    ],
    "or": [
      "c"
    ]
  },
  "and": [
    "d"
  ]
}
and
or
and
A
B
C
D
(a and b) or (c and d)	
"logicalGrouping": {
    "or":[
        {
            "and": [
                "a",
                "b"
            ]
        }
        ,
        {
            "and": [
                "c",
                "d"
            ]
        }
    ]
}
or
and
and
A
B
C
D
(a and b) and (c or d)	
"logicalGrouping": {
    "and":[
        {
            "and": [
                "a",
                "b"
            ]
        }
        ,
        {
            "or": [
                "c",
                "d"
            ]
        }
    ]
}
and
or
and
A
B
C
D
jobDependency
The jobDependency element defines the logical rules required to be fulfiled prior to the job being executed. If there is no logicalGrouping element found, the job has no dependencies and can fire as scheduled.

job	String	The job field contains the job identifier of the job that the dependency is defined for.
logicalGrouping	logicalGrouping	
The logicalGrouping field contains logical structure that must equate to true in order for the dependant job to execute.

jobDependencies
The jobDependencies array provides the opportunity to logically group a number of jobs and their dependencies that may support an individual business function or logically bound set of scheduled jobs, within a given context. 

jobDependencies	array(jobDependency)	
The contains a list of jobDependency objects, each of which define the logical relationships between jobs.

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
        },
      }
    },
    "jobDependency": {
      "job": "agentName6-jobName6",
      "logicalGrouping": {
        "and": [
          "agentName5-jobName5",
          "agentName4-jobName4"
        ]
      }
    },
    "jobDependency": {
      "job": "agentName7-jobName7"
    },
    "jobDependency": {
      "job": "agentName8-jobName8",
      "logicalGrouping": {
        "and": [
          "agentName6-jobName6",
          "agentName7-jobName7"
        ]
      }
    }
  }
]
and
and
or
and
agentName1-jobName1
agentName2-jobName2
agentName3-jobName3
agentName4-jobName4
agentName5-jobName5
agentName6-jobName6
agentName7-jobName7
agentName8-jobName8
{
  "jobDependencies": [
    {
      "jobDependency": {
        "job": "agentName1-jobName1"
      },
      "jobDependency": {
        "job": "agentName2-jobName2",
        "logicalGrouping": {
          "and": [
            "agentName1-jobName1"
          ]
        }
      },
      "jobDependency": {
        "job": "agentName3-jobName3",
        "logicalGrouping": {
          "and":
            "agentName1-jobName1"
          ]
        }
      },
      "jobDependency": {
        "job": "agentName4-jobName4",
        "logicalGrouping": {
          "and": [
            "agentName1-jobName1"
          ]
        }
      },
      "jobDependency": {
        "job": "agentName5-jobName5",
        "logicalGrouping": {
          "and": [
            "agentName1-jobName1"
          ]
        }
      }
    }
  ]
}
agentName1-jobName1
agentName2-jobName2
agentName3-jobName3
agentName4-jobName4
agentName5-jobName5
contexts
The contexts array allows for the nesting of scheduler contexts.

Putting It All Together
The table below provides a series of full job schedules along with associated visual representation of the jobs.

{
  "name": "Context1",
  "description": "Context Template Description",
  "timeWindowStart" : "0 0 1 ? * * *",
  "timeWindowEnd" : "0 0 23 ? * * *",
  "contextParameters": [
    {
      "name": "name",
      "type": "type"
    },
    {
      "name": "name",
      "type": "type"
    },
    {
      "name": "name",
      "type": "type"
    }
  ],
  "scheduledJobs": [
  ],
  "jobDependencies": [
  ],
  "contexts": [
    {
      "name": "Context2",
      "contextParameters": [
        {
          "name": "name",
          "type": "type"
        },
        {
          "name": "name",
          "type": "type"
        },
        {
          "name": "name",
          "type": "type"
        }
      ],
      "scheduledJobs": [
      ],
      "jobDependencies": [
      ],
      "contexts": [
        {
          "name": "Context3",
          "contextParameters": [
            {
              "name": "name",
              "type": "type"
            },
            {
              "name": "name",
              "type": "type"
            },
            {
              "name": "name",
              "type": "type"
            }
          ],
          "scheduledJobs": [
            {
              "identifier": "agentName1-jobName1",
              "agentName": "agentName1",
              "jobName": "jobName1"
            },
            {
              "identifier": "agentName2-jobName2",
              "agentName": "agentName2",
              "jobName": "jobName2"
            },
            {
              "identifier": "agentName3-jobName3",
              "agentName": "agentName3",
              "jobName": "jobName3"
            },
            {
              "identifier": "agentName4-jobName4",
              "agentName": "agentName4",
              "jobName": "jobName4"
            },
            {
              "identifier": "agentName5-jobName5",
              "agentName": "agentName5",
              "jobName": "jobName5"
            },
            {
              "identifier": "agentName6-jobName6",
              "agentName": "agentName6",
              "jobName": "jobName6"
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
          ]
        },
        {
          "name": "Context4",
          "contextParameters": [
            {
              "name": "name",
              "type": "type"
            },
            {
              "name": "name",
              "type": "type"
            },
            {
              "name": "name",
              "type": "type"
            }
          ],
          "scheduledJobs": [
            {
              "identifier": "agentName7-jobName7",
              "agentName": "agentName7",
              "jobName": "jobName7"
            },
            {
              "identifier": "agentName8-jobName8",
              "agentName": "agentName8",
              "jobName": "jobName8"
            },
            {
              "identifier": "agentName9-jobName9",
              "agentName": "agentName9",
              "jobName": "jobName9"
            },
            {
              "identifier": "agentName10-jobName10",
              "agentName": "agentName10",
              "jobName": "jobName10"
            },
            {
              "identifier": "agentName11-jobName11",
              "agentName": "agentName11",
              "jobName": "jobName11"
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
      ]
    },
    {
      "name": "Context5",
      "contextParameters": [
        {
          "name": "name",
          "type": "type"
        },
        {
          "name": "name",
          "type": "type"
        },
        {
          "name": "name",
          "type": "type"
        }
      ],
      "scheduledJobs": [
        {
          "identifier": "agentName12-jobName12",
          "agentName": "agentName12",
          "jobName": "jobName12"
        },
        {
          "identifier": "agentName13-jobName13",
          "agentName": "agentName13",
          "jobName": "jobName13"
        },
        {
          "identifier": "agentName14-jobName14",
          "agentName": "agentName14",
          "jobName": "jobName14"
        },
        {
          "identifier": "agentName15-jobName15",
          "agentName": "agentName15",
          "jobName": "jobName15"
        },
        {
          "identifier": "agentName16-jobName16",
          "agentName": "agentName16",
          "jobName": "jobName16"
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
    }
  ]
}
Context1
Context2
Context5
Context3
and
or
and
agentName1-jobName1
agentName2-jobName2
agentName3-jobName3
agentName4-jobName4
agentName5-jobName5
agentName6-jobName6
and
and
agentName12-jobName12
agentName13-jobName13
agentName15-jobName15
agentName14-jobName14
agentName16-jobName16
Context4
and
or
agentName7-jobName7
agentName8-jobName8
agentName10-jobName10
agentName9-jobName9
agentName11-jobName11
AND



