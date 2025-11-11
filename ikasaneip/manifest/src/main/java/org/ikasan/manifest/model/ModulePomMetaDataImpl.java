package org.ikasan.manifest.model;

import org.ikasan.spec.metadata.ModulePomMetaData;

public class ModulePomMetaDataImpl implements ModulePomMetaData {
    private String pomArtefactId;
    private String pomGroupId;
    private String version;

    @Override
    public String getPomArtefactId() {
        return pomArtefactId;
    }

    @Override
    public void setPomArtefactId(String pomArtefactId) {
        this.pomArtefactId = pomArtefactId;
    }

    @Override
    public String getPomGroupId() {
        return pomGroupId;
    }

    @Override
    public void setPomGroupId(String pomGroupId) {
        this.pomGroupId = pomGroupId;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }
}
