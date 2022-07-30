package org.ikasan.spec.scheduled.context.model;

import java.io.Serializable;

public interface ScheduledContextViewRecord extends Serializable {

    String getId();

    String getParentContextName();

    void setParentContextName(String parentContextName);

    String getContextName();

    void setContextName(String contextName);

    String getContextView();

    void setContextView(String context);

    long getTimestamp();

    void setTimestamp(long timestamp);

    long getModifiedTimestamp();

    void setModifiedTimestamp(long timestamp);

    String getModifiedBy();

    void setModifiedBy(String modifiedBy);
}
