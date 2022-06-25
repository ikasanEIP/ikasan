package org.ikasan.spec.scheduled.context.service;

import org.ikasan.spec.scheduled.context.model.ContextTemplate;
import org.ikasan.spec.scheduled.job.model.SchedulerJob;

import java.util.List;

public interface ContextUploadInitialisationService {
    void uploadContextAndJobs(ContextTemplate contextTemplate, List<SchedulerJob> contextJobs);
}
