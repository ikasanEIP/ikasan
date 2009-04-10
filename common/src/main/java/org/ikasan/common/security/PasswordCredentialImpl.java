/*
 * $Id: PasswordCredentialImpl.java 16606 2009-04-09 08:07:33Z mitcje $
 * $URL: svn+ssh://svc-vcsp/architecture/ikasan/trunk/common/src/main/java/org/ikasan/common/security/PasswordCredentialImpl.java $
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

// Imported commons classes
import java.util.HashMap;
import java.util.Map;

/**
 * This class represents the '<code>PasswordCredentialImpl</code>', a simple class
 * for providing username and password credentials.
 * 
 * @author <a href="mailto:info@ikasan.org">Madhu Konda</a>
 */
public class PasswordCredentialImpl
    implements IkasanPasswordCredential
{
    /** user name credential */
    private String username;
    /** password credential */
    private String password;
    
    /** 
     * Constructor 
     * @param options 
     */
    public PasswordCredentialImpl(final Map<String,String> options)
    {
        // TODO - is this worth xstreaming?
        this.username = options.get(IkasanPasswordCredentialConst.USERNAME_LITERAL);
        this.password = options.get(IkasanPasswordCredentialConst.PASSWORD_LITERAL);
    }
    
    /**
     * @return the username
     */
    public String getUsername()
    {
        return this.username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(final String username)
    {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword()
    {
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(final String password)
    {
        this.password = password;
    }

    /**
     * @return the IPC instance as a map
     */
    public Map<String, String> toMap()
    {
        
        Map<String, String> map = new HashMap<String, String>();
        map.put(IkasanPasswordCredentialConst.USERNAME_LITERAL, this.getUsername());
        map.put(IkasanPasswordCredentialConst.PASSWORD_LITERAL, this.getPassword());
        return map;
    }

}
