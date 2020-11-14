package org.ikasan.hospital.model;

import org.ikasan.spec.hospital.model.ExclusionEventAction;

public class SolrExclusionEventActionImpl implements ExclusionEventAction<String>
{
    public static final String RESUBMIT = "re-submitted";
    public static final String IGNORED = "ignored";

    private String moduleName;
    private String flowName;
    private String errorUri;
    private String actionedBy;
    private String action;
    private String event;
    private long timestamp;
    private String comment;

    /**
     * Constructor
     *
     * @param moduleName
     * @param flowName
     * @param errorUri
     * @param actionedBy
     * @param action
     * @param event
     * @param timestamp
     * @param comment
     */
    public SolrExclusionEventActionImpl(String moduleName, String flowName, String errorUri, String actionedBy, String action
        , String event, long timestamp, String comment) {
        this.moduleName = moduleName;
        this.flowName = flowName;
        this.errorUri = errorUri;
        this.actionedBy = actionedBy;
        this.action = action;
        this.event = event;
        this.timestamp = timestamp;
        this.comment = comment;
    }

    @Override
    public String getModuleName()
    {
        return moduleName;
    }

    @Override
    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    @Override
    public String getFlowName()
    {
        return flowName;
    }

    @Override
    public void setFlowName(String flowName)
    {
        this.flowName = flowName;
    }

    @Override
    public String getErrorUri()
    {
        return errorUri;
    }

    @Override
    public void setErrorUri(String errorUri)
    {
        this.errorUri = errorUri;
    }

    @Override
    public String getActionedBy()
    {
        return actionedBy;
    }

    @Override
    public void setActionedBy(String actionedBy)
    {
        this.actionedBy = actionedBy;
    }

    @Override
    public String getAction()
    {
        return action;
    }

    @Override
    public void setAction(String action)
    {
        this.action = action;
    }

    @Override
    public String getEvent()
    {
        return event;
    }

    @Override
    public void setEvent(String event)
    {
        this.event = event;
    }

    @Override
    public long getTimestamp()
    {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    @Override
    public String getComment()
    {
        return comment;
    }

    @Override
    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("ExclusionEventActionImpl{");
        sb.append("moduleName='").append(moduleName).append('\'');
        sb.append(", flowName='").append(flowName).append('\'');
        sb.append(", errorUri='").append(errorUri).append('\'');
        sb.append(", actionedBy='").append(actionedBy).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", event=").append(event).append('\'');
        sb.append(", timestamp=").append(timestamp);
        sb.append(", comment='").append(comment).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
