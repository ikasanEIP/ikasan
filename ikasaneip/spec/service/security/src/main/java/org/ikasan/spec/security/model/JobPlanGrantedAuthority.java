package org.ikasan.spec.security.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a granted authority specifically for job plan access control.
 *
 * <p>Extends Spring Security's {@link GrantedAuthority} to provide job plan-specific
 * authorization capabilities within the Ikasan security framework. Job plan authorities
 * define permissions for accessing and managing scheduled job plans.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface JobPlanGrantedAuthority extends GrantedAuthority
{
    String getAuthority();
}
