package org.ikasan.security.model;

import org.ikasan.spec.security.model.ModuleGrantedAuthority;

public class ModuleGrantedAuthorityImpl implements ModuleGrantedAuthority {

    private static final String MODULE_PREFIX = "MODULE:";

    private final String authority;

    /**
     * Constructs a new instance of ModuleGrantedAuthorityImpl with the specified authority.
     *
     * @param authority the authority string representing specific module access control.
     *                  This value typically defines permissions associated with modules.
     */
    public ModuleGrantedAuthorityImpl(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return MODULE_PREFIX+this.authority;
    }
}
