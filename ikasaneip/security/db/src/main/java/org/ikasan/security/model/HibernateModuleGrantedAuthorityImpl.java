package org.ikasan.security.model;

import org.ikasan.spec.security.model.ModuleGrantedAuthority;

public class HibernateModuleGrantedAuthorityImpl implements ModuleGrantedAuthority {
    private static final String MODULE_PREFIX = "MODULE:";
    private String moduleName;

    public HibernateModuleGrantedAuthorityImpl(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getAuthority() {
        return MODULE_PREFIX+moduleName;
    }
}
