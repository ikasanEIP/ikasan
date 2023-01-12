package org.ikasan.spec.scheduled.context.service;

import org.quartz.JobExecutionContext;

public interface ContextInstanceRegistrationService {

    String register(String contextName);
    void deRegisterByName(String contextName);
    void deRegisterById(String contextInstanceId, JobExecutionContext context);
}
