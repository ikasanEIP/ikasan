package org.ikasan.ootb.scheduler.agent.rest.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.ikasan.spec.scheduled.event.model.DryRunParameter;

public class DryRunParameterDto implements DryRunParameter {

    private boolean dryRunMode;

    @Override
    public void setDryRunMode(boolean dryRunMode) {
        this.dryRunMode = dryRunMode;
    }

    @Override
    public boolean getDryRunMode() {
        return dryRunMode;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
