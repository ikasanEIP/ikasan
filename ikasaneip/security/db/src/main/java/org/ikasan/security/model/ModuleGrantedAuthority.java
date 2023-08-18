package org.ikasan.security.model;

import org.springframework.security.core.GrantedAuthority;

public class ModuleGrantedAuthority implements GrantedAuthority {
    private static final String MODULE_PREFIX = "MODULE:";
    private String moduleName;

    public ModuleGrantedAuthority(String moduleName) {
        this.moduleName = moduleName;
    }

    @Override
    public String getAuthority() {
        return MODULE_PREFIX+moduleName;
    }
}
