package org.ikasan.spec.scheduled.instance.model;

public interface ContextInstanceSearchFilter {
    String getContextSearchFilter();

    public void setContextSearchFilter(String contextSearchFilter);

    public String getContextInstanceId();

    public void setContextInstanceId(String contextInstanceId);

    public long getCreatedTimestamp();

    public void setCreatedTimestamp(long createdTimestamp);

    public long getModifiedTimestamp();

    public void setModifiedTimestamp(long modifiedTimestamp);

    public String getStatus();

    public void setStatus(String status);
}
