/*
 * $Id: IkasanPasswordCredential.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/IkasanPasswordCredential.java $
 * 
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * Copyright (c) 2003-2008 Mizuho International plc. and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the 
 * Free Software Foundation Europe e.V. Talstrasse 110, 40217 Dusseldorf, Germany 
 * or see the FSF site: http://www.fsfeurope.org/.
 * ====================================================================
 */
package org.ikasan.common.security;

import java.util.Map;

/**
 * Interface for defining Ikasan Password Credential operations.
 * 
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 */
public interface IkasanPasswordCredential
{
    /**
     * @return the username
     */
    String getUsername();

    /**
     * @param username the username to set
     */
    void setUsername(final String username);

    /**
     * @return the password
     */
    String getPassword();

    /**
     * @param password the password to set
     */
    void setPassword(final String password);

    /**
     * @return returns the IkasanPasswordCredential instance as a map
     */
    Map<String,String> toMap();
}
