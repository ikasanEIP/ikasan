package org.ikasan.spec.scheduled.dryrun;

import java.util.List;

public interface DryRunFileListParameter<T> {

    List<T> getFileList();

    void setFileList(List<T> fileList);
}
