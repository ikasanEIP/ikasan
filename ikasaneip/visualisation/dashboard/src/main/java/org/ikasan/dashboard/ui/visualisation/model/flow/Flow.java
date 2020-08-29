package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.dashboard.broadcast.State;
import org.ikasan.dashboard.ui.visualisation.component.FlowOptionsDialog;
import org.ikasan.spec.module.StartupType;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Flow
{
	private String name;
    private String configurationId;
	private Consumer consumer;
	private State status = State.RUNNING_STATE;
	private StartupType startupType;
    private String startupComment;

	// flow border values
	private int x = 0,y = 0,w = 0,h = 0;

	private int controlRelativeX = -137;
	private int controlRelativeY = 0;
	private int controlImageW = 75;
	private int controlImageH = 75;

    /**
     * Constructor
     *
     * @param name
     * @param configurationId
     * @param consumer
     */
	public Flow(String name, String configurationId, Consumer consumer, StartupType startupType, String startupComment)
	{
		this.name = name;
		this.configurationId = configurationId;
		this.consumer = consumer;
		this.startupType = startupType;
		this.startupComment = startupComment;
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

    public int getControlX() {
        return this.getX() + controlRelativeX;
    }

    public int getControlY() {
        return this.getY() + controlRelativeY;
    }

    public int getControlRelativeX() {
        return controlRelativeX;
    }

    public void setControlRelativeX(int controlRelativeX) {
        this.controlRelativeX = controlRelativeX;
    }

    public int getControlRelativeY() {
        return controlRelativeY;
    }

    public void setControlRelativeY(int controlRelativeY) {
        this.controlRelativeY = controlRelativeY;
    }

    public int getControlImageW() {
        return controlImageW;
    }

    public void setControlImageW(int controlImageW) {
        this.controlImageW = controlImageW;
    }

    public int getControlImageH() {
        return controlImageH;
    }

    public void setControlImageH(int controlImageH) {
        this.controlImageH = controlImageH;
    }

    public State getStatus()
    {
        return status;
    }

    public void setStatus(State status)
    {
        this.status = status;
    }

    public StartupType getStartupType() {
        return startupType;
    }

    public void setStartupType(StartupType startupType) {
        this.startupType = startupType;
    }

    public String getStartupComment() {
        return startupComment;
    }

    public void setStartupComment(String startupComment) {
        this.startupComment = startupComment;
    }

    public boolean flowClickedOn(Double x, Double y){
        return (((x > this.getX() && x < (this.getX() + this.getW())) && (y > this.getY() && y < (this.getY() + this.getH())))
            && !controlIconClickedOn(x, y));
    }

    public boolean controlIconClickedOn(Double x, Double y){
        return ((x > this.getControlX() && x < (this.getControlX() + this.getControlImageW())) && (y > this.getControlY() && y < (this.getControlY() + this.getControlImageH())));
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
