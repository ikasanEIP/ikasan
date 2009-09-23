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
package org.ikasan.framework.security;

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

    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config,
            Object returnedObject) throws AccessDeniedException
    {
        Iterator<?> iter = config.getConfigAttributes().iterator();
        if (returnedObject == null)
        {
            // AclManager interface contract prohibits nulls
            // As they have permission to null/nothing, grant access
            if (logger.isDebugEnabled())
            {
                logger.debug("Return object is null, skipping");
            }
            return null;
        }
        if (!Module.class.isAssignableFrom(returnedObject.getClass()))
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Return object is not a Module, skipping");
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
