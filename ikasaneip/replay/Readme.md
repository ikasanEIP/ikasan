[../](../../Readme.md)
![IKASAN](../developer/docs/quickstart-images/Ikasan-title-transparent.png)
# Replay Service
<img src="../developer/docs/quickstart-images/replay.gif" width="200px" align="left" style="padding-right: 20px; display: block; border: none;"> 
The Replay Service provides a mechanism for recording and replaying data events. Flows can be configured to record events as they are received by the consumer of the flow, prior to any
mutations within the flow. Replay events are serialised and persisted to the underlying datastore as well as the text index if one is configured. Once data events have been recorded, they
can then be replayed back into the flow from which they were recorded, in either the same environment that they were recorded, or into another environment in which the same module/flow is 
deployed. This service provide 2 valuable features. Firstly in the unlikely event that data has not arrived at its intended destination, the data event can be replayed into the same flow
, in the same environment, within which it was recorded, thus providing a fall back approach to the guaranteed delivery of data. The second feature allows for data recoded from one environment
to be played into another environment. This is particularly useful when troubleshooting problems or providing quality assurance against new developments that require real data for the purpose 
of testing.</br>
