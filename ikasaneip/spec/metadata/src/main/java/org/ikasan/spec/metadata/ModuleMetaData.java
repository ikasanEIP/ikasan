package org.ikasan.spec.metadata;

import org.ikasan.spec.module.ModuleType;

import java.util.List;

public interface ModuleMetaData
{
    /**
     * Set the module type on the metadata.
     *
     * @param moduleType
     */
    public void setType(ModuleType moduleType);

    /**
     * Get the module type.
     *
     * @return
     */
    public ModuleType getType();

    /**
     * Returns the url of the module.
     *
     * @return
     */
    public String getUrl();

    /**
     * Set the url of the module.
     *
     */
    public void setUrl(String url);

    /**
     * Set the flow name.
     *
     * @param name the flow name
     */
    public  void setName(String name);

    /**
     * Get the flow name.
     *
     * @return the flow name.
     */
    public String getName();

    /**
     * Set the module description
     *
     * @param description
     */
    public void setDescription(String description);

    /**
     * Get the module description.
     *
     * @return
     */
    public String getDescription();

    /**
     * Set the module version
     *
     * @param version
     */
    public void setVersion(String version);

    /**
     * Get the module version.
     *
     * @return
     */
    public String getVersion();

    /**
     * Set the flow meta data list.
     *
     * @param flows
     */
    public void setFlows(List<FlowMetaData> flows);

    /**
     * Get the flow meta data list.
     *
     * @return
     */
    public List<FlowMetaData> getFlows();

    /**
     * Get the configured resource id for the module.
     *
     * @return
     */
    public String getConfiguredResourceId();

    /**
     * Set the configured resource id for the module.
     *
     * @param id
     */
    public void setConfiguredResourceId(String id);

    /**
     * Get the host that the module is running on
     *
     * @return
     */
    public String getHost();

    /**
     * Set the host that the module is running on.
     *
     * @param host
     */
    public void setHost(String host);

    /**
     * Get the port that the module is bound to.
     *
     * @return
     */
    public Integer getPort();

    /**
     * Set the port that the module is bound to.
     *
     * @param port
     */
    public void setPort(Integer port);

    /**
     * Get the root context of the module.
     *
     * @return
     */
    public String getContext();

    /**
     * Set the root context of the module.
     *
     * @param context
     */
    public void setContext(String context);

    /**
     * Get the protocol under which the module is running.
     *
     * @return
     */
    public String getProtocol();

    /**
     * Set the protocol that the module will run under.
     *
     * @param protocol
     */
    public void setProtocol(String protocol);
}
