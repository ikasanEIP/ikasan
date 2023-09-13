package org.ikasan.spec.scheduled.status.model;

import java.util.List;

public interface ContextMachineStatusWrapper {

    List<ContextMachineStatus> getContextMachineStatusList();
    void setContextMachineStatusList(List<ContextMachineStatus> contextMachineStatusList);
}
