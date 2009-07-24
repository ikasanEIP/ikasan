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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.ikasan.framework.module.Module;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthorizationServiceException;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;

/**
 * Class for determining access/configuration rights for collection of configuration
 *  
 * @author Ikasan Development Team
 */
public class AfterInvocationModuleCollectionFilteringProvider extends AbstractModuleAfterInvocationProvider
{
    /** AFTER_MODULE_COLLECTION_READ */
    private static final String AFTER_MODULE_COLLECTION_READ = "AFTER_MODULE_COLLECTION_READ";

    /** Constructor */
    public AfterInvocationModuleCollectionFilteringProvider()
    {
        super(AFTER_MODULE_COLLECTION_READ);
    }

    /** Logger for this class */
    Logger logger = Logger.getLogger(AfterInvocationModuleCollectionFilteringProvider.class);

    /**
     * TODO comment properly - Decide if the AFTER_MODULE_COLLECTION_READ should be authenticated
     * 
     * @param authentication - The authentication scheme
     * @param object - The object to authenticate, not used!
     * @param config - The configuration attribute to check
     * @param returnedObject - The return object to seed
     * @return A list of authorised objects or 
     * @throws AccessDeniedException - Access was denied 
     */
    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Object returnedObject) throws AccessDeniedException
    {
        // TODO Would be nice to enforce <ConfigAttribute> generic
        Iterator<?> iter = config.getConfigAttributes().iterator();
        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute)iter.next();
            if (!this.supports(attr))
            {
                continue;
            }
            // Need to process the Collection for this invocation
            if (!(returnedObject instanceof Collection))
            {
                throw new AuthorizationServiceException("A Collection was required as the "
                        + "returnedObject, but the returnedObject was [" + returnedObject + "]");
            }
            // Locate unauthorised Collection elements
            Collection<Module> authorisedModules = new ArrayList<Module>();
            Iterator<?> collectionIter = ((Collection<?>) returnedObject).iterator();
            while (collectionIter.hasNext())
            {
                Object domainObject = collectionIter.next();
                // Ignore nulls or entries which aren't instances of the configured domain object class
                if (domainObject == null || !Module.class.isAssignableFrom(domainObject.getClass()))
                {
                    continue;
                }
                Module thisModule = (Module) domainObject;
                if (mayReadModule(authentication, thisModule))
                {
                    authorisedModules.add(thisModule);
                }
            }
            return authorisedModules;
        }
        return returnedObject;
    }

    /**
     * Returns true if the configuration attribute is supported by this provider
     *  
     * @param configAttribute configuration attribute to test 
     * @return true if the configuration attribute is supported by this provider
     */
    @Override
    public boolean supports(ConfigAttribute configAttribute)
    {
        return configAttribute.getAttribute().equalsIgnoreCase(AFTER_MODULE_COLLECTION_READ);
    }
}
