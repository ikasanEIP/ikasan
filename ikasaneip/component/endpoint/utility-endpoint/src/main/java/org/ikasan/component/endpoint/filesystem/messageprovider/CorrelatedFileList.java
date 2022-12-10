package org.ikasan.component.endpoint.filesystem.messageprovider;

import java.io.File;
import java.util.List;

public class CorrelatedFileList {
    private List<File> fileList;
    private String correlatingIdentifier;

    public CorrelatedFileList(List<File> fileList, String correlatingIdentifier) {
        this.fileList = fileList;
        this.correlatingIdentifier = correlatingIdentifier;
    }

    public List<File> getFileList() {
        return fileList;
    }

    public String getCorrelatingIdentifier() {
        return correlatingIdentifier;
    }
}
