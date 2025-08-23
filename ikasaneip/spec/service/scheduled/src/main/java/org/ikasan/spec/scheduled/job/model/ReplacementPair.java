package org.ikasan.spec.scheduled.job.model;

import java.io.Serializable;

public interface ReplacementPair extends Serializable {

    /**
     * Retrieve the replacement token associated with this ReplacementPair.
     *
     * @return The replacement token as a String.
     */
    String getReplacementToken();

    /**
     * Sets the replacement token to be used for this ReplacementPair.
     *
     * @param replacementToken The new replacement token to be set. Must be a non-null String.
     */
    void setReplacementToken(String replacementToken);

    /**
     * Retrieve the job plan parameter name associated with this object.
     *
     * @return The job plan parameter as a String.
     */
    String getJobPlanParameterName();

    /**
     * Sets the job plan parameter name for this object.
     *
     * @param jobPlanParameterName The name of the job plan parameter to be set. Must be a non-null String.
     */
    void setJobPlanParameterName(String jobPlanParameterName);
}
