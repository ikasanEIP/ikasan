package org.ikasan.job.orchestration.model.job;

import org.ikasan.spec.scheduled.job.model.ReplacementPair;

public class ReplacementPairImpl implements ReplacementPair {
    private String replacementToken;
    private String jobPlanParameterName;

    @Override
    public String getReplacementToken() {
        return replacementToken;
    }

    @Override
    public void setReplacementToken(String replacementToken) {
        this.replacementToken = replacementToken;
    }

    @Override
    public String getJobPlanParameterName() {
        return jobPlanParameterName;
    }

    @Override
    public void setJobPlanParameterName(String jobPlanParameterName) {
        this.jobPlanParameterName = jobPlanParameterName;
    }

    @Override
    public int hashCode() {
        return getReplacementToken().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof org.ikasan.spec.scheduled.job.model.ReplacementPair)) {
            return false;
        }
        org.ikasan.spec.scheduled.job.model.ReplacementPair other = (org.ikasan.spec.scheduled.job.model.ReplacementPair) obj;
        return replacementToken == other.getReplacementToken();
    }
}
