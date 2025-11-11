package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.RepositoryMetaData;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RepositoryMetaDataImpl that = (RepositoryMetaDataImpl) o;
        return Objects.equals(id, that.id)
            && Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url);
    }
}