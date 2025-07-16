package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.RepositoryMetaData;

public class RepositoryMetaDataImpl implements RepositoryMetaData {
    private String id;
    private String url;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
}