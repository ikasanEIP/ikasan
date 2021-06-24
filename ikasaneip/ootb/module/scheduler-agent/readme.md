![Problem Domain](./quickstart-images/Ikasan-title-transparent.png)

# Scheduler Agent Design
                                                                                 
                                                                            /--> ProcessExecutionBroker --> ScheduledProcessEventProdicer 
Scheduled Consumer --> JobExecution Converter --> Blackout Period Router -- 
                                                                            \--> ScheduledProcessEventFilter --> ScheduledProcessEventProdicer 

