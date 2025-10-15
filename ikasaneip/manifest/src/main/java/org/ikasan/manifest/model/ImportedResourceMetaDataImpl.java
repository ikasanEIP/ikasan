package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ImportedResourceMetaData;

public class ImportedResourceMetaDataImpl implements ImportedResourceMetaData {
    private String source;
    private String resourceType;
    private String resource;

    @Override
    public String getSource() {
        return source;
    }

    @Override
    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String getResourceType() {
        return resourceType;
    }

    @Override
    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Override
    public String getResource() {
        return resource;
    }

    @Override
    public void setResource(String resource) {
        this.resource = resource;
    }
}
