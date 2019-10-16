package org.ikasan.dashboard.ui.visualisation.model.flow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by stewmi on 08/11/2018.
 */
public class Module
{
    private String url;
	private String name;
	private String description;
	private String version;
	private List<Flow> flows;

    /**
     * Construcior
     *
     * @param url
     * @param name
     * @param description
     * @param version
     */
	public Module(String url, String name, String description, String version)
	{
	    this.url = url;
		this.name = name;
		this.description = description;
		this.version = version;
	}

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getName()
	{
		return name;
	}

    public String getDescription()
    {
        return description;
    }

    public String getVersion()
    {
        return version;
    }

    public List<Flow> getFlows()
	{
		return flows;
	}

	public void addFlow(Flow flow)
    {
        if(flows == null)
        {
            flows = new ArrayList<>();
        }

        flows.add(flow);
    }
}
