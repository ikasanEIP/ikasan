 /*
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 *
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing
 * of individual contributors are as shown in the packaged copyright.txt
 * file.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.spec.security.model;

 import java.security.Principal;
 import java.util.Date;
 import java.util.Set;

 /**
 * Represents a security principal in the Ikasan security framework.
 *
 * <p>A principal is a security entity (such as a user group, application, or service account)
 * that can be authenticated and authorized. Principals can be assigned multiple {@link Role}s
 * to grant them specific permissions within the system. This interface extends Java's
 * {@link Principal} interface to provide additional properties and role management capabilities.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface IkasanPrincipal extends Principal
{
    /**
     * Retrieves the unique identifier of this principal.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Sets the unique identifier of this principal.
     *
     * @param id the unique identifier to set
     */
    void setId(Object id);

    /**
     * Retrieves the name of this principal.
     *
     * @return the principal name, never {@code null}
     */
    String getName();

    /**
     * Sets the name of this principal.
     *
     * @param name the name to set, must not be {@code null}
     */
    void setName(String name);

    /**
     * Retrieves the type of this principal (e.g., "user", "group", "application").
     *
     * @return the principal type, or {@code null} if not set
     */
    String getType();

    /**
     * Sets the type of this principal.
     *
     * @param type the type to set (e.g., "user", "group", "application")
     */
    void setType(String type);

    /**
     * Retrieves the date and time when this principal was created.
     *
     * @return the creation timestamp, or {@code null} if not set
     */
    Date getCreatedDateTime();

    /**
     * Sets the date and time when this principal was created.
     *
     * @param createdDateTime the creation timestamp to set
     */
    void setCreatedDateTime(Date createdDateTime);

    /**
     * Retrieves the date and time when this principal was last updated.
     *
     * @return the last update timestamp, or {@code null} if not set
     */
    Date getUpdatedDateTime();

    /**
     * Sets the date and time when this principal was last updated.
     *
     * @param updatedDateTime the last update timestamp to set
     */
    void setUpdatedDateTime(Date updatedDateTime);

    /**
     * Retrieves the set of roles assigned to this principal.
     *
     * @return a set of {@link Role} instances assigned to this principal, never {@code null}
     */
    Set<Role> getRoles();

    /**
     * Sets the roles assigned to this principal.
     *
     * @param roles the set of {@link Role} instances to assign, must not be {@code null}
     */
    void setRoles(Set<Role> roles);

    /**
     * Retrieves the human-readable description of this principal.
     *
     * @return the description, or {@code null} if not set
     */
    String getDescription();

    /**
     * Sets the human-readable description of this principal.
     *
     * @param description the description to set, may be {@code null}
     */
    void setDescription(String description);

    /**
     * Retrieves the LDAP base distinguished name for application security groups.
     *
     * <p>Used when this principal is synchronized from an LDAP directory to identify
     * the base DN where security groups are located.
     *
     * @return the application security base DN, or {@code null} if not applicable
     */
    String getApplicationSecurityBaseDn();

    /**
     * Sets the LDAP base distinguished name for application security groups.
     *
     * @param applicationSecurityBaseDn the application security base DN to set
     */
    void setApplicationSecurityBaseDn(String applicationSecurityBaseDn);

    /**
     * Adds a role to this principal's set of roles.
     *
     * @param role the {@link Role} to add, must not be {@code null}
     */
    void addRole(Role role);
}
