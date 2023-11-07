[../](../../Readme.md)
![Problem Domain](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
 # Wiretap Service
<img src="../developer/docs/quickstart-images/wiretap.gif" width="200px" align="left" style="padding-right: 20px; display: block; border: none;"> 
The Wiretap Service allows for data to be collected and inspected, as it flows through the Ikasan Enterprise Service Bus. This Wiretap Service is an invaluable tool allowing for end to
end tracking of data events, in real time, as data flows and mutates. Wiretap events are captured with a time stamp with millisecond precision along with the location of where the wiretap was triggered.
They are also captured with a life identifier that remains immutable for the data event for its entire journey throughout the bus, even if the underlying data mutates. The correlating elements of the wiretap
build a full chronological picture of the flow of data which can then be queried via the Ikasan Dashboard.</br> 
Wiretap jobs are configured on a component at runtime and record all data events that are received by the component. The Wiretap events can
be written to the underlying persistent data store or alternatively written to the log file. Wiretap events are also written to a text index in order to facilitate a fast and efficient context based search facility.
This service provides a high level of visibility on all data events, and coupled with the transactional, guaranteed data delivery features of Ikasan, provides support users assurance that data has been delivered
to all of the intended endpoints.</br>
The Wiretap Service works out of the box and requires no coding. See the Dashboard documentation for details on how to set up wiretaps on a component.
