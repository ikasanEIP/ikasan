/*
 * $Id: WiretapSearchCriteria.java 2551 2009-10-21 11:22:10Z karianna $
 * $URL: https://open.jira.com/svn/IKASAN/branches/console-redesign/ikasaneip/console/src/main/java/org/ikasan/console/web/command/WiretapSearchCriteria.java $
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
package org.ikasan.console.web.command;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import org.apache.log4j.Logger;
import org.ikasan.console.web.controller.MasterDetailControllerUtil;
import org.ikasan.framework.security.model.User;

/**
 * Command class capturing the User criteria fields
 * 
 * @author Ikasan Development Team
 */
public class UserCriteria implements Serializable
{
    /** serialVersionUID */
    private static final long serialVersionUID = 3595514737829632181L;

    /** The logger */
    private Logger logger = Logger.getLogger(UserCriteria.class);

    /** Username */
    private String username;

    /** Password */
    private String password;

    /** Constructor */
    public UserCriteria(String username, String password)
    {
        this.username = MasterDetailControllerUtil.nullForEmpty(username);
        this.password = MasterDetailControllerUtil.nullForEmpty(password);
    }

    /**
     * Get the serial uid
     * 
     * @return serial uid
     */
    public static long getSerialVersionUID()
    {
        return serialVersionUID;
    }

    /**
     * Get the username
     * 
     * @return username
     */
    public String getUsername()
    {
        return this.username;
    }
    
    /**
     * Get the password
     * 
     * @return password
     */
    public String getPassword()
    {
        return this.password;
    }

}
