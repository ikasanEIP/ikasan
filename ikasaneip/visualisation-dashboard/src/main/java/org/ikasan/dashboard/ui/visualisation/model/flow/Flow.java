package org.ikasan.dashboard.ui.visualisation.model.flow;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Flow
{
	private String name;
    private String configurationId;
	private Consumer consumer;

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
}
