![IKASAN](../../developer/docs/quickstart-images/Ikasan-title-transparent.png)

# Ikasan Visualisation Dashboard Business Stream Notifications

The Ikasan Visualisation Dashboard supports email notifications. Users and support staff are notified of the exclusion of business stream events. Notifications are configurable
 and are fired on a scheduled basis. When a notification job is fired, the notification service queries the Ikasan index to determine if there are any exclusions relating to
 the business stream. If exclusions are found the following [businessStreamModel](./src/main/java/org/ikasan/dashboard/notification/model/BusinessStreamExclusions) is made 
 available to the [Thymeleaf](https://www.thymeleaf.org/) template engine in order to render the notification email content.

| Name      | Description |
| ----------- | ----------- |
| jobName      | Each individual notification must provide a job name to be registered with the scheduler.       |
| emailBodyTemplate   | A [Thymeleaf](https://www.thymeleaf.org/) template used to render the email notification content. This can be either a text or html format.       |
| emailSubjectTemplate   | A [Thymeleaf](https://www.thymeleaf.org/) template used to render the email notification subject. This can be a text format ONLY.        |
| businessStreamName      | The name of the business stream for which we are raising the notification for.       |
| recipientList   | A list of email addresses to whom the emails will be sent.        |
| cronExpression      | The [quartz cron expression](http://www.quartz-scheduler.org/documentation/quartz-2.3.0/tutorials/crontrigger.html) used by the scheduler to fire the notification events.       |
| isHtml   | A boolean flag to indicate if the email body contains html content or text content.        |
| resultSize      | The maximum number of exclusions that can be returned by the exclusion service when searching for exclusions.       |
| isNewExclusionsOnlyNotification   | A boolean flag to indicate if the notification is only applicable for new exclusions or if it notifies for all outstanding exclusions for a business stream.        |

### Example configuration block
```text
dashboard.notification[0].jobName=notification-1
dashboard.notification[0].emailBodyTemplate=<path-to-template>/notification-email-jp.html
dashboard.notification[0].emailSubject=<path-to-template>/notification-email-subject-jp.html
dashboard.notification[0].businessStreamName=Bond Business Stream 
dashboard.notification[0].recipientList=ikasan@there.com, ikasan2@there.com
dashboard.notification[0].cronExpression=0/5 * * * * ?
dashboard.notification[0].isHtml=true
dashboard.notification[0].resultSize=100
dashboard.notification[0].isNewExclusionsOnlyNotification=true
```

### Example html notification template.
```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <style>
            div.container {
                display: inline-block;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <img src="http://localhost:9090/frontend/images/hospital-service.png" height="200px"/>
        </div>
        <div class="container">
            <img src="http://localhost:9090/frontend/images/ikasan-titling-transparent.png" height="150px"/>
        </div>
        <p>
            Business Stream: <span th:utext="${businessStreamModel.businessStreamMetaData.name}"></span>
        </p>
        
        <p>The following exclusions have been recorded:</p>
        <ul th:remove="all-but-first">
            <li th:each="businessStreamExclusion : ${businessStreamModel.businessStreamExclusions}"
                th:text="${businessStreamExclusion.errorOccurrence.uri}">Reading
            </li>
        </ul>
        <p>
            Please log into the Ikasan dashboard to remediate these events.
        </p>
        <p>
            Regards, <br/>
            <em>The Ikasan Team</em>
        </p>
    </body>
</html>
```
### Example text notification template.
```text
Business Stream: [( ${businessStreamModel.businessStreamMetaData.name} )]


The following exclusions have been recorded:
[# th:each="businessStreamExclusion : ${businessStreamModel.businessStreamExclusions}"]
 - [( ${businessStreamExclusion.errorOccurrence.uri} )]
[/]

Please log into the Ikasan dashboard to remediate these events.

Regards,
    The Ikasan Team
```
### Example email subject template.
```text
Attention. Events have been excluded for the following business stream: [( ${businessStreamModel.businessStreamMetaData.name} )]
```

See [Thymeleaf](https://www.thymeleaf.org/) documentation for more details on how to create to create notification templates.


