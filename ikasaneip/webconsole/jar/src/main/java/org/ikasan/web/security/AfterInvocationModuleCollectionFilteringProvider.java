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
package org.ikasan.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.spec.module.Module;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

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
    private static final Logger logger = LoggerFactory.getLogger(AfterInvocationModuleCollectionFilteringProvider.class);

    public Object decide(Authentication authentication, Object object, Collection<ConfigAttribute> config,
            Object returnedObject) throws AccessDeniedException
    {
        // TODO Would be nice to enforce <ConfigAttribute> generic
        Iterator<?> iter = config.iterator();
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

    @Override
    public boolean supports(ConfigAttribute configAttribute)
    {
        return configAttribute.getAttribute().equalsIgnoreCase(AFTER_MODULE_COLLECTION_READ);
    }

}
