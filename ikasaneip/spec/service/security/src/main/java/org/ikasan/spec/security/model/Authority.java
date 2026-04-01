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

/**
 * Represents a security authority within the Ikasan security framework.
 *
 * <p>Extends Spring Security's {@link GrantedAuthority} interface to provide additional
 * properties suitable for ORM persistence, including a unique identifier and description.
 * Authorities represent specific permissions that can be granted to users and principals
 * for fine-grained access control.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface Authority extends GrantedAuthority
{
    /**
     * Retrieves the human-readable description of this authority.
     *
     * @return the description of the authority, or {@code null} if not set
     */
    String getDescription();

    /**
     * Sets the human-readable description of this authority.
     *
     * @param description the description to set, may be {@code null}
     */
    void setDescription(String description);

    /**
     * Retrieves the unique identifier of this authority.
     *
     * @return the unique identifier, or {@code null} if not yet persisted
     */
    Object getId();

    /**
     * Sets the authority string that defines the permission.
     *
     * @param authority the authority string to set, must not be {@code null}
     */
    void setAuthority(String authority);
}
