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

import org.springframework.security.core.GrantedAuthority;

import java.util.Date;

/**
 * Represents a security policy in the Ikasan security framework.
 *
 * <p>A policy defines a specific permission or set of permissions that can be granted to a {@link Role}.
 * Policies extend Spring Security's {@link GrantedAuthority} interface and can be linked to specific
 * resources (modules, flows, job plans) through {@link PolicyLink} associations to provide fine-grained
 * access control.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface Policy extends GrantedAuthority, Comparable<Policy>
{
    /**
     * Retrieves the unique identifier of this policy.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Long getId();

    /**
     * Sets the unique identifier of this policy.
     *
     * @param id the unique identifier to set
     */
    void setId(Long id);

    /**
     * Retrieves the name of this policy.
     *
     * @return the policy name, never {@code null}
     */
    String getName();

    /**
     * Sets the name of this policy.
     *
     * @param name the name to set, must not be {@code null}
     */
    void setName(String name);

    /**
     * Retrieves the date and time when this policy was created.
     *
     * @return the creation timestamp, or {@code null} if not set
     */
    Date getCreatedDateTime();

    /**
     * Sets the date and time when this policy was created.
     *
     * @param createdDateTime the creation timestamp to set
     */
    void setCreatedDateTime(Date createdDateTime);

    /**
     * Retrieves the date and time when this policy was last updated.
     *
     * @return the last update timestamp, or {@code null} if not set
     */
    Date getUpdatedDateTime();

    /**
     * Sets the date and time when this policy was last updated.
     *
     * @param updatedDateTime the last update timestamp to set
     */
    void setUpdatedDateTime(Date updatedDateTime);

    /**
     * Retrieves the human-readable description of this policy.
     *
     * @return the description, or {@code null} if not set
     */
    String getDescription();

    /**
     * Sets the human-readable description of this policy.
     *
     * @param description the description to set, may be {@code null}
     */
    void setDescription(String description);

    /**
     * Retrieves the authority string representation of this policy.
     *
     * <p>This method is required by the {@link GrantedAuthority} interface and typically
     * returns the policy name prefixed with an appropriate authority prefix.
     *
     * @return the authority string, never {@code null}
     */
    @Override
    String getAuthority();

    /**
     * Retrieves the policy link that associates this policy with a specific resource.
     *
     * @return the {@link PolicyLink} for this policy, or {@code null} if not linked to a specific resource
     */
    PolicyLink getPolicyLink();

    /**
     * Sets the policy link that associates this policy with a specific resource.
     *
     * @param policyLink the {@link PolicyLink} to set, may be {@code null} for general policies
     */
    void setPolicyLink(PolicyLink policyLink);

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    int hashCode();

    @Override
    boolean equals(Object obj);

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    String toString();

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    int compareTo(Policy policy);
}
