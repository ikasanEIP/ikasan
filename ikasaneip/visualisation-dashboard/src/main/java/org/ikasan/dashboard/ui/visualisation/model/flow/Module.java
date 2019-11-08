package org.ikasan.dashboard.ui.visualisation.model.flow;

import org.ikasan.spec.metadata.ConfigurationMetaData;
import org.ikasan.spec.metadata.FlowElementMetaData;
import org.ikasan.topology.model.Component;

import java.util.ArrayList;
import java.util.HashMap;
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
	private HashMap<String, ConfigurationMetaData> configurationMap;
    private HashMap<String, FlowElementMetaData> componentMap;

    /**
     * Constructor
     *
     * @param url
     * @param name
     * @param description
     * @param version
     * @param configurationMap
     * @param componentMap
     */
	public Module(String url, String name, String description, String version, HashMap<String
        , ConfigurationMetaData> configurationMap, HashMap<String, FlowElementMetaData> componentMap)
	{
	    this.url = url;
		this.name = name;
		this.description = description;
		this.version = version;
		this.configurationMap = configurationMap;
		this.componentMap = componentMap;
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

    public HashMap<String, ConfigurationMetaData> getConfigurationMap()
    {
        return configurationMap;
    }

    public HashMap<String, FlowElementMetaData> getComponentMap()
    {
        return componentMap;
    }
}
