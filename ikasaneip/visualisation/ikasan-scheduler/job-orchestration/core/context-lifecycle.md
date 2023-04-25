Introduction
This page outlines the life cycle of a context in the scheduler dashboard and scheduler agent.

Core Concepts
There are a number of core concepts relating to the Ikasan Scheduler.

Context
Scheduler Context Data Model outlines how complex job orchestrations are organised.

For futher details on the context data model see the Ikasan implementation - Ikasan context implemtation.

Context Instance
Scheduler Context Instantiation Data Model outlines what a context looks like when it is instantiated.

For futher details on the context instance data model see the Ikasan implementation - Ikasan context instance implemtation.

Scheduler Jobs
Scheduled Job Data Model outlines the different jobs that are supported by the Ikasan Scheduler.

For futher details on the scheduler job data model see the Ikasan implementation - Ikasan scheduler job implementation.

Context Machine
The ContextMachine is at the core of the Ikasan Scheduler. There is an individual context machine created for each context instance. The context machine is responsible for managing a context instance. It does this by listening for scheduler events and applying logic against the context instance to determine if there are any jobs that can be initiated as a result of the event received. If there are, the context machine notifies any listeners of those job initiations.

As each event is received and a state change occurs against the underlying context instance, the context instance is updated and persisted, as well as there being an audit record created for each transition in order to provide complete transparency and auditability of the behaviour of the Ikasan Scheduler. 

process event
Scheduled Event Received
update context instance
create context instance audit record
persist context instance and audit records
are the any jobs to initiate
no
yes
notify listeners

Problem Statement
Ikasan employs a distributed micro service architecture, with which comes many benefits such as a highly decoupled system, robustness, resliancy, scalability and reliability.

However, a distributed system is also burdended with more complexity due to:

nodes in the architecture may need to send state to one another
nodes may need to adhere to certain startup and shut down protocols in order make sure that data integrity is maintaned.
More specifically, the Ikasan Scheduler has some challenges relating to context instances and the way that details of a context instance, such as parameters are shared between the dashboard and it's agents. This is even more exaggerated by the fact two of the Ikasan Scheduler jobs types, Quartz Driven Jobs and File Received Driven Jobs are completely decoupled from the lifecycle of the underlying context within which they are being executed. 

Solution
The following table presents a number of use cases that must be fulfilled in order to provide a robust context lifecycle, along with solutions for each use case. 

The solution is predicated on the following:

File and time based events cannot fire until the agent has been provided with an active context instance.

Use Case	Comments	Solution
Dashboard restart occurs prior to context start time window or after end time window

(warning) If a scheduler agent cannot be provided with the context parameters due to not being available, we can simply ignore this as it will be the responsibility of the agent to request the parameters when it is started.

Context registered with dashboard scheduler to fire at the context start time window cron. 
Dashboard Restart
Cron Job Fires
1. Context instance created
2. Context paramter factory creates all context parameters.
3. All scheduler agents notified of new context instance and associated parameters.
4. Context instance added to the context instance cache.

Dashboard restart occurs after context start time window, but prior to context end time window	
In this case a context instance will already have been created and will exist in solr in either a WAITING or RUNNING state. 

Need to add another state to a context instance that denotes redundent or dead instances. (CANCELLED)

(warning) There is a possibility that a context that should be active due to its time window, is in fact not found in the persistence store. (This case needs to be considered particuarly if the dashboard was down when a context creation cron expression should have fired.)

(warning) Relying on the state of a context could be problematic. This approach requires that all contexts have a terminal node defined within them and once this terminal node (job) has been completed, then the context itself is deemed to be complete. In my opinion this is the biggest hole in this solution as we are relying on contexts always acheiving a terminal state, whereas I think in the real world this might not be the case. Also if we are relying on the context end time window flagging a context as complete, we run the risk of not allowing contexts that have massively overrun to complete. For example we have seen cases that the Muurex batch tips well into the following business day.

(warning) If a scheduler agent cannot be provided with the context parameters due to not being available, we can simply ignore this as it will be the responsibility of the agent to request the parameters when it is started.

 Load all context instances that are in a WAITING or RUNNING state. 
Determine all context instances that should currently be active based on their time window
Are all contexts represented
by the set of RUNNING and
 WAITING contexts
no
yes
Add all context instances to the context intance cache 
For each missing context, offload to seperate thread (possibly via cron that fires immediately or big queue)
1. Context iinstance created
2. Context paramater factory creates all context paramaters.
3. All scheduler agents notified of new context instance and associated context parameters.
4. Context instance added to the context instance cache.
Determine the set of contexts that are not in the RUNNING or WAITING set
Dashboard Restart

Agent restart occurs - happy path	
(warning) All jobs will need to be put on hold until all context information has been sucessfully requested from the dashboard.

(warning) Jobs can only ever execute when the context that they belong to is active and available to the agent. I propose to provide a component to relevant flows to determine if a context is active for the job. If not the messge will simply be filtered. The challenge is how to deal with time based events, as they will have fired based on their schedule. Should we leverage the Ikasan retry mechanism? 



(question) What is the mechanism for providing the contextualised paramters to the file jobs? The file locations are currently managed as an ikasan configuration however it does not seem sensible to me to reconfigure the jobs everytime a context instance is created and the file path contextualised. One approach that has been discussed it to do a string replacement in the message provider to make the contextualised file path available to the file matcher.



Agent requests all context information from the dashboard
Context information received sucessfully
no
yes
Agent Restart





Agent restart occurs - dashboard not available
Context Instance Reset
Considerations:

If a context is reset how do we want to manage events firing again. For example if file event jobs have already fired, what do we do to allow that event to fire again? Do we remove records from the message filter table so that the file can be picked up again? Or do we want to allow file events to be manually created?
Introduction
Core Concepts
Context
Context Instance
Scheduler Jobs
Context Machine
Problem Statement
Solution
Context Instance Reset
Introduction
Core Concepts
Context
Context Instance
Scheduler Jobs
Context Machine
Problem Statement
Solution
Context Instance Reset
Introduction
This page outlines the life cycle of a context in the scheduler dashboard and scheduler agent.

Core Concepts
There are a number of core concepts relating to the Ikasan Scheduler.

Context
Scheduler Context Data Model outlines how complex job orchestrations are organised.

For futher details on the context data model see the Ikasan implementation - Ikasan context implemtation.

Context Instance
Scheduler Context Instantiation Data Model outlines what a context looks like when it is instantiated.

For futher details on the context instance data model see the Ikasan implementation - Ikasan context instance implemtation.

Scheduler Jobs
Scheduled Job Data Model outlines the different jobs that are supported by the Ikasan Scheduler.

For futher details on the scheduler job data model see the Ikasan implementation - Ikasan scheduler job implementation.

Context Machine
The ContextMachine is at the core of the Ikasan Scheduler. There is an individual context machine created for each context instance. The context machine is responsible for managing a context instance. It does this by listening for scheduler events and applying logic against the context instance to determine if there are any jobs that can be initiated as a result of the event received. If there are, the context machine notifies any listeners of those job initiations.

As each event is received and a state change occurs against the underlying context instance, the context instance is updated and persisted, as well as there being an audit record created for each transition in order to provide complete transparency and auditability of the behaviour of the Ikasan Scheduler. 

process event
Scheduled Event Received
update context instance
create context instance audit record
persist context instance and audit records
are the any jobs to initiate
no
yes
notify listeners

Problem Statement
Ikasan employs a distributed micro service architecture, with which comes many benefits such as a highly decoupled system, robustness, resliancy, scalability and reliability.

However, a distributed system is also burdended with more complexity due to:

nodes in the architecture may need to send state to one another
nodes may need to adhere to certain startup and shut down protocols in order make sure that data integrity is maintaned.
More specifically, the Ikasan Scheduler has some challenges relating to context instances and the way that details of a context instance, such as parameters are shared between the dashboard and it's agents. This is even more exaggerated by the fact two of the Ikasan Scheduler jobs types, Quartz Driven Jobs and File Received Driven Jobs are completely decoupled from the lifecycle of the underlying context within which they are being executed. 

Solution
The following table presents a number of use cases that must be fulfilled in order to provide a robust context lifecycle, along with solutions for each use case. 

The solution is predicated on the following:

File and time based events cannot fire until the agent has been provided with an active context instance.

Use Case	Comments	Solution

Use Case	Comments	Solution
Dashboard restart occurs prior to context start time window or after end time window

(warning) If a scheduler agent cannot be provided with the context parameters due to not being available, we can simply ignore this as it will be the responsibility of the agent to request the parameters when it is started.

Context registered with dashboard scheduler to fire at the context start time window cron. 
Dashboard Restart
Cron Job Fires
1. Context instance created
2. Context paramter factory creates all context parameters.
3. All scheduler agents notified of new context instance and associated parameters.
4. Context instance added to the context instance cache.

Dashboard restart occurs after context start time window, but prior to context end time window	
In this case a context instance will already have been created and will exist in solr in either a WAITING or RUNNING state. 

Need to add another state to a context instance that denotes redundent or dead instances. (CANCELLED)

(warning) There is a possibility that a context that should be active due to its time window, is in fact not found in the persistence store. (This case needs to be considered particuarly if the dashboard was down when a context creation cron expression should have fired.)

(warning) Relying on the state of a context could be problematic. This approach requires that all contexts have a terminal node defined within them and once this terminal node (job) has been completed, then the context itself is deemed to be complete. In my opinion this is the biggest hole in this solution as we are relying on contexts always acheiving a terminal state, whereas I think in the real world this might not be the case. Also if we are relying on the context end time window flagging a context as complete, we run the risk of not allowing contexts that have massively overrun to complete. For example we have seen cases that the Muurex batch tips well into the following business day.

(warning) If a scheduler agent cannot be provided with the context parameters due to not being available, we can simply ignore this as it will be the responsibility of the agent to request the parameters when it is started.

 Load all context instances that are in a WAITING or RUNNING state. 
Determine all context instances that should currently be active based on their time window
Are all contexts represented
by the set of RUNNING and
 WAITING contexts
no
yes
Add all context instances to the context intance cache 
For each missing context, offload to seperate thread (possibly via cron that fires immediately or big queue)
1. Context iinstance created
2. Context paramater factory creates all context paramaters.
3. All scheduler agents notified of new context instance and associated context parameters.
4. Context instance added to the context instance cache.
Determine the set of contexts that are not in the RUNNING or WAITING set
Dashboard Restart

Agent restart occurs - happy path	
(warning) All jobs will need to be put on hold until all context information has been sucessfully requested from the dashboard.

(warning) Jobs can only ever execute when the context that they belong to is active and available to the agent. I propose to provide a component to relevant flows to determine if a context is active for the job. If not the messge will simply be filtered. The challenge is how to deal with time based events, as they will have fired based on their schedule. Should we leverage the Ikasan retry mechanism? 



(question) What is the mechanism for providing the contextualised paramters to the file jobs? The file locations are currently managed as an ikasan configuration however it does not seem sensible to me to reconfigure the jobs everytime a context instance is created and the file path contextualised. One approach that has been discussed it to do a string replacement in the message provider to make the contextualised file path available to the file matcher.



Agent requests all context information from the dashboard
Context information received sucessfully
no
yes
Agent Restart





Agent restart occurs - dashboard not available
Context Instance Reset
Considerations:

If a context is reset how do we want to manage events firing again. For example if file event jobs have already fired, what do we do to allow that event to fire again? Do we remove records from the message filter table so that the file can be picked up again? Or do we want to allow file events to be manually created?
