package org.ikasan.spec.scheduled.dryrun;

import java.util.List;

public interface DryRunModeService<T> {

    void setDryRunMode(boolean dryRunMode);

    boolean getDryRunMode();

    void addDryRunFileList(List<T> dryRunFileList);

    String getDryRunFileName();
}
