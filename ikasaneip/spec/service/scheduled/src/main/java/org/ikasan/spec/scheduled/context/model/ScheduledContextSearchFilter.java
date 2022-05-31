package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ScheduledContextSearchFilter extends Serializable {

    public String getContextName();

    public void setContextName(String contextName);
}