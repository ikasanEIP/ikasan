[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Hospital Service
<img src="../developer/docs/quickstart-images/hospital.gif" width="200px" align="left" style="margin-right: 10px"> 
The Ikasan Hospital Service provides Ikasan users with the ability to view and understand errors that have occurred on the Ikasan service bus. Depending upon the categorisation of the error, the user is
able to remediate the error by resubmitting messages that have been excluded. Error within Ikasan are broadly categorised into to two types of errors. Firstly, there are technical errors. Technical
errors are considered to be transient, and as such when one occurs, Ikasan will log the error to the error reporting component of the Hospital Service and then will rollback and attempt
process the message again. Ikasan can be configured to retry a fixed number of times or indefinitely. If configured to retry for a fixed
number of times, Ikasan, upon exhausting the number of retries, will stop the processing flow, flag it into an error state, and notify the monitoring service of the error that has occurred.<br/>
<br/>
The second broad categorisation of errors within Ikasan, are those that are considered business errors. Business errors typically occur when an Ikasan module is unable to process a message that it has received, perhaps dues to missing
static data it is trying to retrieve from the mapping service, or due to an XML validation issue. Generally business errors are deemed to be repairable. With this in mind Ikasan excludes messages associated
with business exceptions. These excluded messages can be viewed via the Ikasan Dashboard along with the error that caused the exclusion. Ikasan users are then able to resubmit the excluded messages once the underlying
business exception has been remediated. Alternatively users can choose to ignore excluded message. All details of the user actions are recorded in order to provide an audit trail of actions taken and can be linked back
to problem management systems.
<br/>

The time to live on error occurrences within an Ikasan Module can be set by setting the `ikasan.error.occurrence.ttl.milliseconds` property in the module properties file. The default value is `2592000000` which is 30 days.
