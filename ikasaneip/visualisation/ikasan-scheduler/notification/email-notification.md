![IKASAN](../../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Email Notification

The Ikasan Scheduler comes with the ability to send out email notification during the life cycle of the running Job Plan.
This page describes the JSON definition for the email notification.
Provisioning of the notification will not be explain here.

The 5 Monitor Types that the Ikasan Scheduler supports are:

 - ERROR - when a job goes into Error
 - OVERDUE - when a file has not arrived in time based on an SLA cron expression
 - COMPLETE - when a job has completed
 - START - when a job has started
 - RUNNING_TIME - when a job has either run too quickly or too long based on the job definition.
 
There are two ways to set up notification for a Job Plans
1. Setup up an overall notification for your Job Plan / Context
2. Setup up a specific notification for a job that runs on a given child context

### Setting up notification for your Job Plan

The definition of the notification will be stored in solr under the type: emailNotificationContext
This follows the model referenced: [EmailNotificationContext](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/notification/model/EmailNotificationContext.java)

A JSON payload can be created to represent the definition for the notification, and a sample of it can found below

```json
{
  "contextName" : "-1793100514",
  "monitorTypes" : [ "START", "COMPLETE", "ERROR", "OVERDUE", "RUNNING_TIME" ],
  "emailSendTo" : [ "${EMAIL}" ],
  "emailSendToByMonitorType" : {
    "OVERDUE" : [ "${EmailMulti}" ]
  },
  "emailSendCcByMonitorType" : {
    "COMPLETE" : [ "${EmailMulti}" ]
  },
  "emailSendBccByMonitorType" : { },
  "emailSubjectNotificationTemplate" : {
    "COMPLETE" : "${directory}notification-complete-email-subject-template.txt",
    "ERROR" : "${directory}notification-error-email-subject-template.txt",
    "START" : "${directory}notification-start-email-subject-template.txt",
    "RUNNING_TIME" : "${directory}notification-runningtimes-email-subject-template.txt",
    "OVERDUE" : "${directory}notification-overdue-email-subject-template.txt"
  },
  "emailBodyNotificationTemplate" : {
    "COMPLETE" : "${directory}notification-complete-email-body-template.txt",
    "ERROR" : "${directory}notification-error-email-body-template.txt",
    "START" : "${directory}notification-start-email-body-template.txt",
    "RUNNING_TIME" : "${directory}notification-runningtimes-email-body-template.txt",
    "OVERDUE" : "${directory}notification-overdue-email-body-template.txt"
  },
  "html" : false
}
```

Some Key points to take note of

**parametrisation**

You can define placeholder values that will be replaced by a configuration stored within the Ikasan Dashboard properties. There values are notated by ${somevalue} and will be described here: [Using parameters](#using-parameters)

**monitorTypes**

For your Context / Job plan, you must indicator what monitor types you want to send notification for. If you do not state this, notification will not kick off.

**emailSubjectNotificationTemplate - emailBodyNotificationTemplate**

You must specify a template for each monitor type defined in the notification template. Based on the type of notification use, the system will use the corresponding email template to build the subject and body. Without this defined you will not get emails sent out correctly.

**emailSendTo - emailSendCc - emailSendBcc**
 
These are the default list of email addresses the emails will be sent to. This can be overridden which is explained below.
 
**emailSendToByMonitorType - emailSendToByMonitorType - emailSendToByMonitorType** 

You can define different email address depending on the monitor type. In the above example, for OVERDUE monitor event, it will use the ${EmailMulti} instead of ${EMAIL} when sending the email.

### Setting specific notification for a job that runs on a given child context

Email notification can be set up at an individual basis if you only need a notification setup for one job.
These notifications will take precedence if a notification has been setup for the Job Plan.

The definition of the notification will be stored in solr under the type: emailNotificationDetails
This follows the model referenced: [EmailNotificationDetails](../../../spec/service/scheduled/src/main/java/org/ikasan/spec/scheduled/notification/model/EmailNotificationDetails.java)

A JSON payload can be created to represent the definition for the notification, and a sample of it can found below

```json
{
  "jobName" : "-764230802_ScheduledJob_15:25:00",
  "contextName" : "-1793100514",
  "childContextName" : "CONTEXT-1616674532",
  "monitorType" : "COMPLETE",
  "emailSendTo" : [ "${EmailMulti}" ],
  "emailSubjectTemplate" : "notification/notification-error-email-subject-template.txt",
  "emailBodyTemplate" : "notification/notification-error-email-body-template.txt",
  "html" : false
}
```

Some Key points to take note of

**parametrisation**

You can define placeholder values that will be replaced by a configuration stored within the Ikasan Dashboard properties. There values are notated by ${somevalue} and will be described here: [Using parameters](#using-parameters)

 **monitorTypes**
 
 This is important and mandatory. The specific notification will only be served if the job, child context and the monitor type has been defined and setup.
 
 **emailSendTo - emailSendCc - emailSendBcc**
  
 These are the email addresses the emails will be sent to.
 
 **emailSubjectTemplate - emailBodyTemplate**
 
 Requires a path to where the templates are stored.
 
 ### Using parameters
 
 You can define placeholder values in the notification which will be replaced with configured values from a property file. An example of this is for a list of emails.
 To create a placeholder value, you must define it as **${somevalue}**
 
 Then within the application.properties file for the dashboard you need to define a property value that points to a file, which could be managed by Spring Cloud Config:
```
 scheduler.email.notification.configuration.location[Name_Of_Context]=/dir/some_config_file.txt
```

The config file will be based on key values pairs. So if you defined **${somevalue}** in your notification context, and you want to replace it with an email address for example than within the config file you need to do the following:
```
#some_config_file.txt
somevalue=name@abc.com
```

You can define a list of emails. If we take an example provided in the [Setting up notification for your Job Plan](#setting-up-notification-for-your-job-plan) **${EmailMulti}** is defined, by separating by a comma we can define multiple emails addresses that we want to send the notification for.

```
#some_config_file.txt
EmailMulti=name1@abc.com,name2@abc.com,name3@abc.com,name4@abc.com
```
