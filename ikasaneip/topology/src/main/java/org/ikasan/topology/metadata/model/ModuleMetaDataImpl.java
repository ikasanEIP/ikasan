package org.ikasan.topology.metadata.model;

import org.ikasan.spec.metadata.FlowMetaData;
import org.ikasan.spec.metadata.ModuleMetaData;
import org.ikasan.spec.module.ModuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModuleMetaDataImpl implements ModuleMetaData
{
    private ModuleType moduleType;
    private String url;
    private String host;
    private Integer port;
    private String context;
    private String protocol;
    private String name;
    private String description;
    private String version;
    private String ikasanVersion;
    private List<FlowMetaData> flows;
    private String configuredResourceId;

    @Override
    public void setType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    @Override
    public ModuleType getType() {
        return this.moduleType;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setDescription(String description)
    {
        this.description = description;
    }

    @Override
    public String getDescription()
    {
        return this.description;
    }

    @Override
    public void setVersion(String version)
    {
        this.version = version;
    }

    @Override
    public String getVersion()
    {
        return this.version;
    }

    @Override
    public String getIkasanVersion() {
        return ikasanVersion;
    }

    @Override
    public void setIkasanVersion(String ikasanVersion) {
        this.ikasanVersion = ikasanVersion;
    }

    @Override
    public void setFlows(List<FlowMetaData> flows)
    {
        this.flows = flows;
    }

    @Override
    public List<FlowMetaData> getFlows()
    {
        if(flows == null)
        {
            flows = new ArrayList<>();
        }

        return flows;
    }

    @Override
    public String getUrl()
    {
        return this.url;
    }

    @Override
    public void setUrl(String url)
    {
        this.url = url;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public Integer getPort() {
        return port;
    }

    @Override
    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String getContext() {
        return context;
    }

    @Override
    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    @Override
    public String getConfiguredResourceId() {
        return configuredResourceId;
    }

    @Override
    public void setConfiguredResourceId(String id) {
        this.configuredResourceId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleMetaDataImpl that = (ModuleMetaDataImpl) o;
        return moduleType == that.moduleType
            && Objects.equals(url, that.url)
            && Objects.equals(host, that.host)
            && Objects.equals(port, that.port)
            && Objects.equals(context, that.context)
            && Objects.equals(protocol, that.protocol)
            && Objects.equals(name, that.name)
            && Objects.equals(description, that.description)
            && Objects.equals(version, that.version)
            && Objects.equals(ikasanVersion, that.ikasanVersion)
            && Objects.equals(flows, that.flows)
            && Objects.equals(configuredResourceId, that.configuredResourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleType, url, host, port, context, protocol, name, description
            , version, ikasanVersion, flows, configuredResourceId);
    }
}
