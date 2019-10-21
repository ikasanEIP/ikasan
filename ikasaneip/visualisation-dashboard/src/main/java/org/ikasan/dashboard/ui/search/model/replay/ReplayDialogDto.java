package org.ikasan.dashboard.ui.search.model.replay;

public class ReplayDialogDto
{
    private String user;
    private String replayReason;
    private String targetServer;
    private String authenticationUser;
    private String password;

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public String getReplayReason()
    {
        return replayReason;
    }

    public void setReplayReason(String replayReason)
    {
        this.replayReason = replayReason;
    }

    public String getTargetServer()
    {
        return targetServer;
    }

    public void setTargetServer(String targetServer)
    {
        this.targetServer = targetServer;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getAuthenticationUser()
    {
        return authenticationUser;
    }

    public void setAuthenticationUser(String authenticationUser)
    {
        this.authenticationUser = authenticationUser;
    }
}
