package org.ikasan.spec.scheduled.event.model;

import java.util.List;

public interface DryRunFileListParameter<T> {

    List<T> getFileList();

    void setFileList(List<T> fileList);
}
