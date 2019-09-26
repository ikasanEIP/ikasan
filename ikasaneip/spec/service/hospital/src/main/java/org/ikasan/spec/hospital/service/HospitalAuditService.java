package org.ikasan.spec.hospital.service;

import org.ikasan.spec.hospital.model.ExclusionEventAction;

import java.util.List;

public interface HospitalAuditService
{
    /**
     * Save an ExclusionEventAction
     * @param exclusionEventAction
     */
    public void save(ExclusionEventAction exclusionEventAction);

    /**
     * Save a list of ExclusionEventAction
     * @param exclusionEventActions
     */
    public void save(List<ExclusionEventAction> exclusionEventActions);

}
