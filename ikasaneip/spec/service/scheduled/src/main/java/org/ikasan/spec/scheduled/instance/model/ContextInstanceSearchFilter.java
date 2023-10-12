package org.ikasan.spec.scheduled.instance.model;

import java.util.List;

public interface ContextInstanceSearchFilter {
    String getContextSearchFilter();

    public void setContextSearchFilter(String contextSearchFilter);

    List<String> getContextInstanceNames();

    public void setContextInstanceNames(List<String> contextInstanceNames);

    public String getContextInstanceId();

    public void setContextInstanceId(String contextInstanceId);

    public long getCreatedTimestamp();

    public void setCreatedTimestamp(long createdTimestamp);

    public long getModifiedTimestamp();

    public void setModifiedTimestamp(long modifiedTimestamp);

    public long getStartTime();

    public void setStartTime(long timestamp);

    public long getStartTimeStart();

    public void setStartTimeStart(long timestamp);

    public long getStartTimeEnd();

    public void setStartTimeEnd(long timestamp);

    public long getEndTime();

    public void setEndTime(long timestamp);

    public long getEndTimeStart();

    public void setEndTimeStart(long timestamp);

    public long getEndTimeEnd();

    public void setEndTimeEnd(long timestamp);

    public String getStatus();

    public void setStatus(String status);
}
