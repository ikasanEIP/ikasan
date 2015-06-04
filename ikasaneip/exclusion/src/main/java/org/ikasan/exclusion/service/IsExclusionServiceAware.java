package org.ikasan.exclusion.service;

import org.ikasan.spec.exclusion.ExclusionService;

/**
 * Created by elliga on 03/06/2015.
 */
public interface IsExclusionServiceAware {

    /**
     * Allow the ExclusionService to be set at runtime for a component implementing this contract
     * @param exclusionService
     */
    public void setExclusionService(ExclusionService exclusionService);
}
