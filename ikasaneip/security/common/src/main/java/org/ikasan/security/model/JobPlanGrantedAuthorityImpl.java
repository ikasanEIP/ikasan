package org.ikasan.security.model;

import org.ikasan.spec.security.model.JobPlanGrantedAuthority;

public class JobPlanGrantedAuthorityImpl implements JobPlanGrantedAuthority {

    private static final String JOB_PLAN_PREFIX = "JOB_PLAN:";

    private final String authority;

    /**
     * Constructs a new instance of JobPlanGrantedAuthorityImpl with the specified authority.
     *
     * @param authority the authority string representing specific job plan access control.
     *                  This value typically defines permissions associated with job plans.
     */
    public JobPlanGrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return JOB_PLAN_PREFIX+this.authority;
    }
}
