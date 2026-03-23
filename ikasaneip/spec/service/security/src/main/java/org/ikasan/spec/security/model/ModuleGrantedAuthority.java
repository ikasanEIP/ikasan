package org.ikasan.spec.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a granted authority specifically for module access control.
 *
 * <p>Extends Spring Security's {@link GrantedAuthority} to provide module-specific
 * authorization capabilities within the Ikasan security framework. Module authorities
 * define permissions for accessing and managing integration modules and their components.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface ModuleGrantedAuthority extends GrantedAuthority
{
    String getAuthority();
}
