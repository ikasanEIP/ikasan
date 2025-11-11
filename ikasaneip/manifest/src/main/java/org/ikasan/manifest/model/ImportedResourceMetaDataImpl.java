package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ImportedResourceMetaData;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImportedResourceMetaDataImpl that = (ImportedResourceMetaDataImpl) o;
        return Objects.equals(resourceType, that.resourceType)
            && Objects.equals(resource, that.resource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, resourceType, resource);
    }
}
