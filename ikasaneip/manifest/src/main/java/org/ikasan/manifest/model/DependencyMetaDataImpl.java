package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.DependencyMetaData;

import java.util.Objects;

public class DependencyMetaDataImpl implements DependencyMetaData {
    private String group;
    private String artefact;
    private String version;

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getArtefact() {
        return artefact;
    }

    @Override
    public void setArtefact(String artefact) {
        this.artefact = artefact;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DependencyMetaDataImpl that = (DependencyMetaDataImpl) o;
        return Objects.equals(group, that.group)
            && Objects.equals(artefact, that.artefact)
            && Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, artefact, version);
    }
}