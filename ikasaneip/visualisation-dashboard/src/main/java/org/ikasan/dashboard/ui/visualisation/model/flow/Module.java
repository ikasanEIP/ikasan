package org.ikasan.dashboard.ui.visualisation.model.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Module
{
	private String name;
	private List<Flow> flows;

	public Module(String name)
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

	public  void addFlow(Flow flow)
    {
        if(flows == null)
        {
            flows = new ArrayList<>();
        }

        flows.add(flow);
    }
}
