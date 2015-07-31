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
package org.ikasan.dashboard.ui.framework.data;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.constants.SecurityConstants;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.DashboardSessionValueConstants;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.ikasan.security.service.authentication.IkasanAuthentication;
import org.springframework.security.core.Authentication;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;

/**
 * @author Ikasan Development Team
 *
 */
public class LoginFieldGroup extends FieldGroup
{
    private static final long serialVersionUID = 4872295004933189641L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(LoginFieldGroup.class);
    

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private AuthenticationService authenticationService;
    private VisibilityGroup visibilityGroup;

    /**
     * Constructor
     * 
     * @param visibilityGroup
     * @param userService
     * @param authProvider
     */
    public LoginFieldGroup(VisibilityGroup visibilityGroup,
    		AuthenticationService authenticationService)
    {
        super();
        this.visibilityGroup = visibilityGroup;
        this.authenticationService = authenticationService;
    }

   /**
    * Constructor
    * 
    * @param itemDataSource
    * @param visibilityGroup
    * @param userService
    * @param authProvider
    */
    public LoginFieldGroup(Item itemDataSource, VisibilityGroup visibilityGroup,
    		AuthenticationService authenticationService)
    {
        super(itemDataSource);
        this.visibilityGroup = visibilityGroup;
        this.authenticationService = authenticationService;
    }

    /* (non-Javadoc)
     * @see com.vaadin.data.fieldgroup.FieldGroup#commit()
     */
    @Override
    public void commit() throws CommitException
    {
        Field<String> username = (Field<String>) this.getField(USERNAME);
        Field<String> password = (Field<String>) this.getField(PASSWORD);

        try
        {
            logger.info("Attempting to validate user: " + username.getValue());

            Authentication authentication = authenticationService.login(username.getValue(), password.getValue());

            logger.info("Loaded authentication: " + authentication);
            
            VaadinService.getCurrentRequest().getWrappedSession()
                .setAttribute(DashboardSessionValueConstants.USER, authentication);
            
            this.visibilityGroup.setVisible();
        }
        catch (AuthenticationServiceException e)
        {
        	e.printStackTrace();
            logger.info("User has supplied invalid password: " + username.getValue());
            throw new CommitException("Invalid user name or password. Please try again.");
        }
    }

}
