package org.ikasan.spec.scheduled.context.service;

public interface ContextStatusService {

    String getContextStatus(String instanceName, String contextName);

    String getContextStatusForJob(String instanceName, String contextName, String jobIdentifier);

}
