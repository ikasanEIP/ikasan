package org.ikasan.dashboard.ui.visualisation.model.flow;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Flow
{
	private String name;
	private Consumer consumer;

	public Flow(String name, Consumer consumer)
	{
		this.name = name;
		this.consumer = consumer;
	}

	public String getName()
	{
		return name;
	}

	public Consumer getConsumer()
	{
		return consumer;
	}
}
