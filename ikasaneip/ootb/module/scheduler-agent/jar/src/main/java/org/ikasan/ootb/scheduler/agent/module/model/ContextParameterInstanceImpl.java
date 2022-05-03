package org.ikasan.ootb.scheduler.agent.module.model;

import org.ikasan.job.orchestration.model.context.ContextParameterImpl;
import org.ikasan.spec.scheduled.instance.model.ContextParameterInstance;

public class ContextParameterInstanceImpl extends ContextParameterImpl implements ContextParameterInstance {
    private Object value;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
