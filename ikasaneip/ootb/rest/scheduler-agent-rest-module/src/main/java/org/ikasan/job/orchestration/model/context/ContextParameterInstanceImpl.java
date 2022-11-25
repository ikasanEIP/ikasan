package org.ikasan.job.orchestration.model.context;

import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public class ContextParameterInstanceImpl extends ContextParameterImpl implements ContextParameterInstance {
    private String value;

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }
}
