package org.ikasan.dashboard.ui.search.model.replay;

import org.ikasan.spec.replay.ReplayAudit;
import org.ikasan.spec.replay.ReplayAuditEvent;

public class ReplayAuditEventImpl implements ReplayAuditEvent<String>
{
    private String id;
    private ReplayAudit replayAudit;
    private boolean success;
    private String resultMessage;
    private long timestamp;

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public void setId(String id)
    {
        this.id = id;
    }

    @Override
    public ReplayAudit getReplayAudit()
    {
        return replayAudit;
    }

    @Override
    public void setReplayAudit(ReplayAudit replayAudit)
    {
        this.replayAudit = replayAudit;
    }

    @Override
    public boolean isSuccess()
    {
        return success;
    }

    @Override
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    @Override
    public String getResultMessage()
    {
        return this.resultMessage;
    }

    @Override
    public void setResultMessage(String resultMessage)
    {
        this.resultMessage = resultMessage;
    }

    @Override
    public long getTimestamp()
    {
        return this.timestamp;
    }

    @Override
    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }
}
