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
package org.ikasan.console.security;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.log4j.Logger;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfile;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthorizationServiceException;
import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;

/**
 * Class for determining access/configuration rights for collection of
 * configuration
 * 
 * @author Ikasan Development Team
 */
public class AfterInvocationPointToPointFlowProfileCollectionFilteringProvider extends AbstractPointToPointFlowProfileAfterInvocationProvider
{
    /** AFTER_POINT_TO_POINT_FLOW_PROFILE_COLLECTION_READ */
    private static final String AFTER_POINT_TO_POINT_FLOW_PROFILE_COLLECTION_READ = "AFTER_POINT_TO_POINT_FLOW_PROFILE_COLLECTION_READ";

    /** Constructor */
    public AfterInvocationPointToPointFlowProfileCollectionFilteringProvider()
    {
        super(AFTER_POINT_TO_POINT_FLOW_PROFILE_COLLECTION_READ);
    }

    /** Logger for this class */
    Logger logger = Logger.getLogger(AfterInvocationModuleCollectionFilteringProvider.class);

    /**
     * Decide if the user has rights to invoke actions on a PointToPointFlowProfile
     * 
     * @param authentication - The authentication scheme
     * @param object - TODO Not used!
     * @param config - TODO The configuration attribute to check
     * @param returnedObject - TODO The return object to seed
     * @return TODO A list of authorised objects or
     * @throws AccessDeniedException - Access was denied
     */
    public Object decide(Authentication authentication, Object object, ConfigAttributeDefinition config, Object returnedObject)
            throws AccessDeniedException
    {
        Iterator<?> iter = config.getConfigAttributes().iterator();
        while (iter.hasNext())
        {
            ConfigAttribute attr = (ConfigAttribute) iter.next();
            if (!this.supports(attr))
            {
                continue;
            }
            // Need to process the Collection for this invocation
            if (!(returnedObject instanceof Collection<?>))
            {
                throw new AuthorizationServiceException("A Collection was required as the " + "returnedObject, but the returnedObject was [" + returnedObject
                        + "]");
            }
            // Locate unauthorised Collection elements
            Collection<PointToPointFlowProfile> authorisedPointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
            Iterator<?> collectionIter = ((Collection<?>) returnedObject).iterator();
            while (collectionIter.hasNext())
            {
                Object domainObject = collectionIter.next();
                // Ignore nulls or entries which aren't instances of the
                // configured domain object class
                if (domainObject == null || !PointToPointFlowProfile.class.isAssignableFrom(domainObject.getClass()))
                {
                    continue;
                }
                PointToPointFlowProfile thisPointToPointFlowProfile = (PointToPointFlowProfile) domainObject;
                if (mayReadPointToPointFlowProfile(authentication, thisPointToPointFlowProfile))
                {
                    authorisedPointToPointFlowProfiles.add(thisPointToPointFlowProfile);
                }
            }
            return authorisedPointToPointFlowProfiles;
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
        return configAttribute.getAttribute().equalsIgnoreCase(AFTER_POINT_TO_POINT_FLOW_PROFILE_COLLECTION_READ);
    }
}
