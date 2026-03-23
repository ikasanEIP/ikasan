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
package org.ikasan.spec.security.dao;

import java.util.List;

import org.ikasan.spec.security.model.Authority;

/**
 * Data Access Object interface for managing {@link Authority} instances in persistent storage.
 *
 * <p>Provides operations to retrieve, save, and query authorities within the Ikasan security framework.
 * Authorities represent granted permissions that can be assigned to users and principals for
 * access control purposes.
 *
 * @author Ikasan Development Team
 * @since 1.0
 */
public interface AuthorityDao
{
    /**
     * Retrieves all authorities registered in the system.
     *
     * @return a list of all {@link Authority} instances, or an empty list if none exist
     */
    List<Authority> getAuthorities();

    /**
     * Retrieves a specific authority by its name.
     *
     * @param authority the name of the authority to retrieve, must not be {@code null}
     * @return the {@link Authority} instance with the specified name, or {@code null} if no such authority exists
     */
    Authority getAuthority(String authority);

    /**
     * Persists a new authority to the data store.
     *
     * @param newAuthority the {@link Authority} instance to save, must not be {@code null}
     */
    void save(Authority newAuthority);
}
