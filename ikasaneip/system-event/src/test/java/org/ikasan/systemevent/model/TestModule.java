package org.ikasan.systemevent.model;

import java.util.List;

public class TestModule implements org.ikasan.spec.module.Module {
    private String url;
    private String version;
    private String name;
    private List flows;
    private String description;

    public TestModule(String url, String version, String name, List flows, String description) {
        this.url = url;
        this.version = version;
        this.name = name;
        this.flows = flows;
        this.description = description;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public List getFlows() {
        return this.flows;
    }

    @Override
    public Object getFlow(String name) {
        return null;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }
}
