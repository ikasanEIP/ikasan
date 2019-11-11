package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.dashboard.broadcast.State;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Flow
{
	private String name;
    private String configurationId;
	private Consumer consumer;
	private State status = State.RUNNING_STATE;

	// flow border values
	private int x = 0,y = 0,w = 0,h = 0;

    /**
     * Constructor
     *
     * @param name
     * @param configurationId
     * @param consumer
     */
	public Flow(String name, String configurationId, Consumer consumer)
	{
		this.name = name;
		this.configurationId = configurationId;
		this.consumer = consumer;
	}

	public String getName()
	{
		return name;
	}

    public String getConfigurationId()
    {
        return configurationId;
    }

    public Consumer getConsumer()
	{
		return consumer;
	}

	public void setBorder(int x, int y, int w, int h)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getW()
    {
        return w;
    }

    public int getH()
    {
        return h;
    }

    public State getStatus()
    {
        return status;
    }

    public void setStatus(State status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        final StringBuffer sb = new StringBuffer("Flow{");
        sb.append("name='").append(name).append('\'');
        sb.append(", configurationId='").append(configurationId).append('\'');
        sb.append(", consumer=").append(consumer);
        sb.append(", status=").append(status);
        sb.append(", x=").append(x);
        sb.append(", y=").append(y);
        sb.append(", w=").append(w);
        sb.append(", h=").append(h);
        sb.append('}');
        return sb.toString();
    }
}
