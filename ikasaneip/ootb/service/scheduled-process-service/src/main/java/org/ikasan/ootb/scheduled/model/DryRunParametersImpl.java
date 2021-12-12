package org.ikasan.ootb.scheduled.model;

import org.ikasan.spec.scheduled.event.model.DryRunParameters;

public class DryRunParametersImpl implements DryRunParameters {

    private long minExecutionTimeMillis;
    private long maxExecutionTimeMillis;
    private long fixedExecutionTimeMillis;
    private double jobErrorPercentage;
    private boolean error;

    @Override
    public long getMinExecutionTimeMillis() {
        return minExecutionTimeMillis;
    }

    @Override
    public void setMinExecutionTimeMillis(long minExecutionTimeMillis) {
        this.minExecutionTimeMillis = minExecutionTimeMillis;
    }

    @Override
    public long getMaxExecutionTimeMillis() {
        return this.maxExecutionTimeMillis;
    }

    @Override
    public void setMaxExecutionTimeMillis(long maxExecutionTimeMillis) {
        this.maxExecutionTimeMillis = maxExecutionTimeMillis;
    }

    @Override
    public long getFixedExecutionTimeMillis() {
        return this.fixedExecutionTimeMillis;
    }

    @Override
    public void setFixedExecutionTimeMillis(long fixedExecutionTimeMillis) {
        this.fixedExecutionTimeMillis = fixedExecutionTimeMillis;
    }

    @Override
    public double getJobErrorPercentage() {
        return this.jobErrorPercentage;
    }

    @Override
    public void setJobErrorPercentage(double jobErrorPercentage) {
        this.jobErrorPercentage = jobErrorPercentage;
    }

    @Override
    public boolean isError() {
        return this.error;
    }

    @Override
    public void setError(boolean error) {
        this.error = error;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("DryRunParametersImpl{");
        sb.append("minExecutionTimeMillis=").append(minExecutionTimeMillis);
        sb.append(", maxExecutionTimeMillis=").append(maxExecutionTimeMillis);
        sb.append(", fixedExecutionTimeMillis=").append(fixedExecutionTimeMillis);
        sb.append(", jobErrorPercentage=").append(jobErrorPercentage);
        sb.append(", error=").append(error);
        sb.append('}');
        return sb.toString();
    }
}
