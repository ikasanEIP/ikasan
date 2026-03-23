package org.ikasan.security.model;

import org.ikasan.spec.security.model.JobPlanGrantedAuthority;

public class HibernateJobPlanGrantedAuthorityImpl implements JobPlanGrantedAuthority {
    private static final String MODULE_PREFIX = "JOB_PLAN:";
    private String moduleName;

    public HibernateJobPlanGrantedAuthorityImpl(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getAuthority() {
        return MODULE_PREFIX+moduleName;
    }
}
