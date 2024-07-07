package org.ikasan.spec.scheduled.job.model;

public interface JobConstants {
    String FILE_EVENT_DRIVEN_JOB = "fileEventDrivenJob";
    String INTERNAL_EVENT_DRIVEN_JOB = "internalEventDrivenJob";
    String QUARTZ_SCHEDULE_DRIVEN_JOB = "quartzScheduleDrivenJob";
    String GLOBAL_EVENT_JOB = "globalEventJob";

    String FILE_EVENT_DRIVEN_JOB_INSTANCE = "fileEventDrivenJobInstance";
    String INTERNAL_EVENT_DRIVEN_JOB_INSTANCE = "internalEventDrivenJobInstance";
    String QUARTZ_SCHEDULE_DRIVEN_JOB_INSTANCE = "quartzScheduleDrivenJobInstance";
    String GLOBAL_EVENT_JOB_INSTANCE = "globalEventJobInstance";

    String GLOBAL_EVENT = "GLOBAL_EVENT";

    String CONTEXT_START_JOB = "CONTEXT_START_JOB";
    String CONTEXT_TERMINAL_JOB = "CONTEXT_TERMINAL_JOB";
    String LOCAL_EVENT_JOB = "LOCAL_EVENT_JOB";
    String CONTEXT_START_JOB_INSTANCE = "CONTEXT_START_JOB_INSTANCE";
    String CONTEXT_TERMINAL_JOB_INSTANCE = "CONTEXT_TERMINAL_JOB_INSTANCE";
    String LOCAL_EVENT_JOB_INSTANCE = "LOCAL_EVENT_JOB_INSTANCE";

}
