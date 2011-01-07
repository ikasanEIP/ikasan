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
package org.ikasan.console.service;

import org.apache.log4j.Logger;
import org.ikasan.framework.security.model.User;

/**
 * Default implementation of <code>ConsoleService</code>
 * 
 * @author Ikasan Development Team
 */
public class ConsoleServiceImpl implements ConsoleService
{
    /** The logger instance */
    private static final Logger logger = Logger.getLogger(ConsoleServiceImpl.class);
    
    /** The email notifier for this service to use */
    ConsoleEmailNotifier consoleEmailNotifier = null;

    /**
     * Constructor
     * 
     * @param consoleEmailNotifier - The email notofier to use
     */
    public ConsoleServiceImpl(ConsoleEmailNotifier consoleEmailNotifier)
    {
        super();
        this.consoleEmailNotifier = consoleEmailNotifier;
    }

    /**
     * An implementation of sendNewPassword, in this case we're sending an email to them
     * 
     * TODO More specific error handling than catching Exception required 
     * 
     * @param user The user to send the email to  
     * @throws IllegalArgumentException  - If the user has no valid email
     */
    public void sendNewPassword(User user) throws IllegalArgumentException
    {
        consoleEmailNotifier.setTo(user.getEmail());
        consoleEmailNotifier.setSubject("Ikasan EIP - New Password");
        consoleEmailNotifier.setBody("Hi " + user.getUsername() + ",\n" +
        		"\n" +
        		"You have requested a password reset.\n" +
        		"\n" +
        		"Your new password is: password\n" +
        		"\n" +
        		"Please ensure that you login and change your password as soon as possible under the 'My Account' menu\n" +
        		"\n" +
        		"Thanks,\n" +
        		"The Ikasan EIP Administrators.");
        try
        {
            consoleEmailNotifier.sendMail();
        }
        catch (Exception e)
        {
            logger.error("Failed to send the email for user [" + user.getUsername() + "] to their address at [" + user.getEmail() + "]");
            throw new IllegalArgumentException(e.getMessage(), e); 
        }
    }
    
}
