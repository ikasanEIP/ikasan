![Problem Domain](quickstart-images/Ikasan-title-transparent.png)

# Introduction
Ikasan's data requirements can be considered as follows:
* Long lived data that provides features to the platform such as security and module control.
* Long lived data that provides features that enable to platform to describe itself.
* Long lived data that provides configuration parameters for Ikasan components.
* Transient data that is captured on the wire, by each module, as Ikasan modules perform their role as participants in the broader ESB topology.
* Aggregated long lived data data captured on the wire, as Ikasan modules perform their role as participants in the broader ESB topology, indexed and available for fast contextual searching.


| Data Entity            | H2 Module          | H2 Dashboard       | Solr       | Is Transient | Comments |
|------------------------|--------------------|--------------------|------------|--------------|----------|
| AuthenticationMethod   | :white_check_mark: | :white_check_mark: | :no_entry: | :no_entry:   |          |
| Authorities            |                    |                    |            |              |          |
| Configuration          |                    |                    |            |              |          |
| ConfigurationParameter |                    |                    |            |              |          |
| ConfParamBoolean       |                    |                    |            |              |          |
| ConfParamInteger       |                    |                    |            |              |          |
| ConfParamList          |                    |                    |            |              |          |
| ConfParamListString    |                    |                    |            |              |          |
| ConfParamLong          |                    |                    |            |              |          |
| ConfParamMap           |                    |                    |            |              |          |
| ConfParamMapString     |                    |                    |            |              |          |
| ConfParamMaskedString  |                    |                    |            |              |          |
| ConfParamString        |                    |                    |            |              |          |
| PolicyLink             |                    |                    |            |              |          |
| PolicyLinkType         |                    |                    |            |              |          |
| PrincipalRole          |                    |                    |            |              |          |
| RolePolicy             |                    |                    |            |              |          |
| SecurityPolicy         |                    |                    |            |              |          |
| SecurityPrincipal      |                    |                    |            |              |          |
| SecurityRole           |                    |                    |            |              |          |
| SystemEvent            |                    |                    |            |              |          |
| UserPrincipal          |                    |                    |            |              |          |
| Users                  |                    |                    |            |              |          |
| UsersAuthorities       |                    |                    |            |              |          |

## Ikasan Enterprise Service Bus Data

| Data Entity                    |   |   |   |   |
|--------------------------------|---|---|---|---|
| AuthenticationMethod           |   |   |   |   |
| Authorities                    |   |   |   |   |
| BusinessStream                 |   |   |   |   |
| BusinessStreamFlow             |   |   |   |   |
| Component                      |   |   |   |   |
| Configuration                  |   |   |   |   |
| ConfigurationParameter         |   |   |   |   |
| ConfParamBoolean               |   |   |   |   |
| ConfParamInteger               |   |   |   |   |
| ConfParamList                  |   |   |   |   |
| ConfParamListString            |   |   |   |   |
| ConfParamLong                  |   |   |   |   |
| ConfParamMap                   |   |   |   |   |
| ConfParamMapString             |   |   |   |   |
| ConfParamMaskedString          |   |   |   |   |
| ConfParamString                |   |   |   |   |
| ErrorCategorisation            |   |   |   |   |
| ErrorCategorisationLink        |   |   |   |   |
| ErrorOccurrence                |   |   |   |   |
| ErrorOccurrenceLink            |   |   |   |   |
| ErrorOccurrenceNote            |   |   |   |   |
| ExclusionEvent                 |   |   |   |   |
| ExclusionEventAction           |   |   |   |   |
| Filter                         |   |   |   |   |
| FilterComponent                |   |   |   |   |
| Flow                           |   |   |   |   |
| FlowEventTrigger               |   |   |   |   |
| FlowEventTriggerParameters     |   |   |   |   |
| FlowInvocation                 |   |   |   |   |
| FTChecksumCommand              |   |   |   |   |
| FTCleanupChunksCommand         |   |   |   |   |
| FTDeliverBatchCommand          |   |   |   |   |
| FTDeliverFileCommand           |   |   |   |   |
| FTFileChunk                    |   |   |   |   |
| FTFileChunkHeader              |   |   |   |   |
| FTFileFilter                   |   |   |   |   |
| FTRetrieveFileCommand          |   |   |   |   |
| FTTransactionalResourceCommand |   |   |   |   |
| FTXid                          |   |   |   |   |
| IkasanModule                   |   |   |   |   |
| IkasanWiretap                  |   |   |   |   |
| Link                           |   |   |   |   |
| MessageFilter                  |   |   |   |   |
| MessageHistory                 |   |   |   |   |
| Metric                         |   |   |   |   |
| MetricEvent                    |   |   |   |   |
| Module                         |   |   |   |   |
| Note                           |   |   |   |   |
| Notification                   |   |   |   |   |
| PolicyLink                     |   |   |   |   |
| PolicyLinkType                 |   |   |   |   |
| PrincipalRole                  |   |   |   |   |
| ReplayAudit                    |   |   |   |   |
| ReplayAuditEvent               |   |   |   |   |
| ReplayEvent                    |   |   |   |   |
| RoleFilter                     |   |   |   |   |
| RoleJobPlan                    |   |   |   |   |
| RoleModule                     |   |   |   |   |
| RolePolicy                     |   |   |   |   |
| SecurityPolicy                 |   |   |   |   |
| SecurityPrincipal              |   |   |   |   |
| SecurityRole                   |   |   |   |   |
| StartupControl                 |   |   |   |   |
| SystemEvent                    |   |   |   |   |
| UserBusinessStream             |   |   |   |   |
| UserPrincipal                  |   |   |   |   |
| Users                          |   |   |   |   |
| UsersAuthorities               |   |   |   |   |

## Ikasan Enterprise Scheduler Data


| Data Entity                            |   |   |   |   |
|----------------------------------------|---|---|---|---|
| ScheduledContext                       |   |   |   |   |
| ScheduledContextView                   |   |   |   |   |
| ScheduledProcessEvent                  |   |   |   |   |
| ScheduledContextInstance               |   |   |   |   |
| ScheduledJobInstance                   |   |   |   |   |
| ScheduledContextInstanceAudit          |   |   |   |   |
| ScheduledContextInstanceAuditAggregate |   |   |   |   |
| ContextStartJob                        |   |   |   |   |
| ContextTerminalJob                     |   |   |   |   |
| FileEventDrivenJob                     |   |   |   |   |
| GlobalEventDrivenJob                   |   |   |   |   |
| InternalEventDrivenJob                 |   |   |   |   |
| QuartzScheduleDrivenJob                |   |   |   |   |
| SchedulerJob                           |   |   |   |   |
| JobLockCache                           |   |   |   |   |
| JobLockCacheAudit                      |   |   |   |   |
| EmailNotificationContext               |   |   |   |   |
| EmailNotificationDetails               |   |   |   |   |
| EmailNotificationSendAudit             |   |   |   |   |
| ContextProfile                         |   |   |   |   |
|                                        |   |   |   |   |
