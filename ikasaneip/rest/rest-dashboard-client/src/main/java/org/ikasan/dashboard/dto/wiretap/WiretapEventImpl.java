package org.ikasan.dashboard.dto.wiretap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ikasan.spec.wiretap.WiretapEvent;

@JsonIgnoreProperties
public class WiretapEventImpl implements WiretapEvent<String>
{
    private String identifier;

    private String event;

    private String moduleName;

    private String flowName;

    private String componentName;

    private long timestamp;

    private long expiry;

    private String eventId;

    private String relatedEventId;



    @Override
    public long getIdentifier()
    {
        if (identifier==null)return -1;
        try {
            Long.parseLong(this.identifier);
            return Long.parseLong(identifier);
        }
        catch (NumberFormatException e) {
            return Long.parseLong(identifier.substring(identifier.lastIndexOf("-")+1));
        }
    }

    @Override
    public String getModuleName()
    {
        return this.moduleName;
    }

    @Override
    public String getFlowName()
    {
        return this.flowName;
    }

    @Override
    public String getComponentName()
    {
        return this.componentName;
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public String getEvent()
    {
        return event;
    }

    @Override
    public long getExpiry()
    {
        return this.expiry;
    }

    @Override
    public String getEventId()
    {
        return this.eventId;
    }

    public void setId(String id)
    {
        this.identifier = id;
    }

    public void setEvent(String event)
    {
        this.event = event;
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

    public void setExpiry(long expiry)
    {
        this.expiry = expiry;
    }

    public void setEventId(String eventId)
    {
        this.eventId = eventId;
    }

    public String getRelatedEventId()
    {
        return relatedEventId;
    }

    public void setRelatedEventId(String relatedEventId)
    {
        this.relatedEventId = relatedEventId;
    }

    @Override
    public String toString()
    {
        return "WiretapEventImpl{" +
            "id='" + identifier + '\'' +
            ", event='" + event + '\'' +
            ", moduleName='" + moduleName + '\'' +
            ", flowName='" + flowName + '\'' +
            ", componentName='" + componentName + '\'' +
            ", timestamp=" + timestamp +
            '}';
    }
}
