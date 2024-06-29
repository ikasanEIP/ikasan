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
    void setType(ModuleType moduleType);

    /**
     * Get the module type.
     *
     * @return
     */
    ModuleType getType();

    /**
     * Returns the url of the module.
     *
     * @return
     */
    String getUrl();

    /**
     * Set the url of the module.
     *
     */
    void setUrl(String url);

    /**
     * Set the flow name.
     *
     * @param name the flow name
     */
     void setName(String name);

    /**
     * Get the flow name.
     *
     * @return the flow name.
     */
    String getName();

    /**
     * Set the module description
     *
     * @param description
     */
    void setDescription(String description);

    /**
     * Get the module description.
     *
     * @return
     */
    String getDescription();

    /**
     * Set the module version
     *
     * @param version
     */
    void setVersion(String version);

    /**
     * Get the module's Ikasan version.
     *
     * @return
     */
    String getIkasanVersion();

    /**
     * Set the module's Ikasan version
     *
     * @param ikasanVersion
     */
    void setIkasanVersion(String ikasanVersion);

    /**
     * Get the module version.
     *
     * @return
     */
    String getVersion();

    /**
     * Set the flow meta data list.
     *
     * @param flows
     */
    void setFlows(List<FlowMetaData> flows);

    /**
     * Get the flow meta data list.
     *
     * @return
     */
    List<FlowMetaData> getFlows();

    /**
     * Get the configured resource id for the module.
     *
     * @return
     */
    String getConfiguredResourceId();

    /**
     * Set the configured resource id for the module.
     *
     * @param id
     */
    void setConfiguredResourceId(String id);

    /**
     * Get the host that the module is running on
     *
     * @return
     */
    String getHost();

    /**
     * Set the host that the module is running on.
     *
     * @param host
     */
    void setHost(String host);

    /**
     * Get the port that the module is bound to.
     *
     * @return
     */
    Integer getPort();

    /**
     * Set the port that the module is bound to.
     *
     * @param port
     */
    void setPort(Integer port);

    /**
     * Get the root context of the module.
     *
     * @return
     */
    String getContext();

    /**
     * Set the root context of the module.
     *
     * @param context
     */
    void setContext(String context);

    /**
     * Get the protocol under which the module is running.
     *
     * @return
     */
    String getProtocol();

    /**
     * Set the protocol that the module will run under.
     *
     * @param protocol
     */
    void setProtocol(String protocol);
}
