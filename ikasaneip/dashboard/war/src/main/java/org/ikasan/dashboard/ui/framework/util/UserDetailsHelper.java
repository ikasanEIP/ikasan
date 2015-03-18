/*
 * $Id: UserDetailsHelper.java 44073 2015-03-17 10:38:20Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationUI/war/src/main/java/org/ikasan/mapping/configuration/ui/util/UserDetailsHelper.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.dashboard.ui.framework.util;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author CMI2 Development Team
 *
 */
public class UserDetailsHelper
{
    private UserDetails userDetails;

    /**
     * @return the userDetails
     */
    public UserDetails getUserDetails()
    {
        return userDetails;
    }

    /**
     * @param userDetails the userDetails to set
     */
    public void setUserDetails(UserDetails userDetails)
    {
        this.userDetails = userDetails;
    }
}
