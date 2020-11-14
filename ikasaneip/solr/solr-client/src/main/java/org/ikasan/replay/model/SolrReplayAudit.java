package org.ikasan.replay.model;

import org.ikasan.spec.replay.ReplayAudit;

public class SolrReplayAudit implements ReplayAudit
{
    private long id;
    private String user;
    private String replayReason;
    private String targetServer;
    private long timestamp;

    public SolrReplayAudit(long id, String user, String replayReason, String targetServer, long timestamp) {
        this.id = id;
        this.user = user;
        this.replayReason = replayReason;
        this.targetServer = targetServer;
        this.timestamp = timestamp;
    }

    @Override
    public Long getId()
    {
        return this.id;
    }

    @Override
    public void setId(Long id)
    {
        this.id = id;
    }

    @Override
    public String getUser()
    {
        return this.user;
    }

    @Override
    public void setUser(String user)
    {
        this.user = user;
    }

    @Override
    public String getReplayReason()
    {
        return replayReason;
    }

    @Override
    public void setReplayReason(String replayReason)
    {
        this.replayReason = replayReason;
    }

    @Override
    public String getTargetServer()
    {
        return this.targetServer;
    }

    @Override
    public void setTargetServer(String targetServer)
    {
        this.targetServer = targetServer;
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
}
