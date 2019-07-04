package org.ikasan.dashboard.ui.visualisation.model.flow;

import java.util.List;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Module
{
	private String name;
	private List<Flow> flows;

	public Module(String name, List<Flow> flows)
	{
		this.name = name;
		this.flows = flows;
	}

	public String getName()
	{
		return name;
	}

	public List<Flow> getFlows()
	{
		return flows;
	}
}
