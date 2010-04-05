/*
 * $Id$
 * $URL$
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
package org.ikasan.console.security;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.ikasan.framework.module.Module;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;

/**
 * Class for determining access/configuration rights
 * 
 * @author Ikasan Development Team
 */
public class ModuleAfterInvocationProvider extends AbstractModuleAfterInvocationProvider
{
    /** AFTER_MODULE_READ configuration attribute */
    private static final String AFTER_MODULE_READ = "AFTER_MODULE_READ";

    /** Logger for this class */
    private Logger logger = Logger.getLogger(ModuleAfterInvocationProvider.class);

    /** Constructor */
    public ModuleAfterInvocationProvider()
    {
        super(AFTER_MODULE_READ);
    }

    /**
     * Decide whether a user has access to view a certain module
     * 
     * @param authentication - The authentication scheme
     * @param object - Not used!
     * @param config - The configuration attribute to check
     * @param returnedObject - The return object to seed
     * @return A list of authorised objects or 
     * @throws AccessDeniedException - Access was denied 
     */
    public Object decide(Authentication authentication, @SuppressWarnings("unused") Object object, ConfigAttributeDefinition config,
            Object returnedObject) throws AccessDeniedException
    {
        Iterator<?> iter = config.getConfigAttributes().iterator();
        if (returnedObject == null)
        {
            // AclManager interface contract prohibits nulls
            // As they have permission to null/nothing, grant access
            if (this.logger.isDebugEnabled())
            {
                this.logger.debug("Return object is null, skipping");
            }
            return null;
        }
        if (!Module.class.isAssignableFrom(returnedObject.getClass()))
        {
            if (this.logger.isDebugEnabled())
            {
                this.logger.debug("Return object is not a Module, skipping");
            }
            return returnedObject;
        }
        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();
            if (!this.supports(attr))
            {
                continue;
            }
            // Need to make an access decision on this invocation
            if (mayReadModule(authentication, (Module) returnedObject))
            {
                return returnedObject;
            }
            throw new AccessDeniedException("user[" + authentication + "] does not have access to module ["
                    + ((Module) returnedObject).getName() + "]");
        }
        return returnedObject;
    }
}
