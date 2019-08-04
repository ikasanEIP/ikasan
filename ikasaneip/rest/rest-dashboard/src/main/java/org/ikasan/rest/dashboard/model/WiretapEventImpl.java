package org.ikasan.rest.dashboard.model;

import org.ikasan.spec.wiretap.WiretapEvent;

public class WiretapEventImpl implements WiretapEvent<String>
{
    private long identifier;
    private String moduleName;
    private String flowName;
    private String componentName;
    private long timestamp;
    private String event;
    private Long expiry;
    private String eventId;

    @Override
    public long getIdentifier()
    {
        return 0;
    }

    @Override
    public String getModuleName()
    {
        return null;
    }

    @Override
    public String getFlowName()
    {
        return null;
    }

    @Override
    public String getComponentName()
    {
        return null;
    }

    @Override
    public long getTimestamp()
    {
        return 0;
    }

    @Override
    public String getEvent()
    {
        return null;
    }

    @Override
    public long getExpiry()
    {
        return 0;
    }

    @Override
    public String getEventId()
    {
        return null;
    }

    public void setIdentifier(long identifier)
    {
        this.identifier = identifier;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    public void setComponentName(String componentName)
    {
        this.componentName = componentName;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    public void setEvent(String event)
    {
        this.event = event;
    }

    public void setExpiry(Long expiry)
    {
        this.expiry = expiry;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }
}
