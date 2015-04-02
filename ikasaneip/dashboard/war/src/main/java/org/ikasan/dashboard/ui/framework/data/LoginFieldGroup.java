/*
 * $Id: LoginFieldGroup.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/data/LoginFieldGroup.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.data;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.ui.framework.group.VisibilityGroup;
import org.ikasan.dashboard.ui.framework.util.UserDetailsHelper;
import org.ikasan.dashboard.ui.mappingconfiguration.util.MappingConfigurationUISessionValueConstants;
import org.ikasan.security.model.IkasanPrincipal;
import org.ikasan.security.model.Policy;
import org.ikasan.security.model.Role;
import org.ikasan.security.model.User;
import org.ikasan.security.service.AuthenticationService;
import org.ikasan.security.service.AuthenticationServiceException;
import org.ikasan.security.service.UserService;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Field;

/**
 * @author CMI2 Development Team
 *
 */
public class LoginFieldGroup extends FieldGroup
{
    private static final long serialVersionUID = 4872295004933189641L;

    /** Logger instance */
    private static Logger logger = Logger.getLogger(LoginFieldGroup.class);
    
    private static final String ADMIN_ROLE = "ROLE_ADMIN";

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    private AuthenticationService authenticationService;
    private UserService userService;
    private VisibilityGroup visibilityGroup;
    private UserDetailsHelper userDetailsHelper;

    /**
     * Constructor
     * 
     * @param visibilityGroup
     * @param userService
     * @param authProvider
     * @param userDetailsHelper
     */
    public LoginFieldGroup(VisibilityGroup visibilityGroup, UserService userService,
    		AuthenticationService authenticationService, UserDetailsHelper userDetailsHelper)
    {
        super();
        this.visibilityGroup = visibilityGroup;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userDetailsHelper = userDetailsHelper;
    }

   /**
    * Constructor
    * 
    * @param itemDataSource
    * @param visibilityGroup
    * @param userService
    * @param authProvider
    * @param userDetailsHelper
    */
    public LoginFieldGroup(Item itemDataSource, VisibilityGroup visibilityGroup, UserService userService,
    		AuthenticationService authenticationService, UserDetailsHelper userDetailsHelper)
    {
        super(itemDataSource);
        this.visibilityGroup = visibilityGroup;
        this.userService = userService;
        this.authenticationService = authenticationService;
        this.userDetailsHelper = userDetailsHelper;
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

            IkasanPrincipal principal = authenticationService.login(username.getValue(), password.getValue());

            logger.info("Loaded principal: " + principal);
            
            if(principal != null)
            {
	            for(Role role: principal.getRoles())
	            {
	                logger.info("Loaded role: " + principal);
	
	                for(Policy policy: role.getPolicies())
	                {
	                    logger.info("Loaded policy: " + policy);
	                }
	            }
            }
            
            User userDetails = userService.loadUserByUsername("admin");

            this.userDetailsHelper.setUserDetails(userDetails);

            if(isAdminUser(userDetails))
            {
                this.visibilityGroup.setVisible(true);
            }

            VaadinService.getCurrentRequest().getWrappedSession()
                .setAttribute(MappingConfigurationUISessionValueConstants.USER, this.userDetailsHelper);
        }
        catch (AuthenticationServiceException e)
        {
        	e.printStackTrace();
            logger.info("User has supplied invalid password: " + username.getValue());
            throw new CommitException("Invalid user name or password. Please try again.");
        }
    }

    /**
     * Method to determine id the user is an admin user.
     * 
     * @param userDetails
     * @return
     */
    private boolean isAdminUser(User userDetails)
    {
        return true;
    }
}
