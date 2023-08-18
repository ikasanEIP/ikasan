package org.ikasan.security.model;

import org.springframework.security.core.GrantedAuthority;

public class JobPlanGrantedAuthority implements GrantedAuthority {
    private static final String MODULE_PREFIX = "JOB_PLAN:";
    private String moduleName;

    public JobPlanGrantedAuthority(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getAuthority() {
        return MODULE_PREFIX+moduleName;
    }
}
